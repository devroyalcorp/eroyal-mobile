package com.worka.eroyal.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutLongLoadingBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-17.
 */
object LongLoadingDialog {
    fun longLoading(context: Context?): Dialog? {
        return context?.run {
            val longLoadingBinding = DataBindingUtil.inflate<LayoutLongLoadingBinding>(
                LayoutInflater.from(context),
                R.layout.layout_long_loading, null, false).apply {
            }
            Dialog(context).apply {
                setContentView(longLoadingBinding.root)
                val colorDrawable = context.resources?.getColor(android.R.color.transparent)?.let { ColorDrawable(it) }
                window?.setBackgroundDrawable(colorDrawable)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
    }
}