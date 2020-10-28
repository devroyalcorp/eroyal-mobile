package com.worka.eroyal.feature.orders

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.IMAGE_TYPE
import com.worka.eroyal.component.customcomponent.SingleLiveEvent
import com.worka.eroyal.data.orders.OrdersData
import com.worka.eroyal.data.orders.OrdersRequest
import com.worka.eroyal.data.orders.OrdersResponseData
import com.worka.eroyal.data.orders.Product
import com.worka.eroyal.data.report.Role
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.extensions.filterEmpty
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.repository.OrdersRepository
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.DateUtils.DD_MMMM_YYYY
import com.worka.eroyal.utils.DateUtils.YYYY_MM_DDTHH_MM_SSSZ
import com.worka.eroyal.utils.JNIUtil
import com.worka.eroyal.utils.compressImage
import com.worka.eroyal.utils.encodeToBase64
import org.koin.core.inject
import java.io.File


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 27/06/20.
 */
class OrdersViewModel(application: Application) : BaseViewModel(application) {
    private val repository: OrdersRepository by inject()
    lateinit var navController: NavController

    val titlePage = MutableLiveData(getAppContext().getString(R.string.sales_order))

    val currentSales = ObservableField(user.name)
    val consumerName = ObservableField("")
    val deliveryDate = ObservableField("")
    var orderImageUrl = ObservableField(Uri.EMPTY)
    val productType = ObservableField("")
    val productSelected = ObservableField<Product>()
    val productQty = ObservableField("")
    val customerSelected = ObservableField<Customer>()
    val shipToSelected = ObservableField<Customer>()
    val deliveryAddress = ObservableField("")
    val mobilePhone = ObservableField("")
    val email = ObservableField("")
    val notes = ObservableField("")
    val summaryOrdersData = ObservableField<OrdersResponseData>()

    var consumerNameError = ObservableField("")
    var consumerNameErrorEnabled = ObservableField(false)
    var tokoError = ObservableField("")
    var tokoErrorEnabled = ObservableField(false)
    var shipToError = ObservableField("")
    var shipToErrorEnabled = ObservableField(false)
    var deliveryDateError = ObservableField("")
    var deliveryDateErrorEnabled = ObservableField(false)
    var deliveryAddressError = ObservableField("")
    var deliveryAddressErrorEnabled = ObservableField(false)
    var mobilePhoneError = ObservableField("")
    var mobilePhoneErrorEnabled = ObservableField(false)
    var emailError = ObservableField("")
    var emailErrorEnabled = ObservableField(false)
    var notesError = ObservableField("")
    var notesErrorEnabled = ObservableField(false)

    var productTypeError = ObservableField("")
    var productTypeErrorEnabled = ObservableField(false)
    var productError = ObservableField("")
    var productErrorEnabled = ObservableField(false)
    var productQtyError = ObservableField("")
    var productQtyErrorEnabled = ObservableField(false)

    var productSuggestionList = MutableLiveData<ArrayList<Product>>()
    var tokoSuggestionList = MutableLiveData<ArrayList<Customer>>()

    private var _productList = MutableLiveData<ArrayList<Product>>()
    var productList: LiveData<ArrayList<Product>> = _productList

    val onErrorImageOrder = SingleLiveEvent<Unit>()
    val onErrorOrdersItem = SingleLiveEvent<Unit>()

    var downloadId: Long? = -1L

    fun setTitlePage(title: String) {
        titlePage.value = title
    }

    fun setOrderImage(file: File) {
        val fileTemp = file.compressImage()
        orderImageUrl.set(Uri.fromFile(fileTemp))
    }

    fun resetAddItemForm() {
        productSelected.set(null)
        productType.set("")
        productQty.set("")
    }

    fun searchProduct(keyword: String?) {
        repository.searchProduct(keyword, productType.get(), { response ->
            productSuggestionList.value = response?.products
        }, {})
    }

    fun searchCustomers(keyword: String?) {
        repository.searchCustomer(keyword, { response ->
            tokoSuggestionList.value = response?.customers
        }, {})
    }

    fun isValidAddProduct(): Boolean {
        var result = true
        productTypeError.set("")
        productTypeErrorEnabled.set(false)
        productError.set("")
        productErrorEnabled.set(false)
        productQtyError.set("")
        productQtyErrorEnabled.set(false)

        when {
            productType.get().isNullOrBlank() -> {
                productTypeErrorEnabled.set(true)
                productTypeError.set(getAppContext().getString(R.string.product_type_is_required))
                result = false
            }
        }

        when {
            productSelected.get() == null -> {
                productErrorEnabled.set(true)
                productError.set(getAppContext().getString(R.string.product_is_required))
                result = false
            }
        }

        when {
            productQty.get().orEmpty().filterEmpty("0").toInt() <= 0 -> {
                productQtyErrorEnabled.set(true)
                productQtyError.set(getAppContext().getString(R.string.invalid_quantity))
                result = false
            }
        }

        return result
    }

