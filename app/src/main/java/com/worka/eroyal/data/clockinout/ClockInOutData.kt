package com.worka.eroyal.data.clockinout

import com.google.gson.annotations.SerializedName

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-27.
 */
data class ClockInOutResponse(@SerializedName("absence") var absence: Absence)

data class Absence(@SerializedName("selfie") var selfieUrl: String?,
                   @SerializedName("clock_in") var clockInTime: String?,
                   @SerializedName("clock_out") var clockOutTime: String?)