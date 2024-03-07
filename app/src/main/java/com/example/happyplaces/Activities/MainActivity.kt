package com.example.happyplaces.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.Adapter.HappyPlaceAdapter
import com.example.happyplaces.Database.DatabaseHandler
import com.example.happyplaces.Models.HappyPlaceModel
import com.example.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.utils.SwipeToDeleteCallback
import pl.kitek.rvswipetodelete.SwipeToEditCallback

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private lateinit var getHappyPlaceList: ArrayList<HappyPlaceModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabHappyPlaces?.setOnClickListener {
            val intent = Intent(this, AddHappyPlace::class.java)
            startActivity(intent)
        }

        getHappyPlaceFromLocalDB()

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvHappyPlacesList?.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, 302)
            }
        }
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvHappyPlacesList?.adapter as HappyPlaceAdapter
                Log.d("TAG", getHappyPlaceList[viewHolder.adapterPosition].id.toString())

                val result =
                    DatabaseHandler(this@MainActivity).deleteHappyPlace(getHappyPlaceList[viewHolder.adapterPosition].id)
                if (result > 0) {
                    getHappyPlaceList.removeAt(viewHolder.adapterPosition)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }

        }

        val deleteItemHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemHelper.attachToRecyclerView(binding?.rvHappyPlacesList)

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlacesList)
    }

    override fun onRestart() {
        getHappyPlaceFromLocalDB()
        super.onRestart()
    }

    private fun getHappyPlaceFromLocalDB() {
        val dbHandler = DatabaseHandler(this)
        getHappyPlaceList = dbHandler.getHappyPlacesList()
        if (getHappyPlaceList.isNotEmpty()) {
            val adapter = HappyPlaceAdapter(this, getHappyPlaceList)
            binding?.rvHappyPlacesList?.adapter = adapter
            adapter.setOnclickListener(object : HappyPlaceAdapter.OnClickListener {
                override fun onClick(position: Int, model: HappyPlaceModel) {
                    val intent = Intent(this@MainActivity, HappyPlaceDetail::class.java)
                    intent.putExtra("EXTRA_PLACE_DETAILS", model)
                    startActivity(intent)
                }
            })
        }
    }
}