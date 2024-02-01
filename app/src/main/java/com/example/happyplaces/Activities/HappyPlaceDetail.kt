package com.example.happyplaces.Activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.Models.HappyPlaceModel
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetail : AppCompatActivity() {
    private var binding: ActivityHappyPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var happyPlaceModel: HappyPlaceModel? = null
        if (intent.hasExtra("EXTRA_PLACE_DETAILS")) {
            happyPlaceModel = intent.getSerializableExtra("EXTRA_PLACE_DETAILS") as HappyPlaceModel
        }
        setSupportActionBar(binding?.toolbarHappyPlaceDetail)

        if (supportActionBar != null && happyPlaceModel != null) {
            supportActionBar?.title = happyPlaceModel.title
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceModel.image))
            binding?.tvDescription?.text = happyPlaceModel.description
            binding?.tvLocation?.text = happyPlaceModel.location
        }
        binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
            onBackPressed()
        }


    }
}