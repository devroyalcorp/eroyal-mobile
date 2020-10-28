package com.worka.eroyal.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.component.customcomponent.DividerItemDecorator
import com.worka.eroyal.databinding.LayoutFilterListDialogBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.report.OptionItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/06/20.
 */
class OptionListDialog(context: Context, var title: String, var optionFilterList: ArrayList<OptionItemViewModel>) : Dialog(context) {

    var adapter: GenericAppAdapter<OptionItemViewModel>? = null

    init {
        val binding: LayoutFilterListDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.layout_filter_list_dialog,
            null, false)

        adapter = GenericAppAdapter(optionFilterList)
        binding.title = title
        binding.rvFilterArea.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvFilterArea.adapter = adapter
        binding.rvFilterArea.addItemDecoration(DividerItemDecorator(context.getDrawable(R.drawable.white_divider)))

        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))

        binding.btnClose.setOnClickListener { dismiss() }

    }

}