    fun isValidSubmitOrder(): Boolean {
        var result = true
        consumerNameError.set("")
        consumerNameErrorEnabled.set(false)
        tokoError.set("")
        tokoErrorEnabled.set(false)
        deliveryDateError.set("")
        deliveryDateErrorEnabled.set(false)
        deliveryAddressError.set("")
        deliveryAddressErrorEnabled.set(false)
        mobilePhoneError.set("")
        mobilePhoneErrorEnabled.set(false)
        emailError.set("")
        emailErrorEnabled.set(false)
        notesError.set("")
        notesErrorEnabled.set(false)

        when {
            consumerName.get().isNullOrBlank() && !isSalesRole() -> {
                consumerNameErrorEnabled.set(true)
                consumerNameError.set(getAppContext().getString(R.string.consumer_name_is_required))
                result = false
            }
        }

        when {
            customerSelected.get() == null -> {
                tokoErrorEnabled.set(true)
                tokoError.set(getAppContext().getString(R.string.toko_is_required))
                result = false
            }
        }

        when {
            shipToSelected.get() == null && isSalesRole() -> {
                shipToErrorEnabled.set(true)
                shipToError.set(getAppContext().getString(R.string.ship_to_is_required))
                result = false
            }
        }

        when {
            deliveryDate.get()?.isNullOrBlank() == true -> {
                deliveryDateErrorEnabled.set(true)
                deliveryDateError.set(getAppContext().getString(R.string.delivery_date_is_required))
                result = false
            }
        }

        when {
            deliveryAddress.get()?.isNullOrBlank() == true && !isSalesRole() -> {
                deliveryAddressErrorEnabled.set(true)
                deliveryAddressError.set(getAppContext().getString(R.string.delivery_address_is_required))
                result = false
            }
        }

        when {
            mobilePhone.get()?.isNullOrBlank() == true && !isSalesRole() -> {
                mobilePhoneErrorEnabled.set(true)
                mobilePhoneError.set(getAppContext().getString(R.string.mobile_phone_is_required))
                result = false
            }
        }

        when {
            email.get()?.isNullOrBlank() == true && !isSalesRole() -> {
                emailErrorEnabled.set(true)
                emailError.set(getAppContext().getString(R.string.email_is_required))
                result = false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.get().orEmpty()).matches() && !isSalesRole() -> {
                emailErrorEnabled.set(true)
                emailError.set(getAppContext().getString(R.string.email_is_invalid))
                result = false
            }
        }

        when {
            notes.get()?.isNullOrBlank() == true && !isSalesRole() -> {
                notesErrorEnabled.set(true)
                notesError.set(getAppContext().getString(R.string.notes_is_required))
                result = false
            }
        }

        when {
            orderImageUrl.get() == Uri.EMPTY && !isSalesRole() -> {
                onErrorImageOrder.invoke()
                result = false
            }
        }

        when {
            productList.value.isNullOrEmpty() -> {
                onErrorOrdersItem.invoke()
                result = false
            }
        }

        return result
    }

    fun addProductToList() {
        var list: ArrayList<Product>? = arrayListOf()
        if (!productList.value.isNullOrEmpty()) {
            list = productList.value
        }
        list?.add(
            Product(
                productSelected.get()?.id,
                productType.get(),
                productSelected.get()?.name,
                productSelected.get()?.type,
                productQty.get()
            )
        )
        _productList.value = list
        resetAddItemForm()
    }

    fun getProductList(): ArrayList<ProductItemViewModel> {
        val list = arrayListOf<ProductItemViewModel>()
        productList.value?.forEach {
            list.add(ProductItemViewModel(it.id, it.type, it.name, it.qty) { id ->
                val tempList = productList.value
                val product = tempList?.find { it.id == id }
                tempList?.remove(product)
                _productList.value = tempList
            })
        }

        return list
    }

    fun isSalesRole(): Boolean {
        return sessionStorage.get(Constants.ROLE, Role::class.java).hierarchy.orEmpty()
            .equals(Constants.SALES, true)
    }

    private fun getImageData(): String? {
        return if (orderImageUrl.get() != null && orderImageUrl.get() != Uri.EMPTY) {
            File(orderImageUrl.get()?.path.orEmpty()).encodeToBase64()
        } else {
            null
        }
    }

    fun submitOrder(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        try {
            val ordersData = OrdersData(
                customerId = customerSelected.get()?.id.toString(),
                shipToId = shipToSelected.get()?.id.toString(),
                consumer = consumerName.get(),
                phoneNumber = mobilePhone.get(),
                email = email.get(),
                imageData = getImageData(),
                fileName = Constants.EROYAL_FILENAME + System.currentTimeMillis() + ".jpg",
                contentType = IMAGE_TYPE,
                sendDate = DateUtils.formateDate(
                    deliveryDate.get(),
                    DateUtils.DD_MMMM_YYYY,
                    DateUtils.DD_MM_YYYY
                ),
                note = notes.get(),
                address = deliveryAddress.get(),
                productList = productList.value
            )

            val request = OrdersRequest(ordersData)

            repository.submitOrder(request, {
                it?.data?.createdAt =
                    DateUtils.formateDate(it?.data?.createdAt, YYYY_MM_DDTHH_MM_SSSZ, DD_MMMM_YYYY)
                it?.data?.address = it?.data?.address.filterEmpty("-")
                it?.data?.email = it?.data?.email.filterEmpty("-")
                it?.data?.phoneNumber = it?.data?.phoneNumber.filterEmpty("-")
                summaryOrdersData.set(it?.data)
                cbOnSuccess.invoke()
            }, {
                cbOnError.invoke(it)
            })
        } catch (e: Exception) {
            chuckerCollector.onError(OrdersViewModel::class.java.simpleName, e)
        }
    }

    fun getSoDocs() {
        val request = DownloadManager.Request(
            Uri.parse(
                JNIUtil.apiEndpoint().plus(Constants.DOC_URL).plus(summaryOrdersData.get()?.orderId)
                    .plus("/download_pdf")
            )
        )
            .setTitle(getFileName())
            .setDescription(getAppContext().getString(R.string.downloading_so))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getFileName())

        val manager = (getAppContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
        downloadId = manager.enqueue(request)
    }

    private fun getFileName(): String {
        return summaryOrdersData.get()?.soNumber.plus("-").plus(summaryOrdersData.get()?.orderId).plus(".pdf")
    }

}
