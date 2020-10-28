package com.worka.eroyal.feature.report.bybrands

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */
class BrandReportViewModel(var id: Int?, var count: String? = "", var brandName: String?,var cbOnSelected:(Int?) -> Unit):
    SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_brand_report

    fun onSelected(){
        cbOnSelected.invoke(id)
    }
}