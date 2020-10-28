package com.worka.eroyal.extensions

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.worka.eroyal.R
import com.worka.eroyal.utils.getCleanNumbericString
import com.worka.eroyal.utils.getCleanString
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.ln
import kotlin.math.pow


fun String?.filterEmpty(defaultValue: String = ""): String {
    return if (this.isNullOrBlank()) {
        defaultValue
    } else {
        this
    }
}

fun String?.taskByPrefix(): String {
    return getAppContext().getString(R.string.task_by).plus(" ").plus(this)
}

fun String?.visitPrefix(): String {
    return getAppContext().getString(R.string.visit).plus(" ").plus(this)
}

fun String?.visitEndPrefix(): String {
    return this.plus(" ").plus(getAppContext().getString(R.string.visit))
}

fun String?.percentPrefix(): String {
    return this.plus(" %")
}

fun String?.marketPercentPrefix(): String {
    return if (this.orEmpty().contains("n", ignoreCase = true)){
        this.orEmpty()
    } else {
        this.plus(" %")
    }
}

fun String?.ofPrefix(): String {
    return "of ".plus(this)
}

fun String.appendColor(word: String): SpannableString {
    val spannableStr = SpannableString(this)
    spannableStr.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(getAppContext(), R.color.colorGreen)),
        this.indexOf(word), this.indexOf(word) + word.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableStr
}

fun String?.prefixItsBeen(): String {
    return getAppContext().getString(R.string.its_been).plus(" ").plus(this)
}

fun String?.formatThousandSeparator(defaultValue: String = ""): String {
    return if (this.orEmpty().contains("n", ignoreCase = true)){
        this.orEmpty()
    } else if (this != null && (this != "" && !this.contains("-"))) {
        this.toLong().formatThousandSeparator("")
    } else {
        defaultValue
    }
}

fun Long.formatThousandSeparator(currency: String = "Rp. "): String {
    return currency.plus(NumberFormat.getIntegerInstance().format(this))
}

fun String?.quotePrefix(): String {
    return "\"".plus(this).plus("\"")
}

fun Int?.shortNumberFormat(): String {
    this?.let {
        if (this < 1000) return "" + this
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        val format = DecimalFormat("0.#")
        val value: String = format.format(this / 1000.0.pow(exp.toDouble()))
        return String.format("%s%c", value, "kMBTPE"[exp - 1])
    } ?: run {
        return "-"
    }
}

fun String?.shortNumberFormat(defaultValue: String = "-"): String {
    if (this.isNullOrBlank() || this.contains("n/a") || this.contains("-")) {
        return defaultValue
    } else {
        val number = this.getCleanString().toLong()
        number.let {
            if (number < 1000) return "" + this
            val exp = (ln(number.toDouble()) / ln(1000.0)).toInt()
            val format = DecimalFormat("0.#")
            val value: String = format.format(number / 1000.0.pow(exp.toDouble()))
            return String.format("%s%c", value, "kMBTPE"[exp - 1])
        }
    }
}

fun Long?.shortNumberFormat(): String {
    this?.let {
        if (this < 1000) return "" + this
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        val format = DecimalFormat("0.#")
        val value: String = format.format(this / 1000.0.pow(exp.toDouble()))
        return String.format("%s%c", value, "kMBTPE"[exp - 1])
    } ?: run {
        return "-"
    }
}

fun String?.replaceZeroWith(alternate: String): String {
    return if (this.orEmpty() == "0") {
        alternate
    } else {
        this.orEmpty()
    }
}

fun String?.getInitialName(): String {
    val names = this?.trim()?.split(" ")
    var result = ""

    if(!names.isNullOrEmpty() ) {
        result = when {
            names.size == 1 -> names[0].substring(0, 1)
            names.isEmpty() -> ""
            else -> names[0].substring(0, 1).plus(names[1].substring(0, 1))
        }
    }
    return result
}
