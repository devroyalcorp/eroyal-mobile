package com.worka.eroyal.repository

import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.clockinout.ClockInOutResponse
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

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-27.
 */
class ClockInOutRepository(var serviceApi: ServiceApi): KoinComponent {
    private val sessionStorage: SessionStorage by inject()

    fun getAbsence(cbOnSuccess: (ClockInOutResponse?) -> Unit, cbOnError:(String?) -> Unit){
        getAbsenceAPI().subscribeOn(Schedulers.io()). observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<ClockInOutResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: Response<ClockInOutResponse>) {
                    if (t.code() == 200 ) {
                        cbOnSuccess.invoke(t.body())
                    } else {
                        cbOnError.invoke(t.message())
                    }
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun getAbsenceAPI(): Observable<Response<ClockInOutResponse>>{
        return serviceApi.getAbsence(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid())
    }

    fun clockIn(address: String?, latitude: Double?, longitude: Double?, imageClockIn: MultipartBody.Part?, cbOnSuccess: () -> Unit, cbOnError:(String?) -> Unit){
        clockInAPI(address, latitude, longitude, imageClockIn).subscribeOn(Schedulers.io()). observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ClockInOutResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: ClockInOutResponse) {
                    cbOnSuccess.invoke()
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun clockInAPI(address: String?, latitude: Double?, longitude: Double?, imageClockIn: MultipartBody.Part?): Observable<ClockInOutResponse>{
        val addressRequest = address.orEmpty().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val latitudeRequest = latitude.toString().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val longitudeRequest = longitude.toString().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.clockIn(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), addressRequest, latitudeRequest, longitudeRequest, imageClockIn)
    }

    fun clockOut(address: String?, latitude: Double?, longitude: Double?, cbOnSuccess: () -> Unit, cbOnError:(String?) -> Unit){
        clockOutAPI(address, latitude, longitude).subscribeOn(Schedulers.io()). observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ClockInOutResponse>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: ClockInOutResponse) {
                    cbOnSuccess.invoke()
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun clockOutAPI(address: String?, latitude: Double?, longitude: Double?): Observable<ClockInOutResponse>{
        val addressRequest = address.orEmpty().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val latitudeRequest = latitude.toString().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val longitudeRequest = longitude.toString().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.clockOut(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(), addressRequest, latitudeRequest, longitudeRequest)
    }
}
