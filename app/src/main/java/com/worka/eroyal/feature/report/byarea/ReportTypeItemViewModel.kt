package com.worka.eroyal.feature.report.byarea

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-28.
 */
class ReportTypeItemViewModel(var count: String, var icon: Int, var title: String,
                              var clickable: Boolean = false, var onSelected: ((String?) -> Unit?)? = null): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_report_type

    fun onClick(){
        onSelected?.invoke(title)
    }
}
