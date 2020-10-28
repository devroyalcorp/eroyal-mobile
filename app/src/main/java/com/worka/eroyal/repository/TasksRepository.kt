package com.worka.eroyal.repository

import com.worka.eroyal.R
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.TEXT_PLAIN
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.tasks.*
import com.worka.eroyal.data.toGson
import com.worka.eroyal.data.visits.AddCustomerResponse
import com.worka.eroyal.data.visits.GetAreasResponse
import com.worka.eroyal.data.visits.GetBranchesResponse
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import javax.net.ssl.HttpsURLConnection

class TasksRepository(var serviceApi: ServiceApi) : KoinComponent {
    private val sessionStorage: SessionStorage by inject()
    private var observableSearch: Observable<SearchResponse>? = null
    private var observableSearchMarketShare: Observable<SearchMarketShareResponse>? = null

    fun getTasks(date:String?, cbOnSuccess: (TasksResponse?) -> Unit, cbOnError: (String?) -> Unit) {
        getTasksAPI(date).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<TasksResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: TasksResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                }

            })
    }

    private fun getTasksAPI(date:String?): Observable<TasksResponse> {
        return serviceApi.getVisitPlansData(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            date
        )
    }

    fun searchCustomers(
        keyword: String?,
        cbOnSuccess: (SearchResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        observableSearch?.unsubscribeOn(Schedulers.io())
        observableSearch = searchCustomersAPI(keyword).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
        observableSearch?.subscribe(object : DisposableObserver<SearchResponse>() {
            override fun onComplete() {
                dispose()
            }

            override fun onNext(value: SearchResponse) {
                cbOnSuccess.invoke(value)
            }

            override fun onError(e: Throwable) {
                cbOnError.invoke(e.message)
                dispose()
            }
        })
    }

    private fun searchCustomersAPI(keyword: String?): Observable<SearchResponse> {
        return serviceApi.searchCustomers(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            keyword
        )
    }

    fun searchMarketShare(
        keyword: String?,
        scope: String?,
        cbOnSuccess: (SearchMarketShareResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        observableSearchMarketShare?.unsubscribeOn(Schedulers.io())
        observableSearchMarketShare =
            searchMarketShareAPI(keyword, scope).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
        observableSearchMarketShare?.subscribe(object :
            DisposableObserver<SearchMarketShareResponse>() {
            override fun onComplete() {
                dispose()
            }

            override fun onNext(value: SearchMarketShareResponse) {
                cbOnSuccess.invoke(value)
            }

            override fun onError(e: Throwable) {
                cbOnError.invoke(e.message)
                dispose()
            }
        })
    }

    private fun searchMarketShareAPI(
        keyword: String?,
        scope: String?
    ): Observable<SearchMarketShareResponse> {
        return serviceApi.searchMarketShare(
            sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(),
            keyword, scope
        )
    }

    fun checkIn(
        customerId: String,
        visitPlanId: String,
        cbOnSuccess: (CheckinCheckoutResponse) -> Unit,
        cbOnError: (String?) -> Unit) {

        checkinAPI(customerId, visitPlanId).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CheckinCheckoutResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CheckinCheckoutResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })

    }

    private fun checkinAPI(
        customerId: String,
        visitPlanId: String
    ): Observable<CheckinCheckoutResponse> {

        val customerIdReq = customerId.toRequestBody(TEXT_PLAIN.toMediaTypeOrNull())
        val visitPlanIdReq = visitPlanId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.checkIn(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerIdReq,
            visitPlanIdReq
        )
    }

    fun checkInFollowUp(
        customerId: String,
        visitReason: String,
        imageFollowUpVisit: MultipartBody.Part?,
        cbOnSuccess: (CheckinCheckoutResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        checkinFollowUpAPI(customerId, visitReason, imageFollowUpVisit).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<CheckinCheckoutResponse>>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: Response<CheckinCheckoutResponse>) {
                    val response: CheckinCheckoutResponse? = value.body()
                    if (value.code() == HttpsURLConnection.HTTP_OK) {
                        cbOnSuccess.invoke(response)
                    } else if (value.code() == 422) {
                        cbOnError.invoke(getAppContext().getString(R.string.theres_already_have_task_for_tihs_customer))
                    } else {
                        cbOnError.invoke(getAppContext().getString(R.string.something_wrong))
                    }
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })

    }

    private fun checkinFollowUpAPI(
        customerId: String,
        visitReason: String?,
        imageFollowUpVisit: MultipartBody.Part?
    ): Observable<Response<CheckinCheckoutResponse>> {

        val visitReasonReq = visitReason.orEmpty().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val customerIdReq = customerId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.checkInFollowUp(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            visitReasonReq,
            customerIdReq,
            imageFollowUpVisit
        )
    }

    fun checkOut(
        request: CheckOutRequest,
        visitResultId: String,
        cbOnSuccess: () -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        checkoutAPI(request, visitResultId).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<CheckinCheckoutResponse>>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(response: Response<CheckinCheckoutResponse>) {
                    when (response.code()) {
                        200 -> cbOnSuccess.invoke()
                        else -> cbOnError.invoke(response.errorBody()?.string()?.toGson(BaseResponse::class.java)?.messages?.get(0).orEmpty())
                    }
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                }

            })

    }

    private fun checkoutAPI(
        request: CheckOutRequest,
        visitResultId: String
    ): Observable<Response<CheckinCheckoutResponse>> {
        return serviceApi.checkOut(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            visitResultId,
            request
        )
    }

    fun drop(
        reason: String?,
        customerId: String,
        visitPlanId: String,
        cbOnSuccess: () -> Unit,
        cbOnError: (String?) -> Unit
    ) {

        dropAPI(reason, customerId, visitPlanId).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CheckinCheckoutResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CheckinCheckoutResponse) {
                    cbOnSuccess.invoke()
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                }

            })

    }

    private fun dropAPI(
        reason: String?,
        customerId: String,
        visitPlanId: String
    ): Observable<CheckinCheckoutResponse> {
        val visitReasonReq = reason.orEmpty().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val customerIdReq = customerId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val visitPlanIdReq = visitPlanId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.drop(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            visitReasonReq,
            customerIdReq,
            visitPlanIdReq
        )
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
        return serviceApi.getAreas(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid()
        )
    }

    fun getBranches(
        areaId: Int?,
        cbOnSuccess: (GetBranchesResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        getBranchesAPI(areaId).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetBranchesResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: GetBranchesResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                }

            })
    }

    private fun getBranchesAPI(areaId: Int?): Observable<GetBranchesResponse> {
        return serviceApi.getBranches(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            areaId
        )
    }

    fun addCustomer(
        name: String,
        address: String,
        city: String,
        contacPerson: String,
        phoneNumber: String,
        customerType: String,
        latitude: String,
        longitude: String,
        areaId: String,
        branchId: String,
        brandId: String,
        imageProfile: MultipartBody.Part?,
        cbOnSuccess: (AddCustomerResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {

        addCustomerAPI(
            name,
            address,
            city,
            contacPerson,
            phoneNumber,
            customerType,
            latitude,
            longitude,
            areaId,
            branchId,
            brandId,
            imageProfile
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<AddCustomerResponse>>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: Response<AddCustomerResponse>) {
                    val response: AddCustomerResponse? = value.body()
                    if (value.code() == 200) {
                        cbOnSuccess.invoke(response)
                    } else if (value.code() == 422 || response?.messages.orEmpty().contains("Latitude") ||
                        response?.messages.orEmpty().contains("Longitude")) {
                        cbOnError.invoke(getAppContext().getString(R.string.customer_location_already_registered))
                    } else {
                        cbOnError.invoke(response?.messages?.get(0).orEmpty())
                    }
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })

    }

    private fun addCustomerAPI(
        name: String,
        address: String,
        city: String,
        contactPerson: String,
        phoneNumber: String,
        customerType: String,
        latitude: String,
        longitude: String,
        areaId: String,
        branchId: String,
        brandId: String,
        imageProfile: MultipartBody.Part?
    ): Observable<Response<AddCustomerResponse>> {

        val nameReq = name.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val addressReq = address.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val cityReq = city.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val contactPersonReq = contactPerson.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val phoneNumberReq = phoneNumber.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val customerTypeReq = customerType.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val latitudeReq = latitude.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val longitudeReq = longitude.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val areaIdReq = areaId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val branchIdReq = branchId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val brandIdsReq = brandId.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.addCustomer(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            nameReq,
            addressReq,
            cityReq,
            contactPersonReq,
            phoneNumberReq,
            customerTypeReq,
            latitudeReq,
            longitudeReq,
            areaIdReq,
            branchIdReq,
            brandIdsReq,
            imageProfile
        )
    }

    fun getExistingMarketShareCustomerList(customerId: String?, cbOnSuccess: (GetExistingMarketShareResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getExistingMarketShareCustomerListAPI(customerId).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetExistingMarketShareResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onError(e: Throwable) {
                    dispose()
                   cbOnError.invoke(e.message)
                }

                override fun onNext(response: GetExistingMarketShareResponse) {
                    cbOnSuccess.invoke(response)
                }
            })

    }

    private fun getExistingMarketShareCustomerListAPI(customerId: String?): Observable<GetExistingMarketShareResponse> {
        return serviceApi.getExistingMarketShare(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerId)
    }

    fun submitMarketShareList(customerId: String, submitMarketShareRequest: SubmitMarketShareRequest, cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit){
        submitMarketShareListAPI(customerId, submitMarketShareRequest).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<GetExistingMarketShareResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onError(e: Throwable) {
                    dispose()
                    cbOnError.invoke(e.message)
                }

                override fun onNext(response: Response<GetExistingMarketShareResponse>) {
                    when(response.code()) {
                        200 -> cbOnSuccess.invoke()
                        else -> cbOnError.invoke(
                            response.errorBody()?.string()?.toGson(BaseResponse::class.java)?.messages?.get(0).orEmpty()
                        )
                    }
                }
            })
    }

    private fun submitMarketShareListAPI(customerId: String, submitMarketShareRequest: SubmitMarketShareRequest): Observable<Response<GetExistingMarketShareResponse>> {
        return serviceApi.submitMarketShareList(sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerId,
            submitMarketShareRequest)

    }
}

