package com.worka.eroyal.feature.myteam

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.customcomponent.SingleLiveEvent
import com.worka.eroyal.data.me.SalesValue
import com.worka.eroyal.data.report.SalesTeam
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.ofPrefix
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.bill.CellItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.HeaderColumnItemViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import com.worka.eroyal.repository.MyTeamRepository
import com.worka.eroyal.repository.ReportRepository
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */
class MyTeamViewModel(application: Application): BaseViewModel(application) {
    val repository: MyTeamRepository by inject()
    val reportRepository: ReportRepository by inject()

    var navController: NavController? = null

    var selectedMonth = MutableLiveData<String>()
    private val _myTeamItemViewModel = MutableLiveData<ArrayList<MyTeamItemViewModel>>()
    var myTeamItemViewModel: LiveData<ArrayList<MyTeamItemViewModel>> = _myTeamItemViewModel
    private val _rkbVisitsItemViewModel = MutableLiveData<ArrayList<SimpleViewModel>>()
    var rkbVisitsItemViewModel: LiveData<ArrayList<SimpleViewModel>> = _rkbVisitsItemViewModel
    private val _valueVisitsItemViewModel = MutableLiveData<ArrayList<SimpleViewModel>>()
    var valueVisitsItemViewModel: LiveData<ArrayList<SimpleViewModel>> = _valueVisitsItemViewModel
    private val _salesQtyItemViewModel = MutableLiveData<ArrayList<SimpleViewModel>>()
    var salesQtyItemViewModel: LiveData<ArrayList<SimpleViewModel>> = _salesQtyItemViewModel


    var visitChartData = ObservableField(arrayOf(0, 0))
    var rkbChartData = ObservableField(arrayOf(0, 0))
    var chartDatas = ObservableField<Array<Array<Int>?>>()
    var colorLineCharts = ObservableField<Array<Int?>>()

    var onSelectMyTeam = SingleLiveEvent<Unit>()

    var emptyMyTeamVisibility = ObservableField(true)
    var myTeamPage = 1
    var isLoadingMyTeam = false
    var isLastPageMyTeam = false

    var rkbVisitErrorText = ObservableField(getAppContext().getString(R.string.no_data))
    var salesValueErrorText = ObservableField(getAppContext().getString(R.string.no_data))
    var salesQtyErrorText = ObservableField(getAppContext().getString(R.string.no_data))
    var errorRKBVisitsVisibility = ObservableField(true)
    var errorSalesValueVisibility = ObservableField(true)
    var errorSalesQtyVisibility = ObservableField(true)

    var areaFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var quantityBrandFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var valueBrandFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var searchKeyword = ObservableField("")

