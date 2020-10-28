package com.worka.eroyal.repository

import com.worka.eroyal.data.notification.NotificationsResponse
import com.worka.eroyal.data.tasks.SearchResponse
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-19.
 */
class NotificationRepository (var serviceApi: ServiceApi): KoinComponent {
    private val sessionStorage: SessionStorage by inject()

    fun getNotifications(cbOnSuccess:(NotificationsResponse) -> Unit, cbOnError:(String?) -> Unit ){
        getNotificationsAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object : DisposableObserver<NotificationsResponse>(){
                override fun onComplete() {
                   dispose()
                }

                override fun onNext(t: NotificationsResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })

    }

    private fun getNotificationsAPI() : Observable<NotificationsResponse>{
        return serviceApi.getNotifications(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid())
    }

    fun setReadNotification(id: Int?){
        setReadNotificationAPI(id).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object : DisposableObserver<NotificationsResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: NotificationsResponse) {}

                override fun onError(e: Throwable) {
                    dispose()
                }

            })

    }

    private fun setReadNotificationAPI(id: Int?) : Observable<NotificationsResponse>{
        return serviceApi.setReadNotification(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), id)
    }

    fun deleteAllNotification(cbOnSuccess: (NotificationsResponse) -> Unit, cbOnError: (String?) -> Unit){
        deleteAllNotificationsAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object : DisposableObserver<NotificationsResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: NotificationsResponse) {
                    cbOnSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })

    }

    private fun deleteAllNotificationsAPI() : Observable<NotificationsResponse>{
        return serviceApi.deleteAllNotifications(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid())
    }
}