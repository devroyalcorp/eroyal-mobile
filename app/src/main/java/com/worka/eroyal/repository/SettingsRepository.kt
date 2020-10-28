package com.worka.eroyal.repository

import com.worka.eroyal.R
import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.home.HomeResponse
import com.worka.eroyal.data.toGson
import com.worka.eroyal.data.user.LoginResponse
import com.worka.eroyal.data.user.LogoutResponse
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

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-25.
 */

class SettingsRepository (val serviceApi: ServiceApi) : KoinComponent {
    private val sessionStorage: SessionStorage by inject()

    fun updateImageProfile(imageProfile: MultipartBody.Part, onSuccess:(HomeResponse) -> Unit, onError:(String?) -> Unit ){
        updateImageProfileAPI(imageProfile).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<HomeResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: HomeResponse) {
                    onSuccess.invoke(t)
                }

                override fun onError(e: Throwable) {
                    onError(e.message)
                    dispose()
                }
            })
    }

    private fun updateImageProfileAPI(imageProfile: MultipartBody.Part) : Observable<HomeResponse> {
        return serviceApi.updateImageProfile(sessionStorage.getAccessToken(), sessionStorage.getClient(),
            sessionStorage.getUid(), imageProfile)
    }

    fun deleteImageProfile(onSuccess:(HomeResponse) -> Unit, onError:(String?) -> Unit ){
        deleteImageProfileAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<HomeResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: HomeResponse) {
                    onSuccess(t)
                }

                override fun onError(e: Throwable) {
                    onError(e.message)
                    dispose()
                }
            })
    }

    private fun deleteImageProfileAPI() : Observable<HomeResponse> {
        return serviceApi.deleteProfileImage(sessionStorage.getAccessToken(), sessionStorage.getClient(),
            sessionStorage.getUid())
    }

    fun validateResetPasswordToken(resetPasswordToken: String?, onSuccess:(String?, String?, String?) -> Unit, onError:(String?) -> Unit  ){
        validateResetPasswordTokenAPI(resetPasswordToken).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<BaseResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(response: Response<BaseResponse>) {
                    if (response.code() == 200) {
                        onSuccess.invoke(response.headers()[Constants.ACCESS_TOKEN],
                        response.headers()[Constants.CLIENT],
                        response.headers()[Constants.UID])
                    } else {
                        val errorResponse = response.errorBody()?.string()?.toGson(BaseResponse::class.java)
                        if (!errorResponse?.message.isNullOrBlank()) {
                            onError.invoke(errorResponse?.message.orEmpty())
                        } else {
                            onError.invoke(errorResponse?.errorMessage.orEmpty())
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    onError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun validateResetPasswordTokenAPI(resetPasswordToken: String?) : Observable<Response<BaseResponse>> {
        return serviceApi.validateResetPasswordToken(resetPasswordToken)
    }

    fun createNewPassword(newPasword: String, confirmPassword: String,  accessToken: String?, client: String?, uid: String?,
                          onSuccess:() -> Unit, onError:(String?) -> Unit  ){
        createNewPasswordAPI(newPasword, confirmPassword, accessToken, client, uid).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<BaseResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: Response<BaseResponse>) {
                    if(t.code() == 200) {
                        onSuccess()
                    } else {
                        onError.invoke(getAppContext().getString(R.string.failed_change_password))
                    }
                }

                override fun onError(e: Throwable) {
                    onError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun createNewPasswordAPI(newPasword: String, confirmPassword: String,
                                     accessToken: String?, client: String?, uid: String?) : Observable<Response<BaseResponse>> {

        val newPass = newPasword.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val confirmPass = confirmPassword.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.createNewPassword(accessToken.orEmpty(), client.orEmpty(), uid.orEmpty(), newPass, confirmPass)
    }

    fun changePassword(currentPassword: String, newPasword: String, confirmPassword: String, onSuccess:() -> Unit, onError:(String?) -> Unit  ){
        changePasswordAPI(currentPassword, newPasword, confirmPassword).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<BaseResponse>>(){
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: Response<BaseResponse>) {
                    if(t.code() == 200) {
                        onSuccess()
                    } else {
                        onError.invoke(getAppContext().getString(R.string.failed_change_password))
                    }
                }

                override fun onError(e: Throwable) {
                    onError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun changePasswordAPI(currentPassword: String, newPasword: String, confirmPassword: String) : Observable<Response<BaseResponse>> {

        val currentPass = currentPassword.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val newPass = newPasword.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        val confirmPass = confirmPassword.toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.changePassword(sessionStorage.getAccessToken(), sessionStorage.getClient(), sessionStorage.getUid(),
            currentPass, newPass, confirmPass)
    }

    fun signOut(onSuccess:(LogoutResponse?) -> Unit, onError:(String?) -> Unit ){
        signOutAPI().subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<LogoutResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(t: LogoutResponse) {
                    onSuccess(t)
                }

                override fun onError(e: Throwable) {
                    onError(e.message)
                    dispose()
                }
            })
    }

    private fun signOutAPI() : Observable<LogoutResponse> {
        return serviceApi.signOut(sessionStorage.getAccessToken(), sessionStorage.getClient(),
            sessionStorage.getUid())
    }



}
