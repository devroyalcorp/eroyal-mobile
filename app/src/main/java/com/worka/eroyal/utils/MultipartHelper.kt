package com.worka.eroyal.utils

import android.content.Context
import android.net.Uri
import com.worka.eroyal.base.Constants
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLConnection

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-11.
 */

object MultipartHelper {

    /**
     * Generates the file multipart body.
     *
     *
     * Note: Debug build may call the listener progress twice, it's a bug from the HTTP logging.
     * Release builds or builds without HTTP debugging will only show the progress once.
     * @return The request body
     */
    @JvmOverloads
    fun generatePart(context: Context, key: String, uri: Uri): MultipartBody.Part {
        val file = File(uri.path!!)
        var mimeType = context.contentResolver.getType(uri)
        if (mimeType == null) mimeType = URLConnection.guessContentTypeFromName(uri.path)
        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }
}
