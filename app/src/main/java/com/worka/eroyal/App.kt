package com.worka.eroyal

import androidx.multidex.MultiDexApplication
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.orhanobut.hawk.Hawk
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.module.appModule
import com.worka.eroyal.module.appRepoModule
import com.worka.eroyal.module.appViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

open class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        setupConfiguration()
        initRemoteConfig()
        Hawk.init(getAppContext()).build()
    }

    fun setupConfiguration() {
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@App)
                modules(appModule, appViewModelModule, appRepoModule)
            }
        }
    }

    fun initRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
    }
}
