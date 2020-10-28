package com.worka.eroyal.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutDatePickerBinding
import java.util.Calendar
import java.util.Locale

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/03/20.
 */

class DatePickerDialog(
    val context: Context,
    val minDate: Long = -1,
    val maxDate: Long = -1,
    val onCancel: (() -> Unit?)? = null,
    val onDismiss: ((Calendar) -> Unit?)? = null
) {

    lateinit var dialog: Dialog
    lateinit var binding: LayoutDatePickerBinding

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = Dialog(context)
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.transparent))
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_date_picker,
            null,
            false
        )
        if (minDate != -1L) {
            binding.datePicker.minDate = this.minDate
        }
        if (maxDate != -1L) {
            binding.datePicker.maxDate = this.maxDate
        }
        binding.btnOk.setOnClickListener {
            val calendar = Calendar.getInstance(Locale.UK)
            calendar.set(
                binding.datePicker.year,
                binding.datePicker.month,
                binding.datePicker.dayOfMonth
            )
            onDismiss?.invoke(calendar)
            dialog.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            onCancel?.invoke()
        }

        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.setOnCancelListener { onCancel?.invoke() }
        dialog.window?.setBackgroundDrawable(colorDrawable)


    }

    fun show() {
        dialog.show()
    }


}
