package com.worka.eroyal.feature.settings

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutImagePickerDialogBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-22.
 */

object ImagePickerDialog {
    fun buildDialog(context: Context, onOpenCamera: () -> Unit, onOpenGallery: () -> Unit): BottomSheetDialog? {
        val binding = LayoutImagePickerDialogBinding.inflate(LayoutInflater.from(context), null, false)
        return BottomSheetDialog(context).apply {
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.btnCamera.setOnClickListener {
                onOpenCamera.invoke()
                dismiss()
            }
            binding.btnGallery.setOnClickListener {
                onOpenGallery.invoke()
                dismiss()
            }
        }
    }
}