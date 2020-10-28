package com.worka.eroyal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.worka.eroyal.R
import com.worka.eroyal.data.bus.OpenNotification
import com.worka.eroyal.feature.notification.NotificationActivity
import com.worka.eroyal.repository.LoginRepository
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-18.
 */
class NotificationsService: FirebaseMessagingService(), KoinComponent {
    private val repository: LoginRepository by inject()

    companion object{
        const val EROYAL_NOTIF_ID = 0
        const val EROYAL_PROGRESS_NOTIF_ID = 1
        const val BODY = "body"
        const val TITLE = "title"
        const val BIG_PICTURE = "big_picture"
    }

    private lateinit var intent:Intent
    private var title: String? = ""
    private var message: String? = ""
    private var imageUrl: String? = ""

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let {
            title = it[TITLE]
            message = it[BODY]
            imageUrl = it[BIG_PICTURE]
            intent = Intent(this, NotificationActivity::class.java)
            sendNotification(message, intent)
            sendForegroundNotification(message)
        }
    }

    private fun sendNotification(message: String?, intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val image = getBitmapFromURL(imageUrl)
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel_default_id))
                .setSmallIcon(R.mipmap.ic_notif_small)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(true)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(image)
                .bigLargeIcon(null))
                .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(getString(R.string.app_name)).bigText(message))
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(getString(R.string.notification_channel_default_id),
                getString(R.string.notification_channel_default_name), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(EROYAL_NOTIF_ID, notificationBuilder.build())
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) { // Log exception
            null
        }
    }

    override fun onNewToken(token: String) {
       repository.registerFCMToken(token){}
    }

    fun sendForegroundNotification(message: String?){
        EventBus.getDefault().post(OpenNotification(message))
    }
}
