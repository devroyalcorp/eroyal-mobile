package com.worka.eroyal.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.data.base.SingleFilterData
import com.worka.eroyal.databinding.LayoutRadioButtonFilterDialogBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 23/06/20.
 */

class RadioButtonFilterDialog(context: Context, var title: String, var singleFilterDatas: ArrayList<SingleFilterData>, var cbOnFinish:(id: Int?) -> Unit) : Dialog(context) {

    init {
        val binding: LayoutRadioButtonFilterDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.layout_radio_button_filter_dialog,
            null, false)


        binding.title = title
        singleFilterDatas.forEach {
            val radioButton = RadioButton(context)
            radioButton.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            radioButton.text = it.value
            radioButton.id = it.id ?: 0
            binding.rgFilter.addView(radioButton)
        }

        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnApply.setOnClickListener {
            cbOnFinish.invoke(binding.rgFilter.checkedRadioButtonId)
            dismiss()
        }
    }

}
