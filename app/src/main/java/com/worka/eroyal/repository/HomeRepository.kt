package com.worka.eroyal.repository

import com.worka.eroyal.data.home.ActivityResponse
import com.worka.eroyal.data.home.HomeResponse
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeRepository(var serviceApi: ServiceApi) : KoinComponent {
    private val sessionStorage: SessionStorage by inject()

    fun getHomeData(cbOnSuccess: (HomeResponse?) -> Unit, cbOnError: (String?) -> Unit) {
        getHomeDataAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<HomeResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: HomeResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getHomeDataAPI(): Observable<HomeResponse> {
        return serviceApi.getHomeData(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid())
    }

    fun getActivities(cbOnSuccess: (ActivityResponse?) -> Unit, cbOnError: (String?) -> Unit) {
        getActivitiesAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ActivityResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: ActivityResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getActivitiesAPI(): Observable<ActivityResponse> {
        return serviceApi.getActivities(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid())
    }
}