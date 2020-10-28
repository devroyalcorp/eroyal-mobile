package com.worka.eroyal.base

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.chuckerteam.chucker.api.ChuckerCollector
import com.worka.eroyal.data.user.User
import com.worka.eroyal.storage.SessionStorage
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    KoinComponent {
    val sessionStorage: SessionStorage by inject ()
    val chuckerCollector: ChuckerCollector by inject()

    val user: User by lazy { sessionStorage.get(Constants.USER, User::class.java) }

    val userImageProfile = ObservableField<String>().apply {
        if(!sessionStorage.getAccessToken().isNullOrEmpty()) {
            set(sessionStorage.get(Constants.USER, User::class.java).imageProfile)
        }
    }

}
