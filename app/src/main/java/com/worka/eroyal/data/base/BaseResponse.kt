package com.worka.eroyal.data.base

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("success") var succesState: Boolean? = null
    @SerializedName("errors") var errorMessages: List<String>? = null
    @SerializedName("error") var errorMessage: String? = null
    @SerializedName("message") var message: String?  = null
    @SerializedName("messages") var messages: List<String>?  = null
}

open class Meta(
    @SerializedName("code") var int: Int,
    @SerializedName("message") var message: String?
)
