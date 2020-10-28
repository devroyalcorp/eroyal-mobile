package com.worka.eroyal.module

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.worka.eroyal.BuildConfig
import com.worka.eroyal.base.Constants.LONG_TIME_OUT
import com.worka.eroyal.repository.ServiceApi
import com.worka.eroyal.storage.SessionStorage
import com.worka.eroyal.utils.JNIUtil.apiEndpoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val appModule = module {

    single { provideGsonConverter() }

    single { provideRetrofit(get(), get()) }

    single { provideApi(get()) }
    single(named(LONG_TIME_OUT), override = true) {
        provideApi(
            provideRetrofit(
                get(),
                provideOkHttpClient(androidContext(), 10)
            )
        )
    }

    single { provideOkHttpClient(androidContext(), 2) }

    factory { SessionStorage(androidContext()) }
    single { provideChuckerCollector(androidContext()) }
    single { FirebaseRemoteConfig.getInstance() }
    single { FirebaseAnalytics.getInstance(androidContext()) }

}

fun provideGsonConverter(): GsonConverterFactory {
    return GsonConverterFactory.create()
}

fun provideChuckerCollector(context: Context): ChuckerCollector {
    return ChuckerCollector(context, true, RetentionManager.Period.ONE_HOUR)
}


fun provideRetrofit(gsonConverter: GsonConverterFactory, okHttpClient: OkHttpClient): Retrofit {

    return Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(apiEndpoint())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(gsonConverter)
        .build()
}

fun provideApi(retrofit: Retrofit): ServiceApi {
    return retrofit.create(ServiceApi::class.java)
}

fun provideOkHttpClient(context: Context, timeOut: Long): OkHttpClient {
    val okHttpClientBuilder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addInterceptor(logging)
        okHttpClientBuilder.addInterceptor(ChuckerInterceptor(context))
    }

    okHttpClientBuilder.connectTimeout(timeOut, TimeUnit.MINUTES)
    okHttpClientBuilder.writeTimeout(timeOut, TimeUnit.MINUTES)
    okHttpClientBuilder.callTimeout(timeOut, TimeUnit.MINUTES)
    okHttpClientBuilder.readTimeout(timeOut, TimeUnit.MINUTES)

    return okHttpClientBuilder.build()
}
