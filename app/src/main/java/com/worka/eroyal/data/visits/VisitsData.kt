package com.worka.eroyal.data.visits

import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.base.BaseResponse
import com.worka.eroyal.data.report.AreaReport
import com.worka.eroyal.data.tasks.Customer

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-24.
 */

data class GetBranchesResponse(@SerializedName("branches") var branches: ArrayList<Branch>)

open class Branch(
    @SerializedName("alias") var alias: String? = "",
    @SerializedName("address_1") var address1: String? = "",
    @SerializedName("address_2") var address2: String? = ""
) : Place()

data class GetAreasResponse(@SerializedName("areas") var areas: ArrayList<AreaReport>)

open class Place(@SerializedName("id") var id: Int? = null,
                 @SerializedName("name")  var name: String? = null)

data class AddCustomerResponse(@SerializedName("customer") var customer: Customer) : BaseResponse()
