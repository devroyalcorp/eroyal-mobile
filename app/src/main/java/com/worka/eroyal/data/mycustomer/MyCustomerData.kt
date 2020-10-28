package com.worka.eroyal.data.mycustomer

import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.tasks.TaskResponseData

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-13.
 */
data class CustomersResponse (@SerializedName("customers") var customers: ArrayList<Customer>)

data class CustomerDetailsResponse (@SerializedName("customer") var customer: Customer)

data class NotesCustomerResponse(@SerializedName("visit_results") var visitedList: ArrayList<TaskResponseData>)

data class PromoCustomerResponse(@SerializedName("images") var images: ArrayList<PromoCustomer>)

data class PromoCustomer(@SerializedName("image") var imageUrl: String)

data class TopRevenue(@SerializedName("brand") var brand: String?,
                      @SerializedName("month") var month: String?,
                      @SerializedName("sales_amount") var salesAmount: String?)

data class BrandMarket(@SerializedName("competitors") var competitors: ArrayList<BrandMarket>,
                       @SerializedName("percentage") var percentage: String?,
                       @SerializedName("current_month") var currentMonth: String?,
                       @SerializedName("current_month_value") var currentMonthValue: String?,
                       @SerializedName("previous_month") var previousMonth: String?,
                       @SerializedName("previous_month_value") var previousMonthValue: String?): Brand()

data class MarketCustomerResponse(@SerializedName("markets") var markets: ArrayList<BrandMarket>)

data class SalesCustomerResponse(@SerializedName("sales") var salesDatas: ArrayList<SalesCustomerData>)

data class SalesCustomerData(@SerializedName("brand_name") var brandName: String?,
                             @SerializedName("sales_data") var salesData: ArrayList<SalesCustomer>)

data class SalesCustomer(
    @SerializedName("year") var year: String?,
    @SerializedName("month") var month: String?,
    @SerializedName("previous_year_value") var prevYearValue: String?,
    @SerializedName("current_year_value") var currentYearValue: String?,
    @SerializedName("growth") var growth: String?
)

data class RemainingBillResponse(@SerializedName("remaining_bill") var remainingBill: Long?)

data class OutStandingOrder(
    @SerializedName("id") var id: Int?,
    @SerializedName("brand") var brand: String?,
    @SerializedName("item_number") var itemNumber: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("ship_to") var shipTo: String?,
    @SerializedName("short_item") var shortItem: String?,
    @SerializedName("next_status") var nextStatus: Int?,
    @SerializedName("branch_desc") var branchDesc: String?,
    @SerializedName("customer_desc") var customerDesc: String?,
    @SerializedName("total") var total: Int
)

data class OutStandingOrderResponse(@SerializedName("orders") var orders: ArrayList<OutStandingOrder>)
