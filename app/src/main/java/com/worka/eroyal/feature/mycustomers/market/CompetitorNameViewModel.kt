package com.worka.eroyal.feature.mycustomers.market

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-08.
 */
class CompetitorNameViewModel(var competitorName: String?, var color: Int): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_competitor_name
}
