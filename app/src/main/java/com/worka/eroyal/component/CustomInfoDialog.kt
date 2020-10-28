package com.worka.eroyal.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutCustomInfoDialogBinding

class CustomInfoDialog(
    val context: Context,
    val text: String?,
    val onCancel: (() -> Unit?)? = null,
    val onDismiss: (() -> Unit?)? = null,
    val textButtonOk: String? = context.getString(android.R.string.ok),
    val withoutOkBtn: Boolean = false,
    val withoutCancel: Boolean = false,
    val cancelable: Boolean = true,
    val showImediate: Boolean = true
) {

    lateinit var dialog: Dialog
    lateinit var binding: LayoutCustomInfoDialogBinding

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = Dialog(context)
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_custom_info_dialog,
            null,
            false
        )
        binding.btnOk.text = textButtonOk
        binding.tvText.text = text
        binding.btnOk.setOnClickListener {
            dialog.dismiss()
            onDismiss?.invoke()
        }
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            onCancel?.invoke()
        }
        if (withoutOkBtn){
            binding.btnOk.visibility = View.GONE
        }
        if (withoutCancel){
            binding.btnCancel.visibility = View.GONE
        }
        dialog.setCancelable(cancelable)
        dialog.setContentView(binding.root)
        dialog.setOnCancelListener { onCancel?.invoke() }
        dialog.window?.setBackgroundDrawable(colorDrawable)
        if (showImediate) {
            dialog.show()
        }

    }


}