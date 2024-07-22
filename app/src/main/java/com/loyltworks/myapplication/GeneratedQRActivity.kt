package com.loyltworks.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loyltworks.myapplication.databinding.ActivityGeneratedQractivityBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GeneratedQRActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGeneratedQractivityBinding
    var qrData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneratedQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qrData = intent.getSerializableExtra("QR_CODE_DATA") as String

        val generatedQR = BarCodeGeneratorUtils.generateBarcodeBitmap(qrData)
        binding.qrImg.setImageBitmap(generatedQR)



        binding.whatsappShare.setOnClickListener{

            shareBitmap(generatedQR!!)

        }

        binding.back.setOnClickListener{

            onBackPressed()
        }



    }

    private fun shareBitmap(bitmap: Bitmap) {
        try {
            val tempFile = File(cacheDir, "temp_image.jpg")
            val fos = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()


            // Get the URI of the file
            val contentUri: Uri = FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)

            // Create the sharing intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary read permission to the content URI
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                setPackage("com.whatsapp") // Limit to WhatsApp
            }

            startActivity(Intent.createChooser(shareIntent, "Share image via"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}