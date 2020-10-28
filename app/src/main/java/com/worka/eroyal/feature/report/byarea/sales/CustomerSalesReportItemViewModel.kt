package com.worka.eroyal.feature.report.byarea.sales

import com.worka.eroyal.R
import com.worka.eroyal.data.report.BrandSalesReport
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-03-04.
 */
class CustomerSalesReportItemViewModel(
    var customerName: String?,
    var customerValue: String?,
    var brands: ArrayList<BrandSalesReport>
) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_customer_sales_report

    fun getBrandSalesReportAdapter(): GenericAppAdapter<BrandSalesReportItemViewModel> {
        val list = arrayListOf<BrandSalesReportItemViewModel>()
        brands.forEach { brand ->
            list.add(BrandSalesReportItemViewModel(brand.brandName, brand.value.formatThousandSeparator()))
        }
        return GenericAppAdapter(list)
    }
}
