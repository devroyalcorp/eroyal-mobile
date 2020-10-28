package com.worka.eroyal.feature.mycustomers.bill

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/04/20.
 */
class RowItemViewModel(var values: ArrayList<String>): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_row


    fun cellAdapter(): GenericAppAdapter<CellItemViewModel> {
        val list = arrayListOf<CellItemViewModel>()
        values.forEach {
            list.add(CellItemViewModel(it))
        }
        return GenericAppAdapter(list)
    }

}
