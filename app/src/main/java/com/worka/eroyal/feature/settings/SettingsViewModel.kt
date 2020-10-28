package com.worka.eroyal.feature.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.iid.FirebaseInstanceId
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.IMAGE_PROFILE_KEY
import com.worka.eroyal.data.user.User
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.repository.SettingsRepository
import com.worka.eroyal.utils.MultipartHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-21.
 */
class SettingsViewModel(val app: Application) : BaseViewModel(app) {
    private val repository: SettingsRepository by inject()
    lateinit var navController: NavController

    var currentPassword = ObservableField("")
    var newPassword = ObservableField("")
    var confirmPassword = ObservableField("")
    var imageProfile = ObservableField<Uri>(Uri.EMPTY)

    var passError = ObservableField("")
    var passErrorEnabled = ObservableField(false)
    var canUpdatePassword = ObservableField(false)

    var changePasswordLayoutVisibility = ObservableField(View.INVISIBLE)
    var currentPasswordVisibility = ObservableField(View.VISIBLE)

    var resetPasswordToken = ObservableField("")
    var uidResetPassword: String? = null
    var accessTokenResetPassword: String? = null
    var clientResetPassword: String? = null

    fun isResetPassword() : Boolean {
        return !resetPasswordToken.get().isNullOrBlank()
    }

    fun validateResetTokenPassword(cbOnSuccess:() -> Unit, cbOnError:(String?) -> Unit) {
        repository.validateResetPasswordToken(resetPasswordToken.get(),{ accesToken, client, uid ->
            accessTokenResetPassword = accesToken
            clientResetPassword = client
            uidResetPassword = uid
            cbOnSuccess.invoke()
        }) {
            cbOnError.invoke(it)
        }
    }

    fun updateProfileImage(context: Context, cbOnSuccess:(User) -> Unit, cbOnError:(String?) -> Unit){
        val imageProfilePart = imageProfile.get()?.let { MultipartHelper.generatePart(context, IMAGE_PROFILE_KEY, it) }
        imageProfilePart?.let { repository.updateImageProfile(it, {
            sessionStorage.put(Constants.USER, it.data)
            cbOnSuccess.invoke(it.data)
        }, cbOnError) } ?: kotlin.run {
            cbOnError.invoke(context.getString(R.string.something_wrong)) }
    }

    fun deleteProfileImage(cbOnSuccess:() -> Unit, cbOnError:(String?) -> Unit){
        repository.deleteImageProfile({
            sessionStorage.put(Constants.USER, it.data)
            imageProfile.set(Uri.EMPTY)
            cbOnSuccess.invoke()
        }, cbOnError)
    }

    fun changePassword(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        if (!isResetPassword()) {
            repository.changePassword(
                currentPassword.get().orEmpty(),
                newPassword.get().orEmpty(),
                confirmPassword.get().orEmpty(), {
                    cbOnSuccess.invoke()
                }, {
                    cbOnError.invoke(it)
                })
        } else {
            repository.createNewPassword(
                newPassword.get().orEmpty(),
                confirmPassword.get().orEmpty(),
                accessTokenResetPassword,
                clientResetPassword,
                uidResetPassword,{
                    cbOnSuccess.invoke()
                }, {
                    cbOnSuccess.invoke()
                }
            )
        }
    }

    fun signOut(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        repository.signOut({
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                    sessionStorage.onSignedOut()
                }
            }
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    fun validatePassword(){
        newPassword.get()?.let {
            confirmPassword.get()?.let { confirmPassword ->
                if (it.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (newPassword.get() == confirmPassword) {
                        passErrorEnabled.set(false)
                        passError.set("")
                        canUpdatePassword.set(true)
                    } else {
                        passErrorEnabled.set(true)
                        passError.set(getAppContext().getString(R.string.password_did_not_match))
                        canUpdatePassword.set(false)

                    }
                } else {
                    canUpdatePassword.set(false)
                }
            }
        }
    }
}
