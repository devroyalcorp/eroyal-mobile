package com.worka.eroyal.repository

import com.orhanobut.hawk.Hawk
import com.worka.eroyal.R
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.ACCESS_TOKEN
import com.worka.eroyal.base.Constants.CLIENT
import com.worka.eroyal.base.Constants.UID
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.user.LoginRequest
import com.worka.eroyal.data.user.LoginResponse
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

class LoginRepository(private val serviceApi: ServiceApi): KoinComponent {

    val sessionStorage: SessionStorage by inject ()

    fun login(username: String?, password: String?, cbOnSuccess: (LoginResponse?) -> Unit, cbOnError: (String?) -> Unit) {
        val request = LoginRequest(username, password)
        loginAPI(request).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<LoginResponse>>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: Response<LoginResponse>) {
                    if (value.code() == 200) {
                        val response: LoginResponse? = value.body()
                        sessionStorage.saveAccessToken(value.headers().get(ACCESS_TOKEN))
                        sessionStorage.saveClient(value.headers().get(CLIENT))
                        sessionStorage.saveUid(value.headers().get(UID))
                        cbOnSuccess.invoke(response)
                    } else {
                        cbOnError.invoke(getAppContext().resources.getString(R.string.invalid_credential))
                    }
                }

                override fun onError(e: Throwable) {
                    e.cause?.message?.let{
                        if (it.contains("failed", true)) {
                            cbOnError.invoke(getAppContext().getString(R.string.you_seem_to_be_offline))
                        }
                    }?: run {
                        cbOnError.invoke(e.message)
                    }
                }

            })
    }

    private fun loginAPI(request: LoginRequest): Observable<Response<LoginResponse>> {
        return serviceApi.signIn(request)
    }

    fun registerFCMToken(fcmToken: String, cbOnFinish: () -> Unit){
        registerFCMTokenAPI(fcmToken).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<BaseResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: Response<BaseResponse>) {
                    cbOnFinish.invoke()
                }

                override fun onError(e: Throwable) {
                    cbOnFinish.invoke()
                   dispose()
                }

            })
    }

    private fun registerFCMTokenAPI(fcmToken: String):  Observable<Response<BaseResponse>> {
        val accessToken: String = Hawk.get<String>(ACCESS_TOKEN).orEmpty()
        val uid : String = Hawk.get<String>(UID).orEmpty()
        val client: String = Hawk.get<String>(CLIENT).orEmpty()

        val fcmTokenReq = fcmToken.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.registerFCMToken(accessToken, client, uid, fcmTokenReq)
    }

}
