package com.worka.eroyal.feature.report.byarea.sales

import androidx.databinding.ObservableField
import com.worka.eroyal.R
import com.worka.eroyal.data.mycustomer.BrandMarket
import com.worka.eroyal.data.report.CustomerSalesReport
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-03-02.
 */
class BranchSalesReportItemViewModel(
    var branchName: String?,
    var customers: ArrayList<CustomerSalesReport>?
) : SimpleViewModel {

    var expanded = ObservableField(false)

    override fun layoutId(): Int = R.layout.item_branch_sales_report

    fun expandCollapse() {
        expanded.get()?.let {
            expanded.set(!it)
        }
    }

    fun getCustomerSalesReportAdapter(): GenericAppAdapter<CustomerSalesReportItemViewModel> {
        val list = arrayListOf<CustomerSalesReportItemViewModel>()
        customers?.forEach { customer ->
            list.add(CustomerSalesReportItemViewModel(customer.name, customer.value.formatThousandSeparator(), customer.brands))
        }
        return GenericAppAdapter(list)
    }
}
