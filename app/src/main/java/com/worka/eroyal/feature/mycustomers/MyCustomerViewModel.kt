package com.worka.eroyal.feature.mycustomers

import android.app.Application
import android.graphics.Color
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.ACTIVE_CUSTOMER
import com.worka.eroyal.data.mycustomer.BrandMarket
import com.worka.eroyal.data.mycustomer.OutStandingOrder
import com.worka.eroyal.data.mycustomer.PromoCustomer
import com.worka.eroyal.data.mycustomer.SalesCustomerData
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.tasks.TaskResponseData
import com.worka.eroyal.extensions.*
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.bill.CellItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.HeaderColumnItemViewModel
import com.worka.eroyal.feature.mycustomers.market.MarketChartViewModel
import com.worka.eroyal.feature.mycustomers.notes.NotesItemViewModel
import com.worka.eroyal.feature.mycustomers.promo.PromoItemViewModel
import com.worka.eroyal.feature.mycustomers.sales.SalesCustomerItemViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import com.worka.eroyal.repository.MyCustomerRepository
import com.worka.eroyal.repository.ReportRepository
import com.worka.eroyal.utils.getMonthYear
import org.koin.core.inject
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-13.
 */
class MyCustomerViewModel(application: Application) : BaseViewModel(application) {

    val repository: MyCustomerRepository by inject()
    val reportRepository: ReportRepository by inject()

    lateinit var navController: NavController
    var customerList = arrayListOf<Customer>()
    private val _customerViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val customerViewModelList: LiveData<ArrayList<SimpleViewModel>> = _customerViewModelList
    private val _noteViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val noteViewModelList: LiveData<ArrayList<SimpleViewModel>> = _noteViewModelList
    var imageList = arrayListOf<PromoCustomer>()
    private val _promoViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val promoViewModelList: LiveData<ArrayList<SimpleViewModel>> = _promoViewModelList
    private val _outStandingOrderViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val outStandingOrderViewModelList: LiveData<ArrayList<SimpleViewModel>> = _outStandingOrderViewModelList
    val marketViewModelList = arrayListOf<MarketChartViewModel>()
    val salesViewModelList = arrayListOf<SalesCustomerItemViewModel>()
    var selectedCustomer: Customer? = null
    var currentMonth = ObservableField("")
    var remainingBill = ObservableField("")
    var emptyOutStandingOrderVisibility = ObservableField(false)

    var areaFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandFilterList = arrayListOf<CheckBoxFilterItemViewModel>()

