package com.worka.eroyal.repository

import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.LIMIT_GET_DATA
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.mycustomer.CustomersResponse
import com.worka.eroyal.data.report.*
import com.worka.eroyal.data.tasks.SearchMarketShareResponse
import com.worka.eroyal.data.toGson
import com.worka.eroyal.data.visits.GetAreasResponse
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import java.net.HttpURLConnection

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-17.
 */
class ReportRepository(var serviceApi: ServiceApi) : KoinComponent {
    val sessionStorage: SessionStorage by inject()

    fun getMostVisitedCustomer(month: String, cbOnSucces:(CustomersResponse) -> Unit, cbOnError:(String?) -> Unit){
        getMostVisitedCustomerAPI(month).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomersResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CustomersResponse) {
                    cbOnSucces.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getMostVisitedCustomerAPI(month: String): Observable<CustomersResponse> {
        return serviceApi.getMostVisitedCustomer(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), month)
    }

    fun getMostPlannedCustomer(month: String, cbOnSucces:(CustomersResponse) -> Unit, cbOnError:(String?) -> Unit){
        getMostPlannedCustomerAPI(month).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomersResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CustomersResponse) {
                    cbOnSucces.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getMostPlannedCustomerAPI(month: String): Observable<CustomersResponse> {
        return serviceApi.getMostPlannedCustomer(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), month)
    }

    fun getByBranchCustomer(month: String, cbOnSucces:(CustomersResponse) -> Unit, cbOnError:(String?) -> Unit){
        getByBranchCustomerAPI(month).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomersResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CustomersResponse) {
                    cbOnSucces.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getByBranchCustomerAPI(month: String): Observable<CustomersResponse> {
        return serviceApi.getByBranchCustomer(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), month)
    }

    fun getByBrandsReport(month: String, cbOnSucces:(BrandsReportResponse) -> Unit, cbOnError:(String?) -> Unit){
        getByBrandsReportAPI(month).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<BrandsReportResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: BrandsReportResponse) {
                    cbOnSucces.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getByBrandsReportAPI(month: String): Observable<BrandsReportResponse> {
        return serviceApi.getByBrandsReport(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), month)
    }

    fun getBySalesReport(month: String, cbOnSuccess:(BySalesReportResponse) -> Unit, cbOnError:(String?) -> Unit){
        getBySalesReportAPI(month).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<BySalesReportResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: BySalesReportResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getBySalesReportAPI(month: String): Observable<BySalesReportResponse> {
        return serviceApi.getBySalesReport(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), month)
    }


