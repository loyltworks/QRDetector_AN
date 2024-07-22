package com.loyltworks.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.loyltworks.myapplication.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null



    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2

    private var startActivityForResultUsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadImageBtn.setOnClickListener(this)
        binding.captureImage.setOnClickListener(this)
        binding.validateImage.setOnClickListener(this)


    }



    private fun scanQRCode() {

        var image:InputImage? = null

        if(imageUri != null){
            image = InputImage.fromFilePath(applicationContext, imageUri!!)
        }else if(bitmap != null){
            image = InputImage.fromBitmap(bitmap!!, 0)
        }



        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = BarcodeScanning.getClient(options)


        scanner.process(image!!)
            .addOnSuccessListener { barcodes ->

                if (barcodes.isEmpty()) {
                    Toast.makeText(this, "No QR code detected", Toast.LENGTH_SHORT).show()

                } else {
                    for (barcode in barcodes) {

                        if(barcode.rawValue?.isNotEmpty() == true){
                            startActivityForResultUsed = false
                            startActivity(Intent(this@MainActivity, QrDetailsActivity::class.java).putExtra("QR_CODE_DATA", barcode.rawValue))
                        }else{
                            Toast.makeText(this, "Please upload clear image!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Failed to scan QR code", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.uploadImageBtn -> {
                startActivityForResultUsed = true
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)

            }

            R.id.captureImage -> {
                startActivityForResultUsed = true
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)

                val intent = Intent(this, CaptureImageActivity::class.java)
                startForResult.launch(intent)
            }

            R.id.validateImage -> {
                if(imageUri != null || bitmap != null){
                    scanQRCode()
                }else{
                    Toast.makeText(this, "Please upload or capture image!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            bitmap = null
            val intent = result.data
            val uriString = intent?.getStringExtra("image_uri")
            imageUri = uriString?.let { Uri.parse(it) }
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            binding.uploadImage.visibility = View.GONE
            binding.note.visibility = View.GONE
            binding.uploadedImage.visibility = View.VISIBLE
            binding.uploadedImage.setImageBitmap(bitmap)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                /*CAPTURE_IMAGE_REQUEST -> {
                    data?.extras?.get("data")?.let { bitmap ->
                        // Handle the captured image from camera
                        imageUri = null
                        this@MainActivity.bitmap = bitmap as Bitmap
                        binding.uploadImage.visibility = View.GONE
                        binding.uploadedImage.visibility = View.VISIBLE
                        binding.uploadedImage.setImageBitmap(bitmap)
                    }
                }*/
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        // Handle the selected image from gallery

                        val imageUri = data.data!!
                        startCrop(imageUri)

                    }
                }

                UCrop.REQUEST_CROP -> {
                    val resultUri: Uri? = UCrop.getOutput(data!!)
                    resultUri?.let {
                        bitmap = null
                        imageUri = it
                        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                        binding.uploadImage.visibility = View.GONE
                        binding.note.visibility = View.GONE
                        binding.uploadedImage.visibility = View.VISIBLE
                        binding.uploadedImage.setImageBitmap(bitmap)
                    }

                }


            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "croppedImage.jpg"))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(450, 450)
            .start(this)
    }

    override fun onResume() {
        super.onResume()

        if(!startActivityForResultUsed){
            imageUri = null
            bitmap = null
            binding.uploadImage.visibility = View.VISIBLE
            binding.note.visibility = View.VISIBLE
            binding.uploadedImage.visibility = View.GONE
        }

    }

}