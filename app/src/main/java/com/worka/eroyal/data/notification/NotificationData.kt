package com.worka.eroyal.data.notification

import com.google.gson.annotations.SerializedName

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-19.
 */

data class NotificationsResponse(@SerializedName("notifications") var notifications: ArrayList<Notification>)

data class Notification(@SerializedName("id") var id: Int?,
                        @SerializedName("title") var title: String?,
                        @SerializedName("message") var message: String?,
                        @SerializedName("image") var image: String?,
                        @SerializedName("read") var read: Boolean?)