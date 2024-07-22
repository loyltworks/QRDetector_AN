package com.loyltworks.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loyltworks.myapplication.databinding.ActivityMainBinding
import com.loyltworks.myapplication.databinding.ActivityQrDetailsBinding

class QrDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQrDetailsBinding
    var qrData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qrData = intent.getSerializableExtra("QR_CODE_DATA") as String

        binding.withOutHyphen.visibility = View.VISIBLE
        binding.withHyphen.visibility = View.GONE
        binding.qrDetails.text = qrData

        /*if(qrData.contains("-")){
            binding.withHyphen.visibility = View.VISIBLE
            binding.withOutHyphen.visibility = View.GONE
            binding.invoiceNo.text = qrData.split("-")[1]
            binding.invoiceDate.text = qrData.split("-")[3]
            binding.productCode.text = qrData.split("-")[5]
            binding.invoiceValue.text = qrData.split("-")[7]
            binding.dealerCode.text = qrData.split("-")[9]
            binding.influencerMobile.text = qrData.split("-")[11]
        }else{
            binding.withOutHyphen.visibility = View.VISIBLE
            binding.withHyphen.visibility = View.GONE
            binding.qrDetails.text = qrData
        }*/



        binding.submitQR.setOnClickListener(this)
        binding.back.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when(p0!!.id){

            R.id.back -> {
                onBackPressed()
            }

            R.id.submitQR -> {
                startActivity(Intent(this,GeneratedQRActivity::class.java).putExtra("QR_CODE_DATA",qrData))
                //Toast.makeText(this, "QR details submitted successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}