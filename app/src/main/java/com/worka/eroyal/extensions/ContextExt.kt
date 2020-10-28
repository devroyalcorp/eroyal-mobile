package com.worka.eroyal.extensions

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.worka.eroyal.BuildConfig
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.getGreetingsMessage
import org.koin.core.context.GlobalContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun getAppContext() = GlobalContext.get().koin.get<Application>()

fun getGreetingMessage(calendar: Calendar?): String {
    return calendar?.let {
        getAppContext().resources.getString(getGreetingsMessage(it))
    }.orEmpty()
}

fun getCurrentDate(): String {
    val c = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat(DateUtils.EEEE_D_MMM_YYYY, Locale.UK)
    return dateFormat.format(c)
}

fun Context.generateUri(file: File) : Uri {
    return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
        FileProvider.getUriForFile(
            this, "${BuildConfig.APPLICATION_ID}.provider",
            file)
    } else {
        Uri.fromFile(file)
    }
}
