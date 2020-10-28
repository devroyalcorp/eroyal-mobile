package com.worka.eroyal.feature.mycustomers.bill

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/04/20.
 */
class CellItemViewModel(var value: String?, var isLeftGravity: Boolean = false): SimpleViewModel {
    override fun layoutId(): Int = if (!isLeftGravity) R.layout.item_cell else R.layout.item_cell_left
}
