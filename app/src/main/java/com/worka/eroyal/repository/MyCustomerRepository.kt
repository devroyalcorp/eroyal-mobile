package com.worka.eroyal.repository

import com.worka.eroyal.base.Constants.LIMIT_GET_DATA
import com.worka.eroyal.data.mycustomer.*
import com.worka.eroyal.data.report.FilterRequest
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-13.
 */
class MyCustomerRepository(val serviceApi: ServiceApi) : KoinComponent {
    private val sessionStorage: SessionStorage by inject()

    fun getCustomers(brandIds: ArrayList<Int?>?, areaIds: ArrayList<Int?>?, keyword: String?, page: Int?, cbOnSuccess: (CustomersResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getCustomersAPI(brandIds, areaIds, keyword, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
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

    private fun getCustomersAPI(brandIds: ArrayList<Int?>?, areaIds: ArrayList<Int?>?, keyword: String?, page: Int?, limit: Int = LIMIT_GET_DATA): Observable<CustomersResponse> {
        val request = FilterRequest(brandIds, areaIds, keyword)
        return serviceApi.getCustomers(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            page,
            limit,
            request
        )
    }

    fun getCustomerDetails(customerId: Int?, cbOnSuccess: (CustomerDetailsResponse) -> Unit, cbOnError: (String?) -> Unit){
        getCustomerDetailsAPI(customerId).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CustomerDetailsResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: CustomerDetailsResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            } )
    }

    private fun getCustomerDetailsAPI(customerId: Int?): Observable<CustomerDetailsResponse> {
        return serviceApi.getCustomerDetails(sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerId)
    }

    fun getCustomerNotes(customerId: Int?, page:Int, cbOnSuccess: (NotesCustomerResponse) -> Unit, cbOnError: (String?) -> Unit){
        getCustomerNotesAPI(customerId, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<NotesCustomerResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: NotesCustomerResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            } )
    }

    private fun getCustomerNotesAPI(customerId: Int?, page:Int, limit: Int = LIMIT_GET_DATA): Observable<NotesCustomerResponse> {
        return serviceApi.getCustomerNotes(sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerId, page, limit)
    }

    fun getCustomerPromo(customerId: Int?, page:Int, cbOnSuccess: (PromoCustomerResponse) -> Unit, cbOnError: (String?) -> Unit){
        getCustomerPromoAPI(customerId, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromoCustomerResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: PromoCustomerResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            } )
    }

    private fun getCustomerPromoAPI(customerId: Int?, page:Int, limit: Int = LIMIT_GET_DATA): Observable<PromoCustomerResponse> {
        return serviceApi.getCustomerPromo(sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerId, page, limit)
    }

    fun getMarketCustomer(customerId: Int?, cbOnSuccess:(MarketCustomerResponse) -> Unit, cbOnError:(String?) -> Unit){
        getMarketCustomerAPI(customerId).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<MarketCustomerResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: MarketCustomerResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getMarketCustomerAPI(customerId: Int?): Observable<MarketCustomerResponse> {
        return serviceApi.getMarketCustomer(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), customerId)
    }

    fun getSalesCustomer(customerId: Int?, month: String?, cbOnSuccess:(SalesCustomerResponse) -> Unit, cbOnError:(String?) -> Unit){
        getSalesCustomerAPI(customerId, month).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<SalesCustomerResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: SalesCustomerResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun getSalesCustomerAPI(customerId: Int?, month: String?): Observable<SalesCustomerResponse> {
        return serviceApi.getSalesCustomer(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), customerId, month)
    }

    fun getRemainingBill(customerId: Int?, cbOnSuccess: (RemainingBillResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getRemainingBillAPI(customerId).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<RemainingBillResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: RemainingBillResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getRemainingBillAPI(customerId: Int?): Observable<RemainingBillResponse> {
        return serviceApi.getRemainingBill(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            customerId
        )
    }

    fun getOutStandingOrder(customerId: Int?, page: Int,  cbOnSuccess: (OutStandingOrderResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getOutStandingOrderAPI(customerId, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<OutStandingOrderResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: OutStandingOrderResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getOutStandingOrderAPI(customerId: Int?, page: Int): Observable<OutStandingOrderResponse> {
        return serviceApi.getOutStandingOrder(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), customerId, page, LIMIT_GET_DATA)
    }
}
