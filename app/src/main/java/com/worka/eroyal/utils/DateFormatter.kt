package com.worka.eroyal.utils

import com.worka.eroyal.R
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.utils.DateUtils.EEEE_D_MMM_YYYY
import com.worka.eroyal.utils.DateUtils.HH_MM_AAA
import com.worka.eroyal.utils.DateUtils.YYYY_MM_DDTHH_MM_SSSXXX
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-17.
 */

fun Calendar.getMonthYear(): String {
    val sdf = SimpleDateFormat("MMMM yyyy", Locale.UK)
    return sdf.format(this.time)
}

fun Calendar.getDayMonth(): String {
    val sdf = SimpleDateFormat("dd MMM", Locale.UK)
    return sdf.format(this.time)
}

fun Calendar.getDateFormat(format: String): String {
    val sdf = SimpleDateFormat(format, Locale.UK)
    return sdf.format(this.time)
}

fun String?.getVisitSpentTime(checkOutDateTime: String?): String {
    val todayDate = SimpleDateFormat(EEEE_D_MMM_YYYY, Locale.UK).format(Calendar.getInstance().time)
    var checkInDay = DateUtils.formateDate(this, YYYY_MM_DDTHH_MM_SSSXXX, EEEE_D_MMM_YYYY)
    var checkOutDay = DateUtils.formateDate(checkOutDateTime, YYYY_MM_DDTHH_MM_SSSXXX, EEEE_D_MMM_YYYY)
    val checkInTime = DateUtils.formateDate(this, YYYY_MM_DDTHH_MM_SSSXXX, HH_MM_AAA)
    val checkOutTime = DateUtils.formateDate(checkOutDateTime, YYYY_MM_DDTHH_MM_SSSXXX, HH_MM_AAA)

    if (checkInDay == todayDate) {
        checkInDay = getAppContext().getString(R.string.today)
    }

    if (checkOutDay == todayDate) {
        checkOutDay = getAppContext().getString(R.string.today)
    }

    return when (checkInDay) {
        checkOutDay -> checkInDay.plus(" ").plus(getAppContext().getString(R.string.at)).plus( " ")
            .plus(checkInTime).plus(" - ").plus(checkOutTime)
        else -> checkInDay.plus(" ").plus(getAppContext().getString(R.string.at)).plus( " ")
            .plus(checkInTime).plus(" - ").plus(checkOutDay).plus(" ").plus(getAppContext().getString(R.string.at)).plus( " ")
            .plus(checkOutTime)
    }


}
