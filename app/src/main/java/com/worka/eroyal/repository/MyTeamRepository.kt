package com.worka.eroyal.repository

import com.worka.eroyal.base.Constants.LIMIT_GET_DATA
import com.worka.eroyal.data.me.SalesValue
import com.worka.eroyal.data.me.TargetResponse
import com.worka.eroyal.data.report.BySalesReportResponse
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
 * on 2020-01-20.
 */
class MyTeamRepository(var serviceApi: ServiceApi): KoinComponent {
        val sessionStorage: SessionStorage by inject()

    fun getMyTeam(brandIds: ArrayList<Int?>?, areaIds: ArrayList<Int?>?, keyword: String?, page: Int, cbOnSuccess: (BySalesReportResponse) -> Unit, cbOnError: (String?) -> Unit) {
        getMyTeamAPI(brandIds, areaIds, keyword, page).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
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

    private fun getMyTeamAPI(brandIds: ArrayList<Int?>?, areaIds: ArrayList<Int?>?, keyword: String?, page: Int): Observable<BySalesReportResponse> {
        val request = FilterRequest(brandIds, areaIds, keyword)
        return serviceApi.getMyTeam(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), page, LIMIT_GET_DATA, request)
    }

    fun getTeamStatistic(accessToken: String, client: String, uid: String, salesId: String, cbOnSuccess:(TargetResponse?) -> Unit, cbOnError: (String?) -> Unit){
        getTeamStatisticAPI(accessToken, client, uid, salesId).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).
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

    private fun getTeamStatisticAPI(accessToken: String, client: String, uid: String, salesId: String): Observable<TargetResponse> {
        return serviceApi.getTeamStatistic(accessToken, client, uid, salesId)
    }

    fun getSalesValue(accessToken: String, client: String, uid: String, salesId: String, brandIds: ArrayList<Int?>?, cbOnSuccess:(ArrayList<SalesValue>?) -> Unit, cbOnError: (String?) -> Unit){
        getSalesValueAPI(accessToken, client, uid, salesId, brandIds).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).
        observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ArrayList<SalesValue>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: ArrayList<SalesValue>) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getSalesValueAPI(accessToken: String, client: String, uid: String, salesId: String, brandIds: ArrayList<Int?>?): Observable<ArrayList<SalesValue>> {
        val request = FilterRequest(brandIds)
        return serviceApi.getSalesValue(accessToken, client, uid, salesId, request)
    }

    fun getSalesQuantity(accessToken: String, client: String, uid: String, salesId: String, brandIds: ArrayList<Int?>?, cbOnSuccess:(ArrayList<SalesValue>?) -> Unit, cbOnError: (String?) -> Unit){
        getSalesQuantityAPI(accessToken, client, uid, salesId, brandIds).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).
        observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ArrayList<SalesValue>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: ArrayList<SalesValue>) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getSalesQuantityAPI(accessToken: String, client: String, uid: String, salesId: String, brandIds: ArrayList<Int?>?): Observable<ArrayList<SalesValue>> {
        val request = FilterRequest(brandIds)
        return serviceApi.getSalesQuantity(accessToken, client, uid, salesId, request)
    }
}
