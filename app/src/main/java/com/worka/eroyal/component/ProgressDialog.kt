package com.worka.eroyal.component

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.worka.eroyal.R

class ProgressDialog(var context: Context, var cancelable: Boolean = false, var onCancel: (() -> Unit?)? = null, var onDismiss: (() -> Unit?)? = null) {

    lateinit var dialog: Dialog

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = Dialog(context)
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        dialog.setContentView(R.layout.layout_progres_dialog)
        dialog.setCancelable(cancelable)
        dialog.setOnCancelListener { onCancel?.invoke()}
        dialog.window?.setBackgroundDrawable(colorDrawable)
        dialog.show()
    }


}
