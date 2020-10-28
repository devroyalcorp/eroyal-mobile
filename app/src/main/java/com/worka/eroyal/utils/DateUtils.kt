package com.worka.eroyal.utils

import com.worka.eroyal.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

object DateUtils{
    const val YYYY_MM_DD = "dd/MM/yyyy"
    const val EEEE_D_MMM_YYYY = "EEEE, d MMM yyyy"
    const val YYYY_MM_DDTHH_MM_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val YYYY_MM_DDTHH_MM_SSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    const val HH_MM_AAA = "hh:mm aaa"
    const val HH_MM_SS = "HH:mm:ss"
    const val HHh_MMm_SSs = "H 'h' mm 'm' ss 's'"
    const val DD_MM_YYYY = "dd-MM-yyyy"
    const val DD_MMMM_YYYY = "dd MMMM yyyy"
    const val MMMM_YYYY = "MMMM yyyy"
    const val MMMM = "MMMM"

    fun formateDate(dateString: String?, initialPattern: String, newPattern: String): String {
        if (dateString == null)
            return ""
        if (dateString == "00000000" || dateString == "000000000000")
            return ""
        val initialFormatter = SimpleDateFormat(initialPattern, Locale.UK)
        val newFormatter = SimpleDateFormat(newPattern, Locale.UK)
        return try {
            val date = initialFormatter.parse(dateString)
            newFormatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }

}

fun getGreetingsMessage(calendar: Calendar): Int {
    val hourMinute = calendar.get(Calendar.HOUR_OF_DAY) * 100 +
            calendar.get(Calendar.MINUTE)
    return when (hourMinute) {
        in 100..1059 -> R.string.title_welcome_morning
        in 1100..1859 -> R.string.title_welcome_afternoon
        in 1900..2359 -> R.string.title_welcome_evening
        in 0..59 -> R.string.title_welcome_evening
        else -> {
            R.string.title_welcome_evening
        }
    }
}
