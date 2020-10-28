package com.worka.eroyal.data.orders

import com.google.gson.annotations.SerializedName

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 28/06/20.
 */

data class Product(
    @SerializedName("product_id") var id :Int?,
    @SerializedName("kode_barang") var productCode: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("product_type") var type: String?,
    @SerializedName("total") var qty: String?
) {
    override fun toString(): String {
        return name.orEmpty()
    }
}

data class ProductsResponse(
    @SerializedName("products") var products: ArrayList<Product>
)

data class OrdersRequest(
    @SerializedName("sales_order") var data: OrdersData
)

open class OrdersData(
    @SerializedName("customer_id") var customerId: String? = null,
    @SerializedName("ship_to_id") var shipToId: String? = null,
    @SerializedName("consumer") var consumer: String? = null,
    @SerializedName("phone_number") var phoneNumber: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("image_data") var imageData: String? = null,
    @SerializedName("original_filename") var fileName: String? = null,
    @SerializedName("content_type") var contentType: String? = null,
    @SerializedName("send_date") var sendDate: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("products_attributes") var productList: ArrayList<Product>? = null
)

data class OrdersResponse(
    @SerializedName("sales_order") var data: OrdersResponseData
)

data class OrdersResponseData(
    @SerializedName("id") var orderId: Int?,
    @SerializedName("code") var soNumber: String?,
    @SerializedName("user_name") var userName: String?,
    @SerializedName("customer_name") var customerName: String?,
    @SerializedName("ship_to_name") var shipToName: String?,
    @SerializedName("image") var image: String?,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("updated_at") var updatedAt: String?
) : OrdersData()
