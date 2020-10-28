package com.worka.eroyal.feature.mycustomers.bill

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/04/20.
 */
class HeaderColumnItemViewModel (var value: String?): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_header_column
}
