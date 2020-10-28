package com.worka.eroyal.feature.forgotpassword

import android.app.Application
import androidx.databinding.ObservableField
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.repository.ForgotPasswordRepository
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-16.
 */
class ForgotPasswordViewModel(application: Application): BaseViewModel(application) {
    private val repository: ForgotPasswordRepository by inject()
    var email = ObservableField("")

    fun resetPassword(cbOnSuccess:() -> Unit, cbOnError:(String?) -> Unit){
        repository.resetPassword(email.get(),{
            cbOnSuccess.invoke()
        },{
            cbOnError.invoke(it)
        })
    }
}