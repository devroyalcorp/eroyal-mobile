package com.worka.eroyal.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-26.
 */

fun File.compressImage(scale: Int = 3): File {
    val filePath = this.path
    val options = BitmapFactory.Options()
    options.inSampleSize = scale
    val bitmap = BitmapFactory.decodeFile(filePath, options)

    val stream = FileOutputStream(this)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.close()
    return this
}
