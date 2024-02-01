package com.example.happyplaces.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.Database.DatabaseHandler
import com.example.happyplaces.Models.HappyPlaceModel
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHappyPlace : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLongitude: Double = 0.0
    private var mLatitude: Double = 0.0

    private var mHappyPlaceDetails: HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarAddPlace)
        if (supportActionBar != null) {
            supportActionBar?.title = "Add Happy place"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra("EXTRA_PLACE_DETAILS")) {
            mHappyPlaceDetails =
                intent.getSerializableExtra("EXTRA_PLACE_DETAILS") as HappyPlaceModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()

        if (mHappyPlaceDetails != null) {
            supportActionBar?.title = "Edit Happy place"
            binding?.btnSave?.text = "Update"
            binding?.etTitle?.setText(mHappyPlaceDetails!!.title)
            binding?.etDescription?.setText(mHappyPlaceDetails!!.description)
            binding?.etDate?.setText(mHappyPlaceDetails!!.date)
            binding?.etLocation?.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude
            binding?.ivPlaceImage?.setImageURI(Uri.parse(mHappyPlaceDetails!!.image))

            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)
        }
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlace,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select your photo from Gallery", "Capture photo using camera")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }.show()
            }

            R.id.btn_save -> {
                val etTitle = binding?.etTitle?.text
                val etDescription = binding?.etDescription?.text
                val etLocation = binding?.etLocation?.text
                when {
                    etTitle.isNullOrBlank() -> Toast.makeText(
                        this,
                        "Please Enter Title",
                        Toast.LENGTH_SHORT
                    ).show()

                    etDescription.isNullOrBlank() -> Toast.makeText(
                        this,
                        "Please Enter Description",
                        Toast.LENGTH_SHORT
                    ).show()

                    etLocation.isNullOrBlank() -> Toast.makeText(
                        this,
                        "Please Enter Location",
                        Toast.LENGTH_SHORT
                    ).show()

                    saveImageToInternalStorage == null -> Toast.makeText(
                        this,
                        "Please select Image",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            mHappyPlaceDetails?.id ?: 0,
                            etTitle.toString(),
                            saveImageToInternalStorage.toString(),
                            etDescription.toString(),
                            binding?.etDate?.text.toString(),
                            etLocation.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this)
                        if (binding?.btnSave?.text == "Update") {
                            val result = dbHandler.updateHappyPlace(happyPlaceModel)
                            if (result > 0) Toast.makeText(
                                this,
                                "The happy place details are updated Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val result = dbHandler.addHappyPlace(happyPlaceModel)
                            if (result > 0) Toast.makeText(
                                this,
                                "The happy place details are inserted Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        finish()
                    }
                }

            }
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        binding?.ivPlaceImage!!.setImageBitmap(selectedImageBitmap)
                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.d(
                            "TAG",
                            "camera ${selectedImageBitmap.height} --- ${selectedImageBitmap.width}"
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlace, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (requestCode == CAMERA) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                binding?.ivPlaceImage!!.setImageBitmap(thumbnail)
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
            }
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissionList: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }

    private fun takePhotoFromCamera() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It look like you have turned off permission required feature for this feature. it can be enabled under application settings.")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}