    var areaFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var quantityBrandFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var valueBrandFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var selectedSales = ObservableField<SalesTeam>()

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
            quantityBrandFilterList.clear()
            valueBrandFilterList.clear()
            it?.brands?.forEach {
                brandFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                quantityBrandFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                valueBrandFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
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

    fun setQuantityBrandFilterTitle() {
        quantityBrandFilterTitle.set(getFilterTitle(quantityBrandFilterList))
    }

    fun setValueBrandFilterTitle() {
        valueBrandFilterTitle.set(getFilterTitle(valueBrandFilterList))
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

    fun getMyTeam(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        if (!isLoadingMyTeam) {
            isLoadingMyTeam = true

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

            if (brandIds?.size == brandFilterList.size) {
                brandIds = null
            }

            repository.getMyTeam(brandIds, areaIds, searchKeyword.get(), myTeamPage, {
                if (it.users.size < Constants.LIMIT_GET_DATA) {
                    isLastPageMyTeam = true
                }

                if (myTeamPage == 1) {
                    if (it.users.isEmpty()) {
                        emptyMyTeamVisibility.set(true)
                    } else {
                        emptyMyTeamVisibility.set(false)
                        setupMyTeamList(it.users)
                    }
                } else {
                    setupMyTeamList(it.users)
                }
                myTeamPage++
                isLoadingMyTeam = false
                cbOnSuccess.invoke()
            }, {
                isLoadingMyTeam = false
                cbOnError.invoke(it)
            })
        }
    }

    private fun setupMyTeamList(salesList: ArrayList<SalesTeam>) {
        val list = arrayListOf<MyTeamItemViewModel>()
        salesList.forEach { sales ->
            list.add(MyTeamItemViewModel(sales.id, sales.contactPersonAvatar, sales.name, sales.lastActivity.customerName, sales.lastActivity.dateTime, sales.taskCount.toString().ofPrefix(),
                sales.visitCount.toString(), sales.otherVisitCount.toString(), sales.failedCount.toString()) {
                selectedSales.set(sales)
                onSelectMyTeam.invoke()
            })
        }
        val temp = myTeamItemViewModel.value
        if (!temp.isNullOrEmpty() && myTeamPage > 1) {
            temp.addAll(list)
            _myTeamItemViewModel.value = temp
        } else {
            _myTeamItemViewModel.value = list
        }
    }

    fun getRKBVisits() {
        errorRKBVisitsVisibility.set(false)
        repository.getTeamStatistic(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            selectedSales.get()?.id.toString(), {
                it?.statistic?.visited?.let { visit ->
                    setupRKBVisitsData(visit.chartRKB, visit.chartVisit)
                    visitChartData.set(visit.chartVisit)
                    rkbChartData.set(visit.chartRKB)
                }
                colorLineCharts.set(arrayOf(R.color.colorGrey, R.color.colorGreen))
                chartDatas.set(arrayOf(rkbChartData.get(), visitChartData.get()))
            },
            {
                rkbVisitErrorText.set(it)
                errorRKBVisitsVisibility.set(true)
            })
    }

    private fun setupRKBVisitsData(rkbList: Array<Int>?, visitList: Array<Int>?){
        val list = arrayListOf<SimpleViewModel>()

        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.date)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.rkb)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.visit)))

        rkbList?.forEachIndexed { index, rkb ->
            list.add(CellItemViewModel((index + 1).toString()))
            list.add(CellItemViewModel(rkb.toString()))
            list.add(CellItemViewModel(visitList?.get(index).toString()))
        }
        _rkbVisitsItemViewModel.value = list
    }

    fun getSalesValue() {
        errorSalesValueVisibility.set(false)

        var brandIds: ArrayList<Int?>?  = arrayListOf()

        valueBrandFilterList.forEach {
            if (it.isSelected) {
                brandIds?.add(it.id)
            }
        }
        repository.getSalesValue(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            selectedSales.get()?.id.toString(),
            brandIds, {
                it?.let {
                   setupSalesValueData(it)
                }
            },
            {
                salesValueErrorText.set(it)
                errorSalesValueVisibility.set(true)
            })
    }

    private fun setupSalesValueData(valueList: ArrayList<SalesValue>) {
        val list = arrayListOf<SimpleViewModel>()

        list.add(HeaderColumnItemViewModel(""))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.target)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.achievement)))
        list.add(HeaderColumnItemViewModel("%"))

        valueList.forEach { value ->
            list.add(CellItemViewModel(value.month))
            list.add(CellItemViewModel(value.target.formatThousandSeparator("-")))
            list.add(CellItemViewModel(value.value.formatThousandSeparator("-")))
            list.add(CellItemViewModel(value.percentage))
        }
        _valueVisitsItemViewModel.value = list
    }

    fun getSalesQuantity() {
        errorSalesQtyVisibility.set(false)

        var brandIds: ArrayList<Int?>?  = arrayListOf()

        quantityBrandFilterList.forEach {
            if (it.isSelected) {
                brandIds?.add(it.id)
            }
        }

        repository.getSalesQuantity(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            selectedSales.get()?.id.toString(),
            brandIds
            ,{
                it?.let {
                    setupSalesQuantityData(it)
                }
            },
            {
                salesQtyErrorText.set(it)
                errorSalesQtyVisibility.set(true)
            })
    }

    private fun setupSalesQuantityData(valueList: ArrayList<SalesValue>) {
        val list = arrayListOf<SimpleViewModel>()

        list.add(HeaderColumnItemViewModel(""))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.target)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.achievement)))
        list.add(HeaderColumnItemViewModel("%"))

        valueList.forEach { value ->
            list.add(CellItemViewModel(value.month))
            list.add(CellItemViewModel(value.target.formatThousandSeparator("-")))
            list.add(CellItemViewModel(value.value.formatThousandSeparator("-")))
            list.add(CellItemViewModel(value.percentage))
        }
        _salesQtyItemViewModel.value = list
    }
}
