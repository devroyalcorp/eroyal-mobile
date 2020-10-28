package com.worka.eroyal.data.report.filter

import com.worka.eroyal.R

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/06/20.
 */
enum class ArticleReportSortBy(var type: String, var titleRes: Int) {
    DEFAULT("", R.string.default_text),
    QUANTITY("quantity", R.string.quantity),
    PRODUCT("product", R.string.product)
}

enum class CustomerReportSortBy(var type: String, var titleRes: Int) {
    DEFAULT("", R.string.default_text),
    TOTAL("total", R.string.total)
}

enum class SortDirection(var type: String, var titleRes: Int) {
    DEFAULT("", R.string.default_text),
    ASC("asc", R.string.ascending),
    DESC("desc", R.string.descending)
}
