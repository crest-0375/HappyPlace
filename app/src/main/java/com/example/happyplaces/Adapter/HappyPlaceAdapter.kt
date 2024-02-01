package com.example.happyplaces.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.Activities.AddHappyPlace
import com.example.happyplaces.Models.HappyPlaceModel
import com.example.happyplaces.databinding.ItemHappyPlaceListBinding

open class HappyPlaceAdapter(
    private var context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemHappyPlaceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setOnclickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.ivPlaceImage.setImageURI(Uri.parse(model.image))
            holder.tvTitle.text = model.title
            holder.tvDescription.text = model.description

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    private class MyViewHolder(binding: ItemHappyPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var ivPlaceImage = binding.ivPlaceImage
        var tvTitle = binding.tvTitle
        var tvDescription = binding.tvDescription
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddHappyPlace::class.java)
        intent.putExtra("EXTRA_PLACE_DETAILS", list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }
}