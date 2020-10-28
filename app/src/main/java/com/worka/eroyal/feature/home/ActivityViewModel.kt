package com.worka.eroyal.feature.home

import android.view.View
import com.worka.eroyal.R
import com.worka.eroyal.base.Constants.ABSENCE
import com.worka.eroyal.base.Constants.VISIT
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-07.
 */
class ActivityViewModel(var type: String?, var description: String?, var dateTime: String?, var lineVisibility: Int = View.VISIBLE): SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_activity

    fun getTypeIcon(): Int {
        return when (type){
                VISIT -> R.drawable.ic_location_black
                ABSENCE -> R.drawable.ic_clock_black
                else -> 0
        }

    }
}
