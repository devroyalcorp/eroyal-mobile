package com.worka.eroyal.extensions

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-09.
 */

fun String?.toPriceInt(): Int {
    return if (this.orEmpty().contains("n", ignoreCase = true)){
        0
    } else {
        this?.let { it.replace(" %","").toInt() } ?: 0
    }
}
