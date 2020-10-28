package com.worka.eroyal.data.report

import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.tasks.TaskResponseData
import com.worka.eroyal.data.user.User
import com.worka.eroyal.data.visits.Branch
import com.worka.eroyal.data.visits.Place
import java.util.logging.Filter

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */

data class BrandsReportResponse(
    @SerializedName("brands") var brands: ArrayList<Brand>
)

data class BySalesReportResponse(
    @SerializedName("users") var users: ArrayList<SalesTeam>
)

data class SalesTeam(
    @SerializedName("last_activity") var lastActivity: LastActivity,
    @SerializedName("area_names") var areaNames: String?,
    @SerializedName("sales_revenue_per_week") var salesRevenuePerWeek: ArrayList<SalesRevenue>
) : Customer()

data class SalesRevenue(
    @SerializedName("brand") var brand: String,
    @SerializedName("w_1") var week1: String,
    @SerializedName("w_2") var week2: String,
    @SerializedName("w_3") var week3: String,
    @SerializedName("w_4") var week4: String,
    @SerializedName("w_5") var week5: String,
    @SerializedName("value") var value: String
)

data class LastActivity(
    @SerializedName("customer") var customerName: String?,
    @SerializedName("visited") var dateTime: String?
)

open class AreaReport(
    @SerializedName("percentage") var percentage: Int = 0,
    @SerializedName("branches_count") var branchesCount: Long? = 0,
    @SerializedName("customers_count") var customersCount: Long? = 0,
    @SerializedName("orders") var ordesCount: Long? = 0,
    @SerializedName("sales") var totalSalesCount: String? = "",
    @SerializedName("visits") var visitsCount: Long? = 0,
    @SerializedName("isSelected") var isSelected: Boolean = false
) : Place()

data class AreaListResponse(@SerializedName("areas") var areas: ArrayList<AreaReport>)

data class StatisticAreaReportResponse(@SerializedName("areas") var areas: ArrayList<AreaReport>): BaseResponse()

data class BranchReport(
    @SerializedName("asm_count") var asmCount: Int,
    @SerializedName("sales_count") var salesCount: Int
) : AreaReport()

data class BranchReportResponse(@SerializedName("branches") var branches: ArrayList<BranchReport>)

data class ChartReport(
    @SerializedName("chart_title") var chartTitle: String,
    @SerializedName(value = "last_month", alternate = ["last_year"]) var lastMonth: ArrayList<Long>,
    @SerializedName(value = "this_month", alternate = ["this_year"]) var thisMonth: ArrayList<Long>,
    @SerializedName("target") var target: ArrayList<Long>
)

data class ChartReportResponse(@SerializedName("report_charts") var reportCharts: ArrayList<ChartReport>)

class Role(@SerializedName("hierarchy") var hierarchy: String? = null) : User()

data class SalesReportResponse(@SerializedName("branches") var branches: ArrayList<BranchSalesReport>)

data class BranchSalesReport(@SerializedName("total_sales") var totalSales: ArrayList<CustomerSalesReport>?) :
    Branch()

data class CustomerSalesReport(
    @SerializedName("customer_name") var name: String?,
    @SerializedName("value") var value: String?,
    @SerializedName("brands") var brands: ArrayList<BrandSalesReport>
)

data class BrandSalesReport(
    @SerializedName("brand_name") var brandName: String?,
    @SerializedName("value") var value: String?
)

open class FilterRequest(
    @SerializedName("brand_ids") var brandIds: ArrayList<Int?>? = null,
    @SerializedName("area_ids") var areaIds: ArrayList<Int?>? = null,
    @SerializedName("search") var keyword: String? = null,
    @SerializedName("only_sales") var onlySales: Boolean? = null
)


data class DailyVisit(
    @SerializedName("check_in_time") var checkInTime: String?,
    @SerializedName("customer_name") var customerName: String?,
    @SerializedName("spent") var spent: String?,
    @SerializedName("visit_type") var visitType: String
) : TaskResponseData()

data class CustomerDetailsReport(
    @SerializedName("name") var name: String,
    @SerializedName("total_visit") var totalVisit: Long,
    @SerializedName("total_other_visit") var totalOtherVisit: Long,
    @SerializedName("total_failed") var totalFailed: Long,
    @SerializedName("w1_visit_count") var w1VisitCount: Long,
    @SerializedName("w2_visit_count") var w2VisitCount: Long,
    @SerializedName("w3_visit_count") var w3VisitCount: Long,
    @SerializedName("w4_visit_count") var w4VisitCount: Long,
    @SerializedName("w5_visit_count") var w5VisitCount: Long,
    @SerializedName("w1_failed_count") var w1FailedCount: Long,
    @SerializedName("w2_failed_count") var w2FailedCount: Long,
    @SerializedName("w3_failed_count") var w3FailedCount: Long,
    @SerializedName("w4_failed_count") var w4FailedCount: Long,
    @SerializedName("w5_failed_count") var w5FailedCount: Long,
    @SerializedName("w1_other_visit_count") var w1OtherVisitCount: Long,
    @SerializedName("w2_other_visit_count") var w2OtherVisitCount: Long,
    @SerializedName("w3_other_visit_count") var w3OtherVisitCount: Long,
    @SerializedName("w4_other_visit_count") var w4OtherVisitCount: Long,
    @SerializedName("w5_other_visit_count") var w5OtherVisitCount: Long,
    @SerializedName("sales_revenue_per_week") var salesRevenuePerWeek: ArrayList<SalesRevenue>,
    @SerializedName("sales_revenue_per_brand") var salesRevenuePerBrand: ArrayList<SalesRevenue>
)

