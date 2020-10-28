package com.worka.eroyal.data.home

import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.report.Role
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.tasks.TaskResponseData
import com.worka.eroyal.data.user.User
import com.worka.eroyal.data.visits.Branch

data class HomeResponse(
    @SerializedName("user") var data: HomeResponseData
) : BaseResponse()

data class HomeResponseData(
    @SerializedName("actions") var menus: List<String>,
    @SerializedName("visit_plans") var customerList: List<Customer>,
    @SerializedName("branch") var branch: Branch?,
    @SerializedName("role") var role: Role?
): User()

data class MenuHome(
    @SerializedName("type") var type: String?,
    @SerializedName("is_enabled") var isEnabled: Boolean
)

data class Activity(@SerializedName("activity_type") var activityType: String?): TaskResponseData()

data class ActivityResponse(
    @SerializedName("activities") var activities: ArrayList<Activity>
)
