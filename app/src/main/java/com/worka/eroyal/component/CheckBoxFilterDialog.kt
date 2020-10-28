package com.worka.eroyal.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.databinding.LayoutFilterAreaReportDialogBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-30.
 */
class CheckBoxFilterDialog(context: Context, var title: String, var checkBoxFilterList: ArrayList<CheckBoxFilterItemViewModel>, var cbOnFinish:(List<CheckBoxFilterItemViewModel>?) -> Unit) : Dialog(context) {

    var adapter: GenericAppAdapter<CheckBoxFilterItemViewModel>? = null

    init {
        val binding: LayoutFilterAreaReportDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_filter_area_report_dialog,
            null, false)

        adapter = GenericAppAdapter(checkBoxFilterList.toList())
        binding.title = title
        binding.rvFilterArea.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvFilterArea.adapter = adapter

        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnApply.setOnClickListener {
            getSelectedFilter()?.find { it.isSelected }?.let {
                dismiss()
                cbOnFinish.invoke(getSelectedFilter())
            }
        }
    }

    fun getSelectedFilter(): List<CheckBoxFilterItemViewModel>? {
        return adapter?.list
    }

}
