package com.worka.eroyal.feature.login

import android.app.Application
import androidx.databinding.ObservableField
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants.USER
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.repository.HomeRepository
import com.worka.eroyal.repository.LoginRepository
import org.koin.core.inject


class LoginViewModel(app: Application) : BaseViewModel(app) {
    private val repository: LoginRepository by inject()
    private val homeRepository: HomeRepository by inject()
    var username = ObservableField("")
    var password = ObservableField("")

    var usernameError = ObservableField("")
    var usernameErrorEnabled = ObservableField(false)
    var passwordError = ObservableField("")
    var passwordErrorEnabled = ObservableField(false)
    var isCanLogin = ObservableField(false)

    fun login(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.login(username.get(), password.get(), {
            sessionStorage.put(USER, it?.data)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    fun registerFCMToken(fcmToken: String, cbOnFinish: () -> Unit) {
        repository.registerFCMToken(fcmToken) {
            cbOnFinish.invoke()
        }
    }

    fun isHaveActiveSession(): Boolean {
        return sessionStorage.getAccessToken().isNotEmpty()
    }

    fun checkAccessTokenExpired(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        homeRepository.getHomeData({
            cbOnSuccess.invoke()
        }, cbOnError)
    }

    fun validate() {
        usernameErrorEnabled.set(false)
        usernameError.set("")
        passwordErrorEnabled.set(false)
        passwordError.set("")
        isCanLogin.set(true)
        when {
            username.get().isNullOrBlank() || username.get().isNullOrEmpty()  -> {
                usernameErrorEnabled.set(true)
                usernameError.set(getAppContext().getString(R.string.username_cant_be_empty))
                isCanLogin.set(false)
            }
        }

        when {
            password.get().isNullOrBlank() || password.get().isNullOrEmpty()  -> {
                passwordErrorEnabled.set(true)
                passwordError.set(getAppContext().getString(R.string.password_cant_be_empty))
                isCanLogin.set(false)
            }
        }
    }
}
