package com.worka.eroyal.component.monthpicker

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutMonthPickerBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import java.text.DateFormatSymbols
import java.util.*


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-20.
 */

class MonthPickerDialog(context: Context, val onDismiss: ((String) -> Unit?)? = null) : Dialog(context) {

    var adapter: GenericAppAdapter<SimpleViewModel>? = null

    init {
        val binding: LayoutMonthPickerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.layout_month_picker,
            null, false
        )

        val months = DateFormatSymbols(Locale.UK).months
        val monthsList = arrayListOf<SimpleViewModel>()
        val currentMonth = Calendar.getInstance(Locale.UK).get(Calendar.MONTH)
        val currentYear = Calendar.getInstance(Locale.UK).get(Calendar.YEAR)
        val minimumMonth = currentMonth - 6
        if (minimumMonth < 1) {
            val startYear = currentYear - 1
            monthsList.add(YearViewModel(startYear.toString()))
            val startIndex = 12 + minimumMonth + 1
            for (index in startIndex..11) {
                monthsList.add(MonthViewModel(months[index], startYear.toString()) {
                    dismiss()
                    onDismiss?.invoke(it.orEmpty())
                })
            }
            monthsList.add(YearViewModel(currentYear.toString()))
            val endIndex = 6 + minimumMonth
            for (index in 0..endIndex) {
                monthsList.add(MonthViewModel(months[index], currentYear.toString()) {
                    dismiss()
                    onDismiss?.invoke(it.orEmpty())
                })
            }

        } else {
            monthsList.add(YearViewModel(currentYear.toString()))
            for (index in minimumMonth until minimumMonth + 7) {
                monthsList.add(MonthViewModel(months[index], currentYear.toString()) {
                    dismiss()
                    onDismiss?.invoke(it.orEmpty())
                })
            }
        }

        adapter = GenericAppAdapter(monthsList)
        binding.rvMonthPicker.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMonthPicker.adapter = adapter

        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))

    }

}
