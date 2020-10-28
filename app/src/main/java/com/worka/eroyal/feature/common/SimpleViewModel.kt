package com.worka.eroyal.feature.common

import com.worka.eroyal.R
import org.koin.core.KoinComponent

interface SimpleViewModel : KoinComponent {
    fun layoutId(): Int
    fun marginID(): Int {
        return R.dimen.space_0
    }
}