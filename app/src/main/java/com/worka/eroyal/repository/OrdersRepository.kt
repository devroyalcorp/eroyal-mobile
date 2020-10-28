package com.worka.eroyal.repository

import com.worka.eroyal.data.mycustomer.CustomersResponse
import com.worka.eroyal.data.orders.OrdersRequest
import com.worka.eroyal.data.orders.OrdersResponse
import com.worka.eroyal.data.orders.ProductsResponse
import com.worka.eroyal.storage.SessionStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 28/06/20.
 */
class OrdersRepository(var serviceApi: ServiceApi) : KoinComponent {
    private val sessionStorage: SessionStorage by inject()
    private var observable: Observable<ProductsResponse>? = null
    private var observableCustomer: Observable<CustomersResponse>? = null

    fun searchProduct(
        keyword: String?,
        productType: String?,
        cbOnSuccess: (ProductsResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        observable?.unsubscribeOn(Schedulers.io())
        observable = searchProductsAPI(keyword, productType).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
        observable?.subscribe(object :
            DisposableObserver<ProductsResponse>() {
            override fun onComplete() {
                dispose()
            }

            override fun onNext(value: ProductsResponse) {
                cbOnSuccess.invoke(value)
            }

            override fun onError(e: Throwable) {
                cbOnError.invoke(e.message)
                dispose()
            }
        })
    }

    private fun searchProductsAPI(
        keyword: String?,
        productType: String?
    ): Observable<ProductsResponse> {
        return serviceApi.searchProduct(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            keyword,
            productType
        )
    }

    fun searchCustomer(
        keyword: String?,
        cbOnSuccess: (CustomersResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        observableCustomer?.unsubscribeOn(Schedulers.io())
        observableCustomer = searchCustomersAPI(keyword).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
        observableCustomer?.subscribe(object :
            DisposableObserver<CustomersResponse>() {
            override fun onComplete() {
                dispose()
            }

            override fun onNext(value: CustomersResponse) {
                cbOnSuccess.invoke(value)
            }

            override fun onError(e: Throwable) {
                cbOnError.invoke(e.message)
                dispose()
            }
        })
    }

    private fun searchCustomersAPI(
        keyword: String?
    ): Observable<CustomersResponse> {
        return serviceApi.searchCustomer(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            keyword
        )
    }

    fun submitOrder(
        request: OrdersRequest, cbOnSuccess: (OrdersResponse?) -> Unit,
        cbOnError: (String?) -> Unit
    ) {
        submitOrderAPI(request).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object :
                DisposableObserver<OrdersResponse>() {
                override fun onComplete() {
                    dispose()
                }

                override fun onNext(value: OrdersResponse) {
                    cbOnSuccess.invoke(value)
                }

                override fun onError(e: Throwable) {
                    cbOnError.invoke(e.message)
                    dispose()
                }
            })
    }

    private fun submitOrderAPI(
        request: OrdersRequest
    ): Observable<OrdersResponse> {
        return serviceApi.submitOrder(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            request
        )
    }
}
