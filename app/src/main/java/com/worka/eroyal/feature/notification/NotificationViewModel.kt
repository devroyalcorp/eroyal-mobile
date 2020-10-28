package com.worka.eroyal.feature.notification

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.data.notification.Notification
import com.worka.eroyal.repository.NotificationRepository
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-19.
 */
class NotificationViewModel(application: Application): BaseViewModel(application) {
    private val repository: NotificationRepository by inject()

    val notifications = MutableLiveData<ArrayList<Notification>>()

    fun getNotifications(cbOnSuccess:() -> Unit, cbOnError:(String?) -> Unit){
        repository.getNotifications({
            notifications.value = it.notifications
            cbOnSuccess.invoke()
        },{
            cbOnError.invoke(it)
        })
    }

    fun getNotificationList(cbOnRead: () -> Unit) : ArrayList<NotificationItemViewModel> {
        val list = arrayListOf<NotificationItemViewModel>()

        notifications.value?.forEach {
            list.add(NotificationItemViewModel(it.id, ObservableField(it.read), it.title, it.message, it.image){ id ->
               viewModelScope.launch {
                   repository.setReadNotification(id)
               }
                cbOnRead.invoke()
            })
        }

        return  list
    }

    fun deleteAllNotification(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.deleteAllNotification({
            cbOnSuccess.invoke()
        },{
            cbOnError.invoke(it)
        })

    }
}