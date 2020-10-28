package com.worka.eroyal.data.tasks

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.worka.eroyal.data.base.BaseResponse

data class TasksResponse(
    @SerializedName("visit_plans") var data: List<TaskResponseData>,
    @SerializedName("target") var target: Int?,
    @SerializedName("complete") var complete: Int?
) : BaseResponse()

open class TaskResponseData (
    @SerializedName("id") var id: Int? = null,
    @SerializedName("status") var state: String? = null,
    @SerializedName("check_out_time") var checkOut: String? = null,
    @SerializedName(value = "customer", alternate = ["user"]) var customer: Customer? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("created_at") var createdDate: String? = null,
    @SerializedName("created_by") var createdBy: TaskCreator? = null,
    @SerializedName("visit_result_status") var visitResultStatus: String? = null,
    @SerializedName("description") var description: String? = null
)

open class Customer(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("code") var code: String? = null,
    @SerializedName("name") var name: String? = "Notes",
    @SerializedName("image_profile") var contactPersonAvatar: String? = null,
    @SerializedName("contact_name") var contactPersonName: String? = null,
    @SerializedName("phone_number") var phone: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("status") var state: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("brands") var brands: ArrayList<Brand> = arrayListOf(),
    @SerializedName("brand_names") var brandNames: String? = null,
    @SerializedName("count") var count: Int = 0,
    @SerializedName("task_count") var taskCount: Int = 0,
    @SerializedName("visit_count") var visitCount: Int = 0,
    @SerializedName("other_visit_count") var otherVisitCount: Int = 0,
    @SerializedName("failed_count") var failedCount: Int = 0,
    @SerializedName("total_sales_revenue") var totalSalesRevenue: Long = 0,
    @SerializedName("previous_visit") var prevVisit: TaskResponseData? = null
) {
    override fun toString(): String {
        return name.orEmpty()
    }
}

data class TaskCreator(
    @SerializedName("id") var id: Int?,
    @SerializedName("email") var email: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("code") var code: String?,
    @SerializedName("phone_number") var phoneNumber: String?
)

data class SearchResponse(
    @SerializedName("customers") var customers: List<Customer>
) : BaseResponse()


data class CheckinCheckoutResponse(
    @SerializedName("visit_result") var visitResult: VisitResult
) : BaseResponse()

data class VisitResult(
    @SerializedName("id") var id: Int
)

data class CheckOutRequest(
    @SerializedName("visit_result") var checkoutData: CheckoutData,
    @SerializedName("id") var visitResultId: String?
)

data class CheckoutData(
    @SerializedName("client_signature_data") var signatureImage: String?,
    @SerializedName("original_filename") var signatureFilename: String?,
    @SerializedName("content_type") var contentType: String?,
    @SerializedName("notes") var notes: String?,
    @SerializedName("latitude") var latitude: Double?,
    @SerializedName("longitude") var longitude: Double?,
    @SerializedName("images_attributes") var imageAttributes: ArrayList<CheckoutImage>,
    @SerializedName("checklists_attributes") var checkListData: ArrayList<CheckListData>?
)

data class CheckoutImage(
    @SerializedName("image_data") var imageData: String?,
    @SerializedName("original_filename") var originalfilename: String?,
    @SerializedName("content_type") var contentType: String?
)

data class CustomerSubmitMarketShare(
    @SerializedName("marketshares_attributes") var marketshares: ArrayList<Brand>?
)

data class SubmitMarketShareRequest(
    @SerializedName("customer") var customer: CustomerSubmitMarketShare?
)


data class SearchMarketShareResponse(
    @SerializedName("brands") var brands: ArrayList<Brand>
)

data class GetExistingMarketShareResponse(
    @SerializedName("marketshares") var marketShares: ArrayList<Brand>
)

open class Brand {
    var index: String? = null
    @SerializedName(value = "id")
    var id: String? = null
    @SerializedName(value = "brand_id")
    var brandId: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("price")
    var price: String? = null
    @SerializedName("task_count")
    var taskCount: Int? = null
    @SerializedName("_destroy")
    var isDelete: Boolean? = null
    override fun toString(): String {
        return name.orEmpty()
    }
}

data class BrandImage(
    @SerializedName("imageList") var imageList: ArrayList<Uri>? = arrayListOf()): Brand()


data class CheckListData(
    @SerializedName("name") var brandName: String?,
    @SerializedName("checklist_type") var checkListType: String?,
    @SerializedName(value = "brand_id") var brandId: String? = "",
    @SerializedName("images_attributes") var imageListData: ArrayList<ImageBrandData>?)

data class ImageBrandData (
    @SerializedName("original_filename") var imageName: String?,
    @SerializedName("image_data") var image: String?,
    @SerializedName("content_type") var contentType: String?)