    var areaFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))

    var searchKeyword = ObservableField("")

    var page = 1
    var notesPage = 1
    var promoPage = 1
    var outStandingOrderPage = 1
    var isLoading = false
    var isPromoLoading = false
    var isOutStandingOrderLoading = false
    var isLastPage = false
    var isLastNotesPage = false
    var isLastPromoPage = false
    var isLastOutStandingOrderPage = false

    fun setDefaultMonth(){
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        currentMonth.set(calendar.getMonthYear())
    }

    fun getAreaFilterList(cbOnSuccess: () -> Unit) {
        reportRepository.getAreas({
            areaFilterList.clear()
            it?.areas?.forEach { area ->
                areaFilterList.add(CheckBoxFilterItemViewModel(area.id, area.name))
            }
            cbOnSuccess.invoke()
        }, {})
    }


    fun getBrandFilterList(cbOnSuccess: () -> Unit) {
        reportRepository.getBrands({
            brandFilterList.clear()
            it?.brands?.forEach {
                brandFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
            }
            cbOnSuccess.invoke()
        }, {})
    }

    fun setAreaFilterTitle() {
        areaFilterTitle.set(getFilterTitle(areaFilterList))
    }

    fun setBrandFilterTitle() {
        brandFilterTitle.set(getFilterTitle(brandFilterList))
    }

    private fun getFilterTitle(filterList: java.util.ArrayList<CheckBoxFilterItemViewModel>): String {
        var filterActiveCount = 0
        var filterTitleTemp = ""
        filterList.forEach { filter ->
            if (filter.isSelected) {
                filterActiveCount += 1
                if (filterActiveCount == 1) {
                    filterTitleTemp = filter.name.orEmpty()
                }
            }

            when (filterActiveCount) {
                in 2 until filterList.size -> filterTitleTemp = getAppContext().getString(R.string.custom)
                filterList.size -> filterTitleTemp = getAppContext().getString(R.string.all)
            }
        }
        return filterTitleTemp
    }

    fun getMyCustomer(cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit, cbOnSelectedCustomer:() -> Unit) {

        var areaIds: ArrayList<Int?>?  = arrayListOf()
        var brandIds: ArrayList<Int?>?  = arrayListOf()

        areaFilterList.forEach {
            if (it.isSelected) {
                areaIds?.add(it.id)
            }
        }
        brandFilterList.forEach {
            if (it.isSelected) {
                brandIds?.add(it.id)
            }
        }

        if (areaIds?.size == areaFilterList.size) {
            areaIds = null
        }

        if (brandIds?.size == areaFilterList.size) {
            brandIds = null
        }

        repository.getCustomers(brandIds, areaIds, searchKeyword.get(), page, {
            if (page == 1) {
                customerList.clear()
            }
            if (it.customers.size < Constants.LIMIT_GET_DATA) {
                isLastPage = true
            }
            isLoading = false
            customerList.addAll(it.customers)
            setCustomersData(it.customers, cbOnSelectedCustomer)
            page++
            cbOnSuccess.invoke()
        }, {
            isLoading = false
            cbOnError.invoke(it)
        })
    }

    private fun setCustomersData(customers: ArrayList<Customer>, cbOnSelectedCustomer:() -> Unit) {
        val list = arrayListOf<SimpleViewModel>()
        customers.forEach {
            list.add(CustomerItemViewModel(it.id,"", it.contactPersonAvatar, it.name.orEmpty().trim(), it.address, it.state) { id ->
                selectedCustomer = customerList.find { it.id == id }
                cbOnSelectedCustomer.invoke()
            })
        }
        val customersTemp = customerViewModelList.value
            if (!customersTemp.isNullOrEmpty() && page > 1) {
                customersTemp.addAll(list)
                _customerViewModelList.value = customersTemp
            } else {
                _customerViewModelList.value = list
            }
    }

    fun getStateIcon() : Int {
        return when (selectedCustomer?.state) {
            ACTIVE_CUSTOMER -> R.drawable.ic_crown_active
            else -> R.drawable.ic_crown_inactive
        }
    }

    fun getStateMessage() : String? {
        return when (selectedCustomer?.state) {
            ACTIVE_CUSTOMER -> getAppContext().resources.getString(R.string.active)
            else -> selectedCustomer?.state
        }
    }

    fun getCustomerDetails(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        repository.getCustomerDetails(selectedCustomer?.id, {
            selectedCustomer = it.customer
            cbOnSuccess.invoke()
        },{
            cbOnError.invoke(it)
        })
    }

    fun getNotes(cbOnError: (String?) -> Unit) {
        repository.getCustomerNotes(selectedCustomer?.id, notesPage, {
            if (notesPage == 1) {
                _noteViewModelList.value = arrayListOf()
            }
            if (it.visitedList.size < Constants.LIMIT_GET_DATA) {
                isLastNotesPage = true
            }
            notesPage++
            isLoading = false
            setNotesData(it.visitedList)
        }, {
            isLoading = false
            cbOnError.invoke(it)
        })
    }

    fun setNotesData(customers: ArrayList<TaskResponseData>) {
        val list = arrayListOf<SimpleViewModel>()
        customers.forEach {
            list.add(NotesItemViewModel(it.customer?.contactPersonAvatar, it.customer?.name, it.createdDate, it.notes.quotePrefix()))
        }
        val notesTemp = noteViewModelList.value
        if (!notesTemp.isNullOrEmpty() && notesPage > 1) {
            notesTemp.addAll(list)
            _noteViewModelList.value = notesTemp
        } else {
            _noteViewModelList.value = list
        }
    }

    fun getPromo(cbOnError: (String?) -> Unit, cbOnSelected:(Int, String) -> Unit) {
        repository.getCustomerPromo(selectedCustomer?.id, promoPage, {
            if (promoPage == 1) {
                _promoViewModelList.value = arrayListOf()
                imageList.clear()
            }
            if (it.images.size < Constants.LIMIT_GET_DATA) {
                isLastPromoPage = true
            }
            isPromoLoading = false
            promoPage++
            imageList.addAll(it.images)
            setPromoData(it.images, cbOnSelected)
        }, {
            isPromoLoading = false
            cbOnError.invoke(it)
        })
    }

    fun setPromoData(images: ArrayList<PromoCustomer>, cbOnSelected:(Int, String) -> Unit) {
        imageList = images
        val list = arrayListOf<SimpleViewModel>()
        images.forEach {
            list.add(PromoItemViewModel(it.imageUrl){ url ->
                val position = imageList.indexOf(it)
                cbOnSelected.invoke(position, url)
            })
        }
        val promoTemp = promoViewModelList.value
        if (!promoTemp.isNullOrEmpty() && promoPage > 1) {
            promoTemp.addAll(list)
            _promoViewModelList.value = promoTemp
        } else {
            _promoViewModelList.value = list
        }
    }

    fun getImageUrls(): ArrayList<String> {
        val list = arrayListOf<String>()
        imageList.map {
            list.add(it.imageUrl)
        }
        return list
    }

    fun getMarketCustomer(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        repository.getMarketCustomer(selectedCustomer?.id, {
            setMarketCustomerList(it.markets)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setMarketCustomerList(list: ArrayList<BrandMarket>){
        marketViewModelList.clear()
        list.forEach {
            marketViewModelList.add(MarketChartViewModel(it.name, it.percentage.marketPercentPrefix(), it.competitors, getColors(it.competitors.size + 1)))
        }
    }

    private fun getColors(size:Int): ArrayList<Int> {
        val list = arrayListOf<Int>()
        for (i in 0..size){
            list.add(getRandomColor())
        }

        return list
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    fun getSalesCustomer(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.getSalesCustomer(selectedCustomer?.id, currentMonth.get(), { response ->
            setSalesCustomerList(response.salesDatas)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setSalesCustomerList(datas: ArrayList<SalesCustomerData>?) {
        salesViewModelList.clear()
        datas?.forEach {
                salesViewModelList.add(
                    SalesCustomerItemViewModel(it.brandName, it.salesData)
                )
        }
    }

    fun getRemainingBill() {
        repository.getRemainingBill(selectedCustomer?.id, {
            remainingBill.set(it.remainingBill?.formatThousandSeparator())
        }, {

        })
    }

    fun getOutStandingOrder(cbOnError: (String?) -> Unit) {
        repository.getOutStandingOrder(selectedCustomer?.id, outStandingOrderPage, {
            if (it.orders.size < Constants.LIMIT_GET_DATA) {
                isLastOutStandingOrderPage = true
            }
            isOutStandingOrderLoading = false
            if (it.orders.isNullOrEmpty()) {
                emptyOutStandingOrderVisibility.set(true)
            } else {
                emptyOutStandingOrderVisibility.set(false)
                setupOutStandingOrderData(it.orders)
            }
            outStandingOrderPage++
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupOutStandingOrderData(visitPlans: ArrayList<OutStandingOrder>) {
        val list = arrayListOf<SimpleViewModel>()
        val tempList = outStandingOrderViewModelList.value
        if (outStandingOrderPage == 1) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.id)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.brand)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.item_number)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.description)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.ship_to)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.short_item)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.next_status)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.branch_description)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer_desc)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.total)))
        }
        visitPlans.forEach {
            list.add(CellItemViewModel(it.id.toString().filterEmpty("-")))
            list.add(CellItemViewModel(it.brand.filterEmpty("-")))
            list.add(CellItemViewModel(it.itemNumber.filterEmpty("-")))
            list.add(CellItemViewModel(it.description.filterEmpty("-")))
            list.add(CellItemViewModel(it.shipTo.filterEmpty("-")))
            list.add(CellItemViewModel(it.shortItem.filterEmpty("-")))
            list.add(CellItemViewModel(it.nextStatus.toString().filterEmpty("-")))
            list.add(CellItemViewModel(it.branchDesc.filterEmpty("-")))
            list.add(CellItemViewModel(it.customerDesc.filterEmpty("-")))
            list.add(CellItemViewModel(it.total.toString().filterEmpty("-")))
        }
        if (!tempList.isNullOrEmpty() && outStandingOrderPage > 1) {
            tempList.addAll(list)
            _outStandingOrderViewModelList.value = tempList
        } else {
            _outStandingOrderViewModelList.value = list
        }
    }

}