    fun getAreaFilterList(cbOnSuccess:(AreaListResponse) -> Unit, cbOnError:(String?) -> Unit){
        getAreaFilterListAPI().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AreaListResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: AreaListResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getAreaFilterListAPI(): Observable<AreaListResponse> {
        return serviceApi.getAreaFilterList(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid())
    }

    fun getStatisticArea(month: String, brandIds: ArrayList<Int?>?, areaIds: ArrayList<Int?>, cbOnSuccess:(StatisticAreaReportResponse?) -> Unit, cbOnError:(String?) -> Unit){
        getStatisticAreaAPI(month, brandIds, areaIds).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<StatisticAreaReportResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(response: Response<StatisticAreaReportResponse>) {
                    when(response.code()) {
                        HttpURLConnection.HTTP_OK -> cbOnSuccess.invoke(response.body())
                        else -> cbOnError.invoke(response.errorBody()?.string()?.toGson(StatisticAreaReportResponse::class.java)?.messages?.get(0).orEmpty())
                    }
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getStatisticAreaAPI(month: String, brandIds: ArrayList<Int?>?, areaIds: ArrayList<Int?>): Observable<Response<StatisticAreaReportResponse>> {
        val request = FilterRequest(brandIds, areaIds)
        return serviceApi.getStatisticAreaList(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), month, request)
    }

    fun getChartReportList(areaId: Int?, month: String, brandIds: ArrayList<Int?>?, onlySales: Boolean? = null, cbOnSuccess:(ChartReportResponse) -> Unit, cbOnError:(String?) -> Unit){
        getChartReportListAPI(areaId, month, brandIds, onlySales).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ChartReportResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: ChartReportResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getChartReportListAPI(areaId: Int?, month: String, brandIds: ArrayList<Int?>?, onlySales: Boolean? = null): Observable<ChartReportResponse> {
        val request = FilterRequest(brandIds = brandIds, areaIds = null, onlySales = onlySales)
        return serviceApi.getChartReportList(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), areaId, month, request)
    }

    fun getStockReportList(areaId: Int?, month: String, brandIds: ArrayList<Int?>?, onlySales: Boolean? = null, cbOnSuccess:(StockResponse) -> Unit, cbOnError:(String?) -> Unit){
        getStockReportListAPI(areaId, month, brandIds, onlySales).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<StockResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: StockResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getStockReportListAPI(areaId: Int?, month: String, brandIds: ArrayList<Int?>?, onlySales: Boolean? = null): Observable<StockResponse> {
        val request = FilterRequest(brandIds = brandIds, areaIds = null, onlySales = onlySales)
        return serviceApi.getStockReportList(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), areaId, month, request)
    }

    fun getBranchReportList(areaId: Int?, month: String, brandIds: ArrayList<Int?>?, page: Int, cbOnSuccess:(BranchReportResponse) -> Unit, cbOnError:(String?) -> Unit){
        getBranchReportListAPI(areaId, month, brandIds, page).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<BranchReportResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: BranchReportResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getBranchReportListAPI(areaId: Int?, month: String,  brandIds: ArrayList<Int?>?, page:Int, limit: Int = LIMIT_GET_DATA): Observable<BranchReportResponse> {
        val request = FilterRequest(brandIds = brandIds, areaIds = null)
        return serviceApi.getBranchReportList(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), areaId, month, page, limit, request)
    }

    fun getCustomerByBranchReport(branchId: Int?, month: String, brandIds: ArrayList<Int?>?, page: Int, cbOnSuccess:(CustomersResponse) -> Unit, cbOnError:(String?) -> Unit){
        getCustomerReportListAPI(branchId, month, brandIds, page).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomersResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CustomersResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getCustomerReportListAPI(branchId: Int?, month: String, brandIds: ArrayList<Int?>?, page:Int, limit: Int = LIMIT_GET_DATA): Observable<CustomersResponse> {
        val request = FilterRequest(brandIds = brandIds, areaIds = null)
        return serviceApi.getCustomersByBranch(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), branchId, month, page, limit, request)
    }

    fun getSalesByBranchReport(branchId: Int?, month: String, brandIds: ArrayList<Int?>?, page: Int, cbOnSuccess:(SalesReportResponse) -> Unit, cbOnError:(String?) -> Unit){
        getSalesReportListAPI(branchId, month, brandIds, page).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<SalesReportResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: SalesReportResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getSalesReportListAPI(branchId: Int?, month: String, brandIds: ArrayList<Int?>?, page:Int, limit: Int = LIMIT_GET_DATA): Observable<SalesReportResponse> {
        val request = FilterRequest(brandIds = brandIds, areaIds = null)
        return serviceApi.getSalesByBranch(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), branchId, month, page, limit, request)
    }

    fun getAreas(cbOnSuccess: (GetAreasResponse?) -> Unit, cbOnError: (String?) -> Unit) {
        getAreasAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetAreasResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: GetAreasResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                }

            })
    }

    private fun getAreasAPI(): Observable<GetAreasResponse> {
        return serviceApi.getAreas(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), true)
    }

    fun getBrands(cbOnSuccess: (SearchMarketShareResponse?) -> Unit, cbOnError: (String?) -> Unit) {
        getBrandsAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<SearchMarketShareResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: SearchMarketShareResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                }

            })
    }

    private fun getBrandsAPI(): Observable<SearchMarketShareResponse> {
        return serviceApi.searchMarketShare(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), hierarchy = true)
    }

    fun getMyTeamDailyReport(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, date: String?, page: Int, limit: Int,
                             cbOnSuccess: (BySalesReportResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getMyTeamDailyReportAPI(areaIds, brandIds, date, page, limit).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<BySalesReportResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: BySalesReportResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getMyTeamDailyReportAPI(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, date: String?, page: Int, limit: Int): Observable<BySalesReportResponse> {
        val request = FilterRequest(brandIds, areaIds)
        return serviceApi.getVisitReportMyTeam(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(),
            date = date, page = page, limit = limit, request = request)
    }

    fun getMyTeamMonthlyReport(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, month: String?, page: Int, limit: Int,
                             cbOnSuccess: (BySalesReportResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getMyTeamMonthlyReportAPI(areaIds, brandIds, month, page, limit).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<BySalesReportResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: BySalesReportResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getMyTeamMonthlyReportAPI(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, month: String?, page: Int, limit: Int): Observable<BySalesReportResponse> {
        val request = FilterRequest(brandIds, areaIds)
        return serviceApi.getVisitReportMyTeam(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(),
            month = month, page = page, limit = limit, request = request)
    }

    fun getVisitDetailsReport(salesId: Int?, reportType: String, month: String? = null, date: String? = null, page: Int = 1, cbOnSuccess: (ReportDetailsResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getVisitReportDetailsAPI(salesId, reportType, month, date, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ReportDetailsResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: ReportDetailsResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getVisitReportDetailsAPI(
        salesId: Int?,
        reportType: String,
        month: String? = null,
        date: String? = null,
        page: Int
    ): Observable<ReportDetailsResponse> {
        return serviceApi.getVisitReportDetails(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            salesId,
            reportType,
            month,
            date,
            page,
            LIMIT_GET_DATA
        )
    }

    fun getReportSalesByArticle(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, month: String?, keyword: String?, sortBy: String?, sortDirection: String?, page: Int,  cbOnSuccess: (SalesReportByArticleResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getReportSalesByArticleAPI(areaIds, brandIds, month, keyword, sortBy, sortDirection, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<SalesReportByArticleResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: SalesReportByArticleResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getReportSalesByArticleAPI(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, month: String?, keyword: String?, sortBy: String?, sortDirection: String?, page: Int): Observable<SalesReportByArticleResponse> {
        val request = SalesReportTableRequest(month = month, page = page, limit = LIMIT_GET_DATA, sortBy = sortBy, sortDirection = sortDirection).apply {
            this.keyword = keyword
            this.brandIds = brandIds
            this.areaIds = areaIds
        }
        return serviceApi.getReportSalesByArticle(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), request)
    }

    fun getReportSalesByCustomer(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, month: String?, keyword: String?, sortBy: String?, sortDirection: String?, page: Int,  cbOnSuccess: (SalesReportByCustomerResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getReportSalesByCustomerAPI(areaIds, brandIds, month, keyword, sortBy, sortDirection, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<SalesReportByCustomerResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: SalesReportByCustomerResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getReportSalesByCustomerAPI(areaIds: ArrayList<Int?>?, brandIds: ArrayList<Int?>?, month: String?, keyword: String?, sortBy: String?, sortDirection: String?, page: Int): Observable<SalesReportByCustomerResponse> {
        val request = SalesReportTableRequest(month = month, page = page, limit = LIMIT_GET_DATA, sortBy = sortBy, sortDirection = sortDirection).apply {
            this.keyword = keyword
            this.brandIds = brandIds
            this.areaIds = areaIds
        }
        return serviceApi.getReportSalesByCustomer(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), request)
    }

    fun getCustomerActiveRecap(month: String?, page: Int,  cbOnSuccess: (CustomerActiveRecapResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getCustomerActiveRecapAPI(month, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomerActiveRecapResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: CustomerActiveRecapResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getCustomerActiveRecapAPI(month: String?, page: Int): Observable<CustomerActiveRecapResponse> {
        val request = SalesReportTableRequest(month = month, page = page, limit = LIMIT_GET_DATA)
        return serviceApi.getCustomerActiveRecap(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), request)
    }

    fun getCustomerActiveDetail(month: String?, page: Int,  cbOnSuccess: (CustomerActiveDetailResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getCustomerActiveDetailAPI(month, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomerActiveDetailResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: CustomerActiveDetailResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getCustomerActiveDetailAPI(month: String?, page: Int): Observable<CustomerActiveDetailResponse> {
        val request = SalesReportTableRequest(month = month, page = page, limit = LIMIT_GET_DATA)
        return serviceApi.getCustomerActiveDetail(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), request)
    }

    fun getWeeklyWorkPlan(page: Int,  cbOnSuccess: (WeeklyWorkPlanResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getWeeklyWorkPlanAPI(page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<WeeklyWorkPlanResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: WeeklyWorkPlanResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getWeeklyWorkPlanAPI(page: Int): Observable<WeeklyWorkPlanResponse> {
        val request = SalesReportTableRequest(page = page, limit = LIMIT_GET_DATA)
        return serviceApi.getWeeklyWorkPlan(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), request)
    }

    fun getCustomerLocation(cbOnSuccess: (CustomersResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getCustomerLocationAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomersResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CustomersResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getCustomerLocationAPI(): Observable<CustomersResponse> {
        return serviceApi.getCustomersLocation(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid()
        )
    }

}
