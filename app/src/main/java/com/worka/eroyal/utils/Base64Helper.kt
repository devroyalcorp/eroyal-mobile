package com.worka.eroyal.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-02.
 */

fun File.encodeToBase64(scale: Int = 0): String {
    var base64String = ""
    try {
        val options = BitmapFactory.Options()
        options.inSampleSize = scale
        val bitmap = BitmapFactory.decodeFile(this.path, options)

        val stream = ByteArrayOutputStream()
        bitmap?.let {
            it.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArr: ByteArray = stream.toByteArray()

            base64String = Base64.encodeToString(byteArr, 0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return base64String
}
