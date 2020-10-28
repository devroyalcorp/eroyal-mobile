package com.worka.eroyal.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.fragment.app.Fragment
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.extensions.getAppContext
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-05.
 */

object ImagePickerHelper {
    fun openCameraFromFragment(context: Context, activity: BaseActivity, fragment: Fragment, easyImage: EasyImage?) {
        activity.requestForPermissions(
            Constants.CAMERA_STORAGE_PERMISSION,
            Constants.REQUEST_CAMERA, grantResults = { _, permissionResult ->
                if (permissionResult == PermissionResult.GRANTED) {
                    easyImage?.openCameraForImage(fragment)
                } else {
                    CustomInfoDialog(
                        context,
                        context.getString(R.string.storage_camera_permission_required)
                    )
                }
            })
    }

    fun createImageFile() = File(createFileDirectory(), createFileName())

    private fun createFileDirectory() =
        File("${getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}/eRoyal").apply {
            if (exists().not()) mkdir()
        }

    private fun createFileName() = "${Constants.EROYAL_FILENAME}${System.currentTimeMillis()}${Constants.JPG}"

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File) {
        val stream = FileOutputStream(photo)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
    }
}
