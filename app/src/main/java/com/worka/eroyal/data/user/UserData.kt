package com.worka.eroyal.data.user

import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.base.BaseResponse

data class LoginRequest(
    @SerializedName("username") var username: String?,
    @SerializedName("password") var password: String?
)

data class LoginResponse(
    @SerializedName("user") var data: User
) : BaseResponse()

data class LogoutResponse(
    @SerializedName("messages") var message: String
)

open class User{
    @SerializedName("id") var id: Int? = null
    @SerializedName("name") var name: String? = null
    @SerializedName("image_profile") var imageProfile: String? = null
}
