package com.worka.eroyal.data.me

import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.tasks.TaskResponseData

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-12.
 */

data class TargetResponse(
    @SerializedName("area") var area: Area?,
    @SerializedName("statistic") var statistic: Statistic?
)

data class Area(var text: String)

data class Statistic(
    @SerializedName("month") var month: String?,
    @SerializedName("sales") var sales: Visit?,
    @SerializedName(value = "visited", alternate = ["visits"]) var visited: Visit,
    @SerializedName("failed") var failed: Visit,
    @SerializedName("free_visit") var freeVisit: Visit
)

open class StatisticData {
    @SerializedName(value ="value", alternate = ["visit"])
    var value: Int? = null
    @SerializedName(value = "total", alternate = ["rkb"])
    var total: Int? = null
    @SerializedName("percentage")
    var percentage: Int? = null
    @SerializedName("last_interval")
    var lastInterval: Int? = null
    @SerializedName("charts")
    var chartData: Array<Int>? = null
    @SerializedName("charts_rkb")
    var chartRKB: Array<Int>? = null
    @SerializedName("charts_visit")
    var chartVisit: Array<Int>? = null
}

data class Visit(
    @SerializedName("visit_results") var visitedList: ArrayList<TaskResponseData>
) : StatisticData()

data class BrandRevenue(
    @SerializedName("name") var brandName: String?,
    @SerializedName("target") var target: String?,
    @SerializedName("percentage") var percentage: String?,
    @SerializedName("current_month") var currentMonth: String?,
    @SerializedName("current_month_value") var currentMonthValue: String?,
    @SerializedName("previous_month") var previousMonth: String?,
    @SerializedName("previous_month_value") var previousMonthValue: String?,
    @SerializedName("previous_year") var previousYear: String?,
    @SerializedName("previous_year_value") var previousYearValue: String?,
    @SerializedName("revenue") var revenueState: String?,
    @SerializedName("yoy") var yoyState: String?,
    @SerializedName("yoy_percentage") var yoyPercentage: String?
)

data class BrandRevenueResponse(
    @SerializedName("brands") var brands: ArrayList<BrandRevenue>?
)

data class SalesValue(
    @SerializedName("year") var year: String?,
    @SerializedName("month") var month: String?,
    @SerializedName("value") var value: String?,
    @SerializedName("target") var target: String?,
    @SerializedName("percentage") var percentage: String?
)
