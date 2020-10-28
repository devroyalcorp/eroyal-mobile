package com.worka.eroyal.feature.mycustomers.sales

import com.worka.eroyal.R
import com.worka.eroyal.data.mycustomer.SalesCustomer
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.bill.CellItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.HeaderColumnItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-29.
 */

class SalesCustomerItemViewModel(var brandName: String?, var salesData: ArrayList<SalesCustomer>) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_sales_customer


    fun salesAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()

        list.add(HeaderColumnItemViewModel(""))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.prev_year)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.current_year)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.growth)))

        salesData.forEach { value ->
            list.add(CellItemViewModel(value.month))
            list.add(CellItemViewModel(value.prevYearValue.formatThousandSeparator("-")))
            list.add(CellItemViewModel(value.currentYearValue.formatThousandSeparator("-")))
            list.add(CellItemViewModel(value.growth))
        }

        return GenericAppAdapter(list)
    }
}
