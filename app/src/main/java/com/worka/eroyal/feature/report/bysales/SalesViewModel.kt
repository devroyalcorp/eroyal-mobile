package com.worka.eroyal.feature.report.bysales

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */
class SalesViewModel(var id: Int?, var imageAvatar: String? = "", var salesName: String?, var taskCount: String?, var taskOtherVisitCount: String?,
                     var failedTaskCount: String?,  var cbOnSelected:(Int?) -> Unit):
    SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_sales

    fun onSelected(){
        cbOnSelected.invoke(id)
    }
}