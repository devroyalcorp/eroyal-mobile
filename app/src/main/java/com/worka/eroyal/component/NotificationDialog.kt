package com.worka.eroyal.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutBottomNotificationBinding


class NotificationDialog(var message: String?, context: Context, var cbOnViewDetails:() -> Unit) : BottomSheetDialog(context, R.style.NotificationBottomSheetDialog) {

    init {
        val binding: LayoutBottomNotificationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_bottom_notification,
            null, false
        )
        binding.message = message
        setContentView(binding.root)
        window?.attributes?.windowAnimations = R.style.DialogAnimationBounce
        (window?.decorView?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let {
            BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnViewDetails.setOnClickListener {
            dismiss()
            cbOnViewDetails.invoke()
        }
    }

}