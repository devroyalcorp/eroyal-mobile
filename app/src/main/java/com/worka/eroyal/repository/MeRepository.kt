package com.worka.eroyal.repository

import com.worka.eroyal.data.me.BrandRevenueResponse
import com.worka.eroyal.data.me.TargetResponse
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-11.
 */
class MeRepository( var serviceApi: ServiceApi): KoinComponent {

    fun getCustomerVisitedList(accessToken: String, client: String, uid: String, cbOnSuccess:(TargetResponse?) -> Unit, cbOnError: (String?) -> Unit){
        getCustomerVisitedListAPI(accessToken, client, uid).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<TargetResponse>(){
                    override fun onComplete() {
                       dispose()
                    }

                    override fun onNext(t: TargetResponse) {
                       cbOnSuccess.invoke(t)
                    }

                    override fun onError(e: Throwable) {
                        cbOnError.invoke(e.message)
                        dispose()
                    }

                })
    }

    private fun getCustomerVisitedListAPI(accessToken: String, client: String, uid: String): Observable<TargetResponse> {
        return serviceApi.getVisitedCustomer(accessToken, client, uid)
    }

    fun getStatistic(accessToken: String, client: String, uid: String, date: String, cbOnSuccess:(TargetResponse?) -> Unit, cbOnError: (String?) -> Unit){
        getStatisticAPI(accessToken, client, uid, date).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).
            observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<TargetResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: TargetResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getStatisticAPI(accessToken: String, client: String, uid: String, date: String): Observable<TargetResponse> {
        return serviceApi.getStatistic(accessToken, client, uid, date)
    }

    fun getBrandRevenue(month: String, accessToken: String, client: String, uid: String, cbOnSuccess:(BrandRevenueResponse?) -> Unit, cbOnError: (String?) -> Unit){
        getBrandRevenueAPI(month, accessToken, client, uid).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).
            observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<BrandRevenueResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: BrandRevenueResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getBrandRevenueAPI(month: String, accessToken: String, client: String, uid: String): Observable<BrandRevenueResponse> {
        return serviceApi.getBrandRevenues(accessToken, client, uid, month)
    }

}
