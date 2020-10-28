package com.worka.eroyal.repository

import com.worka.eroyal.R
import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.extensions.getAppContext
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-16.
 */
class ForgotPasswordRepository(var serviceApi: ServiceApi) {

    fun resetPassword(email: String?, cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        resetPasswordApi(email).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Response<BaseResponse>>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: Response<BaseResponse>) {
                    if (value.code() == 200) {
                        cbOnSuccess.invoke()
                    } else {
                        cbOnError.invoke(getAppContext().getString(R.string.email_not_found))
                    }
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }

            })
    }

    private fun resetPasswordApi(email: String?): Observable<Response<BaseResponse>> {
        val emailRequest = email.orEmpty().toRequestBody(Constants.TEXT_PLAIN.toMediaTypeOrNull())
        return serviceApi.resetPassword(email = emailRequest)
    }


}
