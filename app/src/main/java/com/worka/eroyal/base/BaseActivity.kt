package com.worka.eroyal.base

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.worka.eroyal.BuildConfig
import com.worka.eroyal.R
import com.worka.eroyal.base.Constants.FIREBASE_EVENT_FAKE_GPS
import com.worka.eroyal.base.Constants.FIREBASE_EVENT_USER_ID
import com.worka.eroyal.base.RemoteConfigConst.BLOCKER
import com.worka.eroyal.base.RemoteConfigConst.BLOCKER_MESSAGE
import com.worka.eroyal.base.RemoteConfigConst.CURRENT_VERSION
import com.worka.eroyal.base.RemoteConfigConst.FORCE_UPDATE
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.ProgressDialog
import com.worka.eroyal.data.bus.FakeGps
import com.worka.eroyal.extensions.filterEmpty
import com.worka.eroyal.storage.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject


abstract class BaseActivity : AppCompatActivity() {
    val sessionStorage: SessionStorage by inject()
    val firebaseAnalytics: FirebaseAnalytics by inject()
    var progressDialog: ProgressDialog? = null
    val fakeGpsWarningDialog: CustomInfoDialog by lazy {
        CustomInfoDialog(
            this,
            getString(R.string.application_temporarily_disabled),
            withoutCancel = true,
            showImediate = false,
            onDismiss = {
                forceSignOut()
                finishAffinity()

            }
        )
    }
    val remoteConfig: FirebaseRemoteConfig by inject()

    private lateinit var grantResult: (Int, PermissionResult) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBlocker()
    }

    private fun checkBlocker() {
        if (remoteConfig.getString(BLOCKER).toBoolean()) {
            CustomInfoDialog(
                this,
                text = remoteConfig.getString(BLOCKER_MESSAGE),
                cancelable = false,
                withoutOkBtn = true,
                withoutCancel = true
            )
        } else {
            checkVersion()
        }
    }

    private fun checkVersion() {
        if (BuildConfig.VERSION_CODE < (remoteConfig.getString(CURRENT_VERSION)).filterEmpty("0").toInt()) {
            CustomInfoDialog(
                this,
                text = getUpdateMessage(),
                cancelable = !remoteConfig.getString(FORCE_UPDATE).toBoolean(),
                withoutCancel = true,
                onDismiss = {
                    if (isForceUpdate()) {
                        finishAffinity()
                    }
                }
            )
        }
    }

    private fun isForceUpdate() : Boolean {
        return remoteConfig.getString(FORCE_UPDATE).toBoolean() ||
            remoteConfig.getString(CURRENT_VERSION).toInt() - BuildConfig.VERSION_CODE >= 5
    }

    private fun getUpdateMessage() : String {
        return  if (isForceUpdate()){
            getString(R.string.force_update_message)
        } else {
            getString(R.string.new_version_available)
        }
    }

    fun showLoading(cancelable: Boolean = false, onCancel: (() -> Unit?)? = null, onDismiss: (() -> Unit?)? = null){
        progressDialog?.dialog?.dismiss()
        if (!isDestroyed || !isFinishing) {
            progressDialog = ProgressDialog(this, cancelable, onCancel, onDismiss)
        }
    }

    fun hideLoading(){
        if (!this.isDestroyed) {
            progressDialog?.let { if (it.dialog.isShowing) it.dialog.dismiss() }
        }
    }

    fun requestForPermissions(permissions: Array<out String>, requestCode: Int, grantResults: (Int, PermissionResult) -> Unit) {

        grantResult = grantResults

        if (permissions.all { convertToPermissionResult(it) == PermissionResult.GRANTED })
            grantResults.invoke(requestCode, PermissionResult.GRANTED)
        else {
            run breaker@{
                permissions.forEach {
                    if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                        this.requestPermissions(permissions, requestCode)
                        return@breaker
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_LOCATION-> {
                var permissionResult: PermissionResult = PermissionResult.GRANTED
                run breaker@{
                    permissions.forEach {
                        permissionResult = convertToPermissionResult(it)
                        if (permissionResult != PermissionResult.GRANTED) {
                            return@breaker
                        }
                    }
                }
                grantResult(requestCode, permissionResult)

            }
        }
    }

    fun Activity.convertToPermissionResult(permission: String): PermissionResult {
        return if ((ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
            PermissionResult.GRANTED
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            PermissionResult.DO_NOT_ASK_AGAIN
        } else {
            PermissionResult.NOT_GRANTED
        }
    }

    fun showFakeGpsDialog(fakeGps: FakeGps){
        if (fakeGps.isFakeGpsExist) {
            trackDetectedFakeGPS()
            fakeGpsWarningDialog.dialog.show()
        } else {
            fakeGpsWarningDialog.dialog.dismiss()
        }
    }

    private fun trackDetectedFakeGPS() {
        val params = Bundle()
        params.putString(FIREBASE_EVENT_USER_ID, sessionStorage.getUid())
        firebaseAnalytics.logEvent(FIREBASE_EVENT_FAKE_GPS, params)
    }

    fun hideKeyboard() {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun forceSignOut() {
        GlobalScope.launch {
        withContext(Dispatchers.IO) {
            FirebaseInstanceId.getInstance().deleteInstanceId()
            sessionStorage.onSignedOut()
        }
    }}


}

enum class PermissionResult {
    GRANTED,
    NOT_GRANTED,
    DO_NOT_ASK_AGAIN
}