data class Sales(
    @SerializedName("brand_names") var brandNames: String,
    @SerializedName("area_names") var areaNames: String,
    @SerializedName("visits") var visits: ArrayList<DailyVisit>,
    @SerializedName("customers") var customers: ArrayList<CustomerDetailsReport>
) : User()

data class ReportDetailsResponse(@SerializedName("user") var sales: Sales)

data class SalesByArticleReport(
    @SerializedName("article") var article: String,
    @SerializedName("article_desc") var articleDesc: String,
    @SerializedName("size") var size: String,
    @SerializedName("quantity") var quantity: String,
    @SerializedName("value") var value: String,
    @SerializedName("mom") var mom: String
)

data class SalesByCustomerReport(
    @SerializedName("customer") var customer: String,
    @SerializedName("customer_desc") var customerDesc: String,
    @SerializedName("product") var product: String,
    @SerializedName("km") var km: String,
    @SerializedName("dv") var dv: String,
    @SerializedName("hb") var hb: String,
    @SerializedName("sa") var sa: String,
    @SerializedName("sb") var sb: String,
    @SerializedName("kb") var kb: String,
    @SerializedName("total") var total: String,
    @SerializedName("percentage") var percentage: String
)

data class SalesReportTableRequest(
    @SerializedName("month") var month: String? = null,
    @SerializedName("week") var week: String? = null,
    @SerializedName("page") var page: Int? = null,
    @SerializedName("limit") var limit: Int? = null,
    @SerializedName("sort_by") var sortBy: String? = "",
    @SerializedName("sort_direction") var sortDirection: String? = ""
): FilterRequest()

data class CustomerActiveRecap(
    @SerializedName("area_id") var areaId: String?,
    @SerializedName("brand") var brand: String?,
    @SerializedName("total") var total: String?,
    @SerializedName("new_customer") var newCustomer: String?,
    @SerializedName("active_customer") var activeCustomer: String?,
    @SerializedName("inactive_customer") var inactiveCustomer: String?,
    @SerializedName("active_previous_month") var activePreviousMonth: String?,
    @SerializedName("inactive_previous_month") var inactivePreviousMonth: String?,
    @SerializedName("growth_customer") var growthCustomer: String?,
    @SerializedName("growth_active") var growthActive: String?,
    @SerializedName("growth_inactive") var growthInactive: String?
)

data class CustomerActiveDetail(
    @SerializedName("last_invoice") var lastInvoice: String?,
    @SerializedName("customer") var customer: String?,
    @SerializedName("city") var city: String?,
    @SerializedName("brand") var brand: String?,
    @SerializedName("cust_status") var customerStatus: String?,
    @SerializedName("salesman") var salesman: String?,
    @SerializedName("customer_visit") var customerVisit: String?,
    @SerializedName("customer_brand_visit") var customerBrandVisit: String?,
    @SerializedName("is_visit") var isVisit: String?,
    @SerializedName("visit_date") var visitDate: String?
)

data class WorkPlan(
    @SerializedName("id") var id: String,
    @SerializedName("branch") var branch: String,
    @SerializedName("address_number") var addressNumber: String,
    @SerializedName("sales_name") var salesman: String,
    @SerializedName("brand") var brand: String,
    @SerializedName("cabang") var cabang: String,
    @SerializedName("target_penjualan") var targetPenjualan: String,
    @SerializedName("jumlah_penjualan") var jumlahPenjualan: String,
    @SerializedName("total_target") var totalTarget: String,
    @SerializedName("sisa") var sisa: String
)

data class SalesReportByArticleResponse(@SerializedName("sales") var sales: ArrayList<SalesByArticleReport>)

data class SalesReportByCustomerResponse(@SerializedName("sales") var sales: ArrayList<SalesByCustomerReport>)

data class CustomerActiveRecapResponse(@SerializedName("customers") var customers: ArrayList<CustomerActiveRecap>)

data class CustomerActiveDetailResponse(@SerializedName("customers") var customers: ArrayList<CustomerActiveDetail>)

data class WeeklyWorkPlanResponse(@SerializedName("visit_plans") var visitPlans: ArrayList<WorkPlan>)

data class StockResponse(
    @SerializedName("stocks") var stockList: ArrayList<Stock>
)

data class Stock(
    @SerializedName("status") var status: String?,
    @SerializedName("mattress") var mattress: Int?,
    @SerializedName("divan") var divan: Int?,
    @SerializedName("headboard") var headboard: Int?,
    @SerializedName("sorong") var sorong: Int?
)
