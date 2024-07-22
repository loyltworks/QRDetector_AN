package com.loyltworks.myapplication

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

object BarCodeGeneratorUtils {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun generateBarcodeBitmap(barcodeData: String): Bitmap? {

        lateinit var multiFormatWriter: MultiFormatWriter

        return try {
            multiFormatWriter = MultiFormatWriter()
            val bitMatrix = multiFormatWriter.encode(barcodeData, BarcodeFormat.QR_CODE, 4000, 4000)
            val barcodeEncoder = BarcodeEncoder()
            val imgBitmap = barcodeEncoder.createBitmap(bitMatrix)
            imgBitmap
        } catch (e: Throwable) {
            e.stackTrace
            e.cause
            e.message
            null
        }
    }
}