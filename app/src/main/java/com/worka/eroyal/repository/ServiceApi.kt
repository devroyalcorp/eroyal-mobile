package com.worka.eroyal.repository

import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.clockinout.ClockInOutResponse
import com.worka.eroyal.data.home.ActivityResponse
import com.worka.eroyal.data.home.HomeResponse
import com.worka.eroyal.data.me.BrandRevenueResponse
import com.worka.eroyal.data.me.SalesValue
import com.worka.eroyal.data.me.TargetResponse
import com.worka.eroyal.data.mycustomer.*
import com.worka.eroyal.data.notification.NotificationsResponse
import com.worka.eroyal.data.orders.OrdersRequest
import com.worka.eroyal.data.orders.OrdersResponse
import com.worka.eroyal.data.orders.ProductsResponse
import com.worka.eroyal.data.report.*
import com.worka.eroyal.data.tasks.*
import com.worka.eroyal.data.user.LoginRequest
import com.worka.eroyal.data.user.LoginResponse
import com.worka.eroyal.data.user.LogoutResponse
import com.worka.eroyal.data.visits.AddCustomerResponse
import com.worka.eroyal.data.visits.GetAreasResponse
import com.worka.eroyal.data.visits.GetBranchesResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ServiceApi {

    @POST("auth/sign_in.json")
    fun signIn(@Body request: LoginRequest): Observable<Response<LoginResponse>>

    @GET("auth/sign_out.json")
    fun signOut(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<LogoutResponse>

    @GET("api/v1/absences.json")
    fun getAbsence(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<Response<ClockInOutResponse>>

    @Multipart
    @POST("api/v1/absences/clock_in.json")
    fun clockIn(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("absence[clock_in_address]") address: RequestBody,
        @Part("absence[clock_in_latitude]") latitude: RequestBody,
        @Part("absence[clock_in_longitude]") longitude: RequestBody,
        @Part imageClockIn: MultipartBody.Part?
    ): Observable<ClockInOutResponse>

    @Multipart
    @POST("api/v1/absences/clock_out.json")
    fun clockOut(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("absence[clock_out_address]") address: RequestBody,
        @Part("absence[clock_out_latitude]") latitude: RequestBody,
        @Part("absence[clock_out_longitude]") longitude: RequestBody
    ): Observable<ClockInOutResponse>

    @Multipart
    @POST("auth/password.json")
    fun resetPassword(@Part("email") email: RequestBody): Observable<Response<BaseResponse>>

    @GET("auth/password/edit.json")
    fun validateResetPasswordToken(
        @Query("reset_password_token") resetPasswordToken: String?
    ): Observable<Response<BaseResponse>>

    @Multipart
    @PATCH("auth/password.json")
    fun createNewPassword(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") confirmationPassword: RequestBody
    ): Observable<Response<BaseResponse>>

    @Multipart
    @PUT("api/v1/users/update_password.json")
    fun changePassword(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("user[current_password]") currentPassword: RequestBody,
        @Part("user[password]") newPassword: RequestBody,
        @Part("user[password_confirmation]") confirmationPassword: RequestBody
    ): Observable<Response<BaseResponse>>

    @Multipart
    @POST("api/v1/users/fcm_token.json")
    fun registerFCMToken(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("user[fcm_token]") fcmToken: RequestBody
    ): Observable<Response<BaseResponse>>

    @Headers("Content-Type: application/json")
    @GET("api/v1/users/profile.json")
    fun getHomeData(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<HomeResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/activities.json")
    fun getActivities(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<ActivityResponse>

    @GET("api/v1/visit_plans.json")
    fun getVisitPlansData(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("date") date: String?
    ): Observable<TasksResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers.json")
    fun searchCustomers(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("search") keyword: String?
    ): Observable<SearchResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/notifications.json")
    fun getNotifications(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<NotificationsResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/{id}.json")
    fun getCustomerDetails(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") id: Int?
    ): Observable<CustomerDetailsResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/users/my_customer.json")
    fun getCustomers(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int,
        @Body request: FilterRequest
    ): Observable<CustomersResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/notifications/{id}/read.json")
    fun setReadNotification(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") id: Int?
    ): Observable<NotificationsResponse>

    @Headers("Content-Type: application/json")
    @DELETE("api/v1/notifications/delete_all.json")
    fun deleteAllNotifications(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<NotificationsResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/brands.json")
    fun searchMarketShare(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("search") keyword: String? = null,
        @Query("scope") scope: String? = Constants.INTERNAL_SCOPE,
        @Query("hierarchy") hierarchy: Boolean? = null
    ): Observable<SearchMarketShareResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/{id}/marketshares.json")
    fun getExistingMarketShare (
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") customerId: String?
    ): Observable<GetExistingMarketShareResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/marketshares/{customer_id}.json")
    fun submitMarketShareList (
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("customer_id") customerId: String?,
        @Body request: SubmitMarketShareRequest?
    ): Observable<Response<GetExistingMarketShareResponse>>

    @Headers("Content-Type: application/json")
    @GET("api/v1/branches.json")
    fun getBranches(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("area_id") areaId: Int?
    ): Observable<GetBranchesResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/areas.json")
    fun getAreas(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Header("hierarchy") hierarchy: Boolean? = null
    ): Observable<GetAreasResponse>

    @Multipart
    @POST("api/v1/customers.json")
    fun addCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("customer[name]") name: RequestBody,
        @Part("customer[address]") address: RequestBody,
        @Part("customer[city]") city: RequestBody,
        @Part("customer[contact_name]") contactPerson: RequestBody,
        @Part("customer[phone_number]") phone: RequestBody,
        @Part("customer[customer_type]") customerType: RequestBody,
        @Part("customer[latitude]") latitude: RequestBody,
        @Part("customer[longitude]") longitude: RequestBody,
        @Part("customer[area_id]") areaId: RequestBody,
        @Part("customer[branch_id]") branchId: RequestBody,
        @Part("customer[brand_ids][]") brandIds: RequestBody,
        @Part imageProfile: MultipartBody.Part?
    ): Observable<Response<AddCustomerResponse>>

    @Multipart
    @POST("api/v1/visits/check_in.json")
    fun checkIn(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("visit_result[customer_id]") customerId: RequestBody,
        @Part("visit_result[visit_plan_id]") visitPlanId: RequestBody
    ): Observable<CheckinCheckoutResponse>

    @Multipart
    @POST("api/v1/visits/check_in.json")
    fun checkInFollowUp(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("visit_result[visit_reason]") visitReason: RequestBody,
        @Part("visit_result[customer_id]") customerId: RequestBody,
        @Part imageFollowUpVisit: MultipartBody.Part?
    ): Observable<Response<CheckinCheckoutResponse>>

    @POST("api/v1/visits/{id}/check_out.json")
    fun checkOut(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") visitResultId: String,
        @Body chekOutRequest: CheckOutRequest
    ): Observable<Response<CheckinCheckoutResponse>>

    @Multipart
    @POST("api/v1/visits/drop.json")
    fun drop(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part("visit_result[drop_reason]") dropReason: RequestBody,
        @Part("visit_result[customer_id]") customerId: RequestBody,
        @Part("visit_result[visit_plan_id]") visitPlanId: RequestBody
    ): Observable<CheckinCheckoutResponse>

    @Multipart
    @PUT("api/v1/users/update_avatar.json")
    fun updateImageProfile(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Part profileImage: MultipartBody.Part
    ): Observable<HomeResponse>

    @DELETE("api/v1/users/avatar.json")
    fun deleteProfileImage(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<HomeResponse>

    @GET("api/v1/users/target.json")
    fun getVisitedCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<TargetResponse>

    @GET("api/v1/users/statistic_per_day.json")
    fun getStatistic(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("date") date: String
    ): Observable<TargetResponse>

    @GET("api/v1/users/{id}/statistic_per_day.json")
    fun getTeamStatistic(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") id: String
    ): Observable<TargetResponse>

    @POST("api/v1/users/{id}/sales_values_per_month.json")
    fun getSalesValue(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") id: String,
        @Body request: FilterRequest
    ): Observable<ArrayList<SalesValue>>

    @POST("api/v1/users/{id}/sales_quantities_per_month.json")
    fun getSalesQuantity(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") id: String,
        @Body request: FilterRequest
    ): Observable<ArrayList<SalesValue>>

    @GET("api/v1/sales_brand_revenues.json")
    fun getBrandRevenues(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String
    ): Observable<BrandRevenueResponse>

    @GET("api/v1/reports/most_visited.json")
    fun getMostVisitedCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String
    ): Observable<CustomersResponse>

    @GET("api/v1/reports/most_planned.json")
    fun getMostPlannedCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String
    ): Observable<CustomersResponse>

    @GET("api/v1/reports/by_branch.json")
    fun getByBranchCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String
    ): Observable<CustomersResponse>

    @GET("api/v1/reports/by_brand.json")
    fun getByBrandsReport(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String
    ): Observable<BrandsReportResponse>

    @GET("api/v1/reports/by_sales.json")
    fun getBySalesReport(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String
    ): Observable<BySalesReportResponse>

    @POST("api/v1/users/team.json")
    fun getMyTeam(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Body request: FilterRequest
    ): Observable<BySalesReportResponse>

    @GET("api/v1/customers/{id}/notes.json")
    fun getCustomerNotes(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") customerId: Int?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Observable<NotesCustomerResponse>

    @GET("api/v1/customers/{id}/promos.json")
    fun getCustomerPromo(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") customerId: Int?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Observable<PromoCustomerResponse>

    @GET("api/v1/users/my_area.json")
    fun getAreaFilterList(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<AreaListResponse>

    @POST("api/v1/reports/statistic.json")
    fun getStatisticAreaList(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("month") month: String,
        @Body body: FilterRequest
    ): Observable<Response<StatisticAreaReportResponse>>

    @POST("api/v1/reports/chart/{area_id}.json")
    fun getChartReportList(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("area_id") areaId: Int?,
        @Query("month") month: String,
        @Body body: FilterRequest
    ): Observable<ChartReportResponse>

    @POST("api/v1/reports/stock/{area_id}.json")
    fun getStockReportList(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("area_id") areaId: Int?,
        @Query("month") month: String,
        @Body body: FilterRequest
    ): Observable<StockResponse>

    @POST("api/v1/reports/branch/{area_id}.json")
    fun getBranchReportList(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("area_id") areaId: Int?,
        @Query("month") month: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Body body: FilterRequest
    ): Observable<BranchReportResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/reports/customer/{branch_id}.json")
    fun getCustomersByBranch(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("branch_id") branchId: Int?,
        @Query("month") month: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Body body: FilterRequest
    ): Observable<CustomersResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/{customer_id}/markets.json")
    fun getMarketCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("customer_id") customerId: Int?
    ): Observable<MarketCustomerResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/{customer_id}/sales.json")
    fun getSalesCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("customer_id") customerId: Int?,
        @Query("month") month: String?
    ): Observable<SalesCustomerResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/reports/branch/{area_id}/sales.json")
    fun getSalesByBranch(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("area_id") branchId: Int?,
        @Query("month") month: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Body body: FilterRequest
    ): Observable<SalesReportResponse>

    @POST("api/v1/users/my_team.json")
    fun getVisitReportMyTeam(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("date") date: String? = null,
        @Query("month") month: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Body request: FilterRequest
    ): Observable<BySalesReportResponse>

    @Headers("Content-Type: application/json")
    @GET("/api/v1/users/{id}/visit_report.json")
    fun getVisitReportDetails(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") salesId: Int?,
        @Query("report_type") reportType: String?,
        @Query("month") month: String? = null,
        @Query("date") date: String? = null,
        @Query("page") page: Int?,
        @Query("limit") limit: Int
    ): Observable<ReportDetailsResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/report_sales/by_article.json")
    fun getReportSalesByArticle(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body body: SalesReportTableRequest
    ): Observable<SalesReportByArticleResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/report_sales/by_customer.json")
    fun getReportSalesByCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body body: SalesReportTableRequest
    ): Observable<SalesReportByCustomerResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/report_customers/active_recap.json")
    fun getCustomerActiveRecap(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body body: SalesReportTableRequest
    ): Observable<CustomerActiveRecapResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/report_customers/active_detail.json")
    fun getCustomerActiveDetail(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body body: SalesReportTableRequest
    ): Observable<CustomerActiveDetailResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/report_visit_plans/weekly.json")
    fun getWeeklyWorkPlan(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body body: SalesReportTableRequest
    ): Observable<WeeklyWorkPlanResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/report_customers/locations.json")
    fun getCustomersLocation(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Observable<CustomersResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/{id}/remaining_bill.json")
    fun getRemainingBill(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") customerId: Int?
    ): Observable<RemainingBillResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/{id}/orders.json")
    fun getOutStandingOrder(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Path("id") customerId: Int?,
        @Query("page") page: Int?,
        @Query("limit") limit: Int
    ): Observable<OutStandingOrderResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/products.json")
    fun searchProduct(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("search") keyword: String?,
        @Query("product_type") page: String?
    ): Observable<ProductsResponse>

    @Headers("Content-Type: application/json")
    @GET("api/v1/customers/by_areas.json")
    fun searchCustomer(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("search") keyword: String?
    ): Observable<CustomersResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v1/sales_orders.json")
    fun submitOrder(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body request: OrdersRequest
    ): Observable<OrdersResponse>

}
