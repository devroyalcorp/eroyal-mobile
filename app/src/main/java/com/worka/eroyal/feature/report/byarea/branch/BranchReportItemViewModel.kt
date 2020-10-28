package com.worka.eroyal.feature.report.byarea.branch

import androidx.databinding.ObservableField
import com.worka.eroyal.R
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.shortNumberFormat
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.byarea.ReportTypeItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-29.
 */
class BranchReportItemViewModel(var branchId: Int?, var branchName: String?, var customerCount: Long?, var ordersCount: Long?, var totalSalesCount: Long?, var visitCount: Long?,
                                var asmCount: String?, var salesCount: String?, var onOpenCustomer:(Int?) -> Unit): SimpleViewModel {

    var expanded = ObservableField(false)

    override fun layoutId(): Int = R.layout.item_branch_report

    fun expandCollapse(){
        expanded.get()?.let {
            expanded.set(!it)
        }
    }

    fun getReportInfoAdapter(): GenericAppAdapter<ReportTypeItemViewModel> {
        val list = arrayListOf<ReportTypeItemViewModel>()
        list.add(
            ReportTypeItemViewModel(
                customerCount.shortNumberFormat(),
                R.drawable.ic_my_customer_active,
                getAppContext().resources.getString(R.string.customers),
                true
            ) {
                onOpenCustomer.invoke(branchId)
            })
        list.add(
            ReportTypeItemViewModel(
                ordersCount.shortNumberFormat(),
                R.drawable.ic_order_active,
                getAppContext().resources.getString(R.string.orders)
            )
        )
        list.add(
            ReportTypeItemViewModel(
                totalSalesCount.shortNumberFormat(),
                R.drawable.ic_sales,
                getAppContext().resources.getString(R.string.sales)
            )
        )
        list.add(
            ReportTypeItemViewModel(
                visitCount.shortNumberFormat(),
                R.drawable.ic_task_green,
                getAppContext().resources.getString(R.string.visits)
            )
        )
        return GenericAppAdapter(list)
    }
}
