package com.worka.eroyal.feature.report.byarea.sales

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-03-02.
 */
class BrandSalesReportItemViewModel(var brandName: String?, var value: String?) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_brand_sales_report
}
