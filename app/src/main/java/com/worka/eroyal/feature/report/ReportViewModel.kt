package com.worka.eroyal.feature.report

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.BRANCH
import com.worka.eroyal.base.Constants.DAILY
import com.worka.eroyal.base.Constants.FREE_VISIT
import com.worka.eroyal.base.Constants.LIMIT_GET_DATA
import com.worka.eroyal.base.Constants.MONTHLY
import com.worka.eroyal.base.Constants.SELECTED_CUSTOMER
import com.worka.eroyal.component.customcomponent.SingleLiveEvent
import com.worka.eroyal.data.json
import com.worka.eroyal.data.mycustomer.CustomersResponse
import com.worka.eroyal.data.report.AreaReport
import com.worka.eroyal.data.report.BranchReport
import com.worka.eroyal.data.report.BranchSalesReport
import com.worka.eroyal.data.report.ChartReport
import com.worka.eroyal.data.report.CustomerActiveDetail
import com.worka.eroyal.data.report.CustomerActiveRecap
import com.worka.eroyal.data.report.CustomerDetailsReport
import com.worka.eroyal.data.report.DailyVisit
import com.worka.eroyal.data.report.Sales
import com.worka.eroyal.data.report.SalesByArticleReport
import com.worka.eroyal.data.report.SalesByCustomerReport
import com.worka.eroyal.data.report.SalesTeam
import com.worka.eroyal.data.report.Stock
import com.worka.eroyal.data.report.WorkPlan
import com.worka.eroyal.data.report.filter.ArticleReportSortBy
import com.worka.eroyal.data.report.filter.CustomerReportSortBy
import com.worka.eroyal.data.report.filter.SortDirection
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.visits.Branch
import com.worka.eroyal.extensions.filterEmpty
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.quotePrefix
import com.worka.eroyal.extensions.shortNumberFormat
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.CustomerItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.CellItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.HeaderColumnItemViewModel
import com.worka.eroyal.feature.report.byarea.AreaChartItemViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import com.worka.eroyal.feature.report.byarea.ReportTypeItemViewModel
import com.worka.eroyal.feature.report.byarea.branch.BranchReportItemViewModel
import com.worka.eroyal.feature.report.byarea.sales.BranchSalesReportItemViewModel
import com.worka.eroyal.feature.report.bybrands.BrandReportViewModel
import com.worka.eroyal.feature.report.bysales.SalesViewModel
import com.worka.eroyal.feature.report.dailyvisitreport.DailyReportMyTeamItemViewModel
import com.worka.eroyal.feature.report.dailyvisitreport.DailyVisitReportItemViewModel
import com.worka.eroyal.feature.report.monthlyvisitreport.MonthlyReportMyTeamItemViewModel
import com.worka.eroyal.feature.report.monthlyvisitreport.MonthlyVisitReportItemViewModel
import com.worka.eroyal.repository.ReportRepository
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.DateUtils.DD_MM_YYYY
import com.worka.eroyal.utils.DateUtils.HH_MM_SS
import com.worka.eroyal.utils.DateUtils.HHh_MMm_SSs
import com.worka.eroyal.utils.getDateFormat
import com.worka.eroyal.utils.getDayMonth
import com.worka.eroyal.utils.getMonthYear
import com.worka.eroyal.utils.getVisitSpentTime
import org.koin.core.inject
import java.text.DateFormatSymbols
import java.util.*

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-16.
 */
class ReportViewModel(application: Application) : BaseViewModel(application) {
    val repository: ReportRepository by inject()

    lateinit var navController: NavController
    val titlePage = MutableLiveData<String>()

    val monthPickerVisibility = ObservableField(View.VISIBLE)
    var mostVisitedCustomers = arrayListOf<CustomerItemViewModel>()
    var mostPlannedCustomers = arrayListOf<CustomerItemViewModel>()
    var byBranchCustomers = arrayListOf<CustomerItemViewModel>()
    var byBrandsCustomers = arrayListOf<BrandReportViewModel>()
    var bySalesReports = arrayListOf<SalesViewModel>()
    var areaChartList = arrayListOf<AreaChartItemViewModel>()
    var reportInfoList = arrayListOf<ReportTypeItemViewModel>()
    var branchReportList = arrayListOf<BranchReport>()
    private val _branchesReportViewModelList = MutableLiveData<ArrayList<BranchReportItemViewModel>>()
    val branchesReportViewModelList: LiveData<ArrayList<BranchReportItemViewModel>> = _branchesReportViewModelList
    var chartData = arrayListOf<ChartReport>()
    var stockData = arrayListOf<Stock>()
    var areaFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandSalesFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var areaDailyFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandDailyFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var areaMonthlyFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandMonthlyFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var areaSalesByArticleFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandSalesByArticleFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var areaSalesByCustomerFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var brandSalesByCustomerFilterList = arrayListOf<CheckBoxFilterItemViewModel>()
    var areaChartReportList = arrayListOf<AreaReport>()
    private val _salesChartReportList = MutableLiveData<ArrayList<ChartReport>>()
    var salesChartReportList: LiveData<ArrayList<ChartReport>> = _salesChartReportList
    private val _customerReportViewModelList = MutableLiveData<ArrayList<CustomerItemViewModel>>()
    val customerReportViewModelList: LiveData<ArrayList<CustomerItemViewModel>> = _customerReportViewModelList
    private val _branchSalesReportViewModelList = MutableLiveData<ArrayList<BranchSalesReportItemViewModel>>()
    val branchSalesReportViewModelList: LiveData<ArrayList<BranchSalesReportItemViewModel>> = _branchSalesReportViewModelList
    private val _dailyReportMyTeamViewModelList = MutableLiveData<ArrayList<DailyReportMyTeamItemViewModel>>()
    val dailyReportMyTeamViewModelList: LiveData<ArrayList<DailyReportMyTeamItemViewModel>> = _dailyReportMyTeamViewModelList
    private val _monthlyReportMyTeamViewModelList = MutableLiveData<ArrayList<MonthlyReportMyTeamItemViewModel>>()
    val monthlyReportMyTeamViewModelList: LiveData<ArrayList<MonthlyReportMyTeamItemViewModel>> = _monthlyReportMyTeamViewModelList
    private val _visitDailyReportViewModelList = MutableLiveData<ArrayList<DailyVisitReportItemViewModel>>()
    val visitDailyReportViewModelList: LiveData<ArrayList<DailyVisitReportItemViewModel>> = _visitDailyReportViewModelList
    private val _visitMonthlyReportViewModelList = MutableLiveData<ArrayList<MonthlyVisitReportItemViewModel>>()
    val visitMonthlyReportViewModelList: LiveData<ArrayList<MonthlyVisitReportItemViewModel>> = _visitMonthlyReportViewModelList
    private val _salesReportByArticleViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val salesReportByArticleViewModelList: LiveData<ArrayList<SimpleViewModel>> = _salesReportByArticleViewModelList
    private val _salesReportByCustomerViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val salesReportByCustomerViewModelList: LiveData<ArrayList<SimpleViewModel>> = _salesReportByCustomerViewModelList
    private val _customerActiveRecapViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val customerActiveRecapViewModelList: LiveData<ArrayList<SimpleViewModel>> = _customerActiveRecapViewModelList
    private val _customerActiveDetailViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val customerActiveDetailViewModelList: LiveData<ArrayList<SimpleViewModel>> = _customerActiveDetailViewModelList
    private val _weeklyWorkPlanViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val weeklyWorkPlanViewModelList: LiveData<ArrayList<SimpleViewModel>> = _weeklyWorkPlanViewModelList
    private val _salesReportViewModelList = MutableLiveData<ArrayList<SimpleViewModel>>()
    val salesReportViewModelList: LiveData<ArrayList<SimpleViewModel>> = _salesReportViewModelList
    var selectedDateDailyReport = ObservableField(Calendar.getInstance().getDateFormat(DD_MM_YYYY))
    var selectedMonthMonthlyReport = ObservableField(Calendar.getInstance().getMonthYear())
    var dateDailyReportDetails = ObservableField("")
    var monthMonthlyReportDetails = ObservableField("")
    val selectedSalesDailyReport = ObservableField<Sales>()
    val selectedSalesMonthlyReport = ObservableField<Sales>()

    var emptyMostVisitedVisibility = ObservableField(true)
    var emptyMostPlannedVisibility = ObservableField(true)
    var emptyByBranchVisibility = ObservableField(true)
    var emptyByBrandsVisibility = ObservableField(true)
    var emptyBySalesVisibility = ObservableField(true)
    var emptyAreaListVisibility = ObservableField(false)
    var emptyCustomerByBranchVisibility = ObservableField(false)
    var emptyDailyReportVisibility = ObservableField(false)
    var emptyMonthlyReportVisibility = ObservableField(false)
    var emptyVisitReportVisibility = ObservableField(false)
    var emptyMonthlyVisitReportVisibility = ObservableField(false)
    var emptySalesReportByArticleVisibility = ObservableField(false)
    var emptySalesReportByCustomerVisibility = ObservableField(false)
    var emptyCustomerActiveRecapVisibility = ObservableField(false)
    var emptyCustomerActiveDetailVisibility = ObservableField(false)
    var emptyWeeklyWorkPlanVisibility = ObservableField(false)
    var areaFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var areaDailyFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandDailyFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var areaMonthlyFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandMonthlyFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var areaSalesFilterTitle = ObservableField(getAppContext().getString(R.string.area))
    var brandSalesFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var areaSalesReportByArticleFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandSalesReportByArticleFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var areaSalesReportByCustomerFilterTitle = ObservableField(getAppContext().getString(R.string.all_area))
    var brandSalesReportByCustomerFilterTitle = ObservableField(getAppContext().getString(R.string.all_brand))
    var dateDailyFilterTitle = ObservableField(Calendar.getInstance().getDayMonth())
    var dateMonthlyFilterTitle = ObservableField(Calendar.getInstance().getMonthYear())
    var searchKeywordReportArticle = ObservableField("")
    var searchKeywordReportCustomer = ObservableField("")
    var sortByTitleSalesReportArticle = ObservableField(getAppContext().getString(R.string.sort_by).plus("    ").plus(getAppContext().getString(R.string.default_text)))
    var sortDirectionTitleSalesReportArticle = ObservableField(getAppContext().getString(R.string.sort).plus("    ").plus(getAppContext().getString(R.string.default_text)))
    var selectedSortBySalesReportArticle = ObservableField<ArticleReportSortBy>()
    var selectedSortDirectionSalesReportArticle = ObservableField<SortDirection>()
    var sortByTitleSalesReportCustomer = ObservableField(getAppContext().getString(R.string.sort_by).plus("    ").plus(getAppContext().getString(R.string.default_text)))
    var sortDirectionTitleSalesReportCustomer = ObservableField(getAppContext().getString(R.string.sort).plus("    ").plus(getAppContext().getString(R.string.default_text)))
    var selectedSortBySalesReportCustomer = ObservableField<CustomerReportSortBy>()
    var selectedSortDirectionSalesReportCustomer = ObservableField<SortDirection>()

    var selectedSalesAreaId : Int? = 0

    var selectedMonth = MutableLiveData<String>()
    var currentMonth = ObservableField("")
    var isGetStatisticEnabled = true
    var byBranchTitle = ObservableField(getAppContext().getString(R.string.title_by_branch, sessionStorage.get(BRANCH, Branch::class.java).name))
    var selectedReportGraphTitle = ObservableField("")
    var selectedStockReportGraphTitle = ObservableField("")
    var selectedReportAreaId: Int? = null
    var selectedReportBranchId: Int? = null
    var selectedAreaReport = ObservableField<AreaReport>()
    var onSelectedReportType = MutableLiveData<String>()
    var defaultStateSalesPageVisibility = ObservableField(true)
    var onFinishedGetData = SingleLiveEvent<Unit>()
    var onErrorGetData = SingleLiveEvent<String?>()
    var onUpdateAreaChart = SingleLiveEvent<Unit?>()
    var isLoadingShow = ObservableField(true)
    var onOpenDailyReportDetails = SingleLiveEvent<Int?>()
    var onOpenMonthlyReportDetails = SingleLiveEvent<Int?>()
    var onSalesReportTabCheckPage = SingleLiveEvent<Unit>()

    var pageBranch = 1
    var isLoadingBranch = false
    var isLastPageBranch = false
    var pageCustomer = 1
    var isLoadingCustomer = false
    var isLastPageCustomer = false
    var pageSales = 1
    var isLoadingSales = false
    var isLastPageSales = false
    var pageDailyReport = 1
    var isLoadingDailyReport = false
    var isLastPageDailyReport = false
    var pageMonthlyReport = 1
    var isLoadingMonthlyReport = false
    var isLastPageMonthlyReport = false
    var pageSalesByArticle = 1
    var isLoadingSalesByArticle = false
    var isLastPageSalesByArticle = false
    var pageSalesByCustomer = 1
    var isLoadingSalesByCustomer = false
    var isLastPageSalesByCustomer = false
    var pageCustomerActiveRecap = 1
    var isLoadingCustomerActiveRecap = false
    var isLastPageCustomerActiveRecap = false
    var pageCustomerActiveDetail = 1
    var isLoadingCustomerActiveDetail = false
    var isLastPageCustomerActiveDetail = false
    var pageWeeklyWorkPlan = 1
    var isLoadingWeeklyWorkPlan = false
    var isLastPageWeeklyWorkPlan = false
    var pageDailyDetails = 1
    var isLoadingDailyDetails = false
    var isLastPageDailyDetails = false
    var pageMonthlyDetails = 1
    var isLoadingMonthlyDetails = false
    var isLastPageMonthlyDetails = false

    fun setDefaultMonth(){
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        currentMonth.set(calendar.getMonthYear())
        selectedMonth.value = calendar.getMonthYear()
    }

    fun getStatisticArea(){
        val areaIds = arrayListOf<Int?>()
        val brandIds = arrayListOf<Int?>()
        val list = areaFilterList.filter { it.isSelected }
        list.forEach {
            areaIds.add(it.id)
        }

        brandFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }

        repository.getStatisticArea(selectedMonth.value.orEmpty(), brandIds, areaIds, {
            it?.let { response ->
                areaChartReportList = response.areas
                setupStatisticReportList(response.areas[0].id)
            }
        },{
            onErrorGetData.value = it
        })
    }

    private fun setupStatisticReportList(selectedId: Int?) {
        if (areaChartReportList.isNotEmpty()) {
            areaChartList.clear()
            selectedReportAreaId = selectedId
            selectedAreaReport.set(areaChartReportList.find { it.id == selectedId })
            onUpdateAreaChart.invoke()
            setupStatisticData()
            areaChartReportList.forEach { areaReport ->
                areaChartList.add(AreaChartItemViewModel(areaReport.id, areaReport.percentage, areaName = areaReport.name, isSelected = areaReport.id == selectedReportAreaId) { id, name ->

                    setupStatisticReportList(id)
                    setupStatisticData()
                })
            }
        }
    }

    private fun setupStatisticData() {
        reportInfoList.clear()
        reportInfoList.add(
            ReportTypeItemViewModel(
                selectedAreaReport.get()?.branchesCount.toString(),
                R.drawable.ic_branch,
                getAppContext().resources.getString(R.string.branches),
                true
            ) {
                onSelectReport(it.orEmpty())
            })
        reportInfoList.add(
            ReportTypeItemViewModel(
                selectedAreaReport.get()?.customersCount.shortNumberFormat(),
                R.drawable.ic_my_customer_active,
                getAppContext().resources.getString(R.string.customers)
            )
        )
        reportInfoList.add(
            ReportTypeItemViewModel(
                selectedAreaReport.get()?.ordesCount.shortNumberFormat(),
                R.drawable.ic_order_active,
                getAppContext().resources.getString(R.string.orders)
            )
        )
       /* reportInfoList.add(
            ReportTypeItemViewModel(
                selectedAreaReport.get()?.totalSalesCount?.replace(
                    "-",
                    "0"
                )?.toLong().shortNumberFormat(),
                R.drawable.ic_sales,
                getAppContext().resources.getString(R.string.sales),
                true
            ) {
                onSelectReport(it.orEmpty())
            })*/
        reportInfoList.add(
            ReportTypeItemViewModel(
                selectedAreaReport.get()?.visitsCount.shortNumberFormat(),
                R.drawable.ic_task_green,
                getAppContext().resources.getString(R.string.visits)
            )
        )

        getChartReport()
    }

    private fun onSelectReport(type: String) {
        onSelectedReportType.value = type
    }

    private fun getChartReport() {
        val brandIds = arrayListOf<Int?>()
        brandFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }

        repository.getChartReportList(selectedReportAreaId, selectedMonth.value.orEmpty(), brandIds, cbOnSuccess = {
            chartData.clear()
            chartData.addAll(it.reportCharts)
            getStockReport()
            setStatisticReportTitle(0)
        }, cbOnError = {
            onErrorGetData.value = it
        })
    }

    private fun getStockReport() {
        val brandIds = arrayListOf<Int?>()
        brandFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }

        repository.getStockReportList(selectedReportAreaId, selectedMonth.value.orEmpty(), brandIds, cbOnSuccess = {
            stockData.clear()
            stockData.addAll(it.stockList)
            onFinishedGetData.invoke()
            setStockReportTitle(0)
        }, cbOnError = {
            onErrorGetData.value = it
        })
    }

    fun getSalesChart(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        val brandIds = arrayListOf<Int?>()
        brandSalesFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }
        if (selectedReportAreaId != 0) {
            repository.getChartReportList(selectedSalesAreaId, selectedMonth.value.orEmpty(), brandIds, true, {
               if (!it.reportCharts.isNullOrEmpty()) {
                   setupSalesReportTable(it.reportCharts[0])
                   _salesChartReportList.value = it.reportCharts
               }
                cbOnSuccess.invoke()
            }, {
                cbOnError.invoke(it)
            })
        }
    }

    private fun setupSalesReportTable(sales: ChartReport) {
        val list = arrayListOf<SimpleViewModel>()
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.month)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.target)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.prev_year)))
        list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.current_year)))

        val months = DateFormatSymbols().months

        months.forEachIndexed { index, month ->
            list.add(CellItemViewModel(month))
            list.add(CellItemViewModel(sales.target[index].toString().formatThousandSeparator("-")))
            list.add(CellItemViewModel(sales.lastMonth[index].toString().formatThousandSeparator("-")))
            list.add(CellItemViewModel(sales.thisMonth[index].toString().formatThousandSeparator("-")))
        }

        _salesReportViewModelList.value = list
    }

    fun getBranchReport(cbOnError: (String?) -> Unit) {
        isLoadingBranch = true
        val brandIds = arrayListOf<Int?>()
        brandFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }
        repository.getBranchReportList(selectedReportAreaId, selectedMonth.value.orEmpty(), brandIds, pageBranch, {
            if (pageBranch == 1) {
                branchReportList.clear()
            }
            if (it.branches.size < Constants.LIMIT_GET_DATA) {
                isLastPageBranch = true
            }
            branchReportList.addAll(it.branches)
            setupBranchReportList(it.branches)
            pageBranch++
            isLoadingBranch = false
        }, {
            isLoadingBranch = false
            cbOnError.invoke(it)
        })
    }

    private fun setupBranchReportList(branches: ArrayList<BranchReport>){
        val list = arrayListOf<BranchReportItemViewModel>()
        branches.forEach {
            list.add(BranchReportItemViewModel(
                it.id,
                it.name,
                it.customersCount,
                it.ordesCount,
                it.totalSalesCount?.replace("-", "0")?.toLong(),
                it.visitsCount,
                it.asmCount.shortNumberFormat(),
                it.salesCount.shortNumberFormat()
            ) {
                selectedReportBranchId = it
                navController.navigate(R.id.action_branchReportListFragment_to_customerListByBranch)
            })
        }
        val branchesTemp = branchesReportViewModelList.value
        if (!branchesTemp.isNullOrEmpty() && pageBranch > 1) {
            branchesTemp.addAll(list)
            _branchesReportViewModelList.value = branchesTemp
        } else {
            _branchesReportViewModelList.value = list
        }
    }

    fun setStatisticReportTitle(position: Int) {
        if (!chartData.isNullOrEmpty()) {
            selectedReportGraphTitle.set(chartData[position].chartTitle)
        }
    }

    fun setStockReportTitle(position: Int) {
        if (!stockData.isNullOrEmpty()) {
            selectedStockReportGraphTitle.set(stockData[position].status?.plus(" ").plus(getAppContext().getString(R.string.stock)))
        }
    }

    fun getMostVisitedCustomer(cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit) {
        repository.getMostVisitedCustomer(selectedMonth.value.orEmpty(), {
            if (it.customers.isEmpty()) {
                emptyMostVisitedVisibility.set(true)
            } else {
                emptyMostVisitedVisibility.set(false)
            }
            setupMostVisitedList(it.customers)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupMostVisitedList(list: ArrayList<Customer>) {
        mostVisitedCustomers.clear()
        list.forEach {
            mostVisitedCustomers.add(CustomerItemViewModel(it.id, it.count.toString(), "", it.name, it.address, it.state, arrowVisibility = View.GONE) {})
        }
    }

    fun getMostPlannedCustomer(cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit) {
        repository.getMostPlannedCustomer(selectedMonth.value.orEmpty(), {
            if (it.customers.isEmpty()) {
                emptyMostPlannedVisibility.set(true)
            } else {
                emptyMostPlannedVisibility.set(false)
            }
            setupMostPlannedList(it.customers)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupMostPlannedList(list: ArrayList<Customer>) {
        mostPlannedCustomers.clear()
        list.forEach {
            mostPlannedCustomers.add(CustomerItemViewModel(it.id, it.count.toString(), "", it.name, it.address, it.state, arrowVisibility = View.GONE) {})
        }
    }

    fun getByBranchCustomer(cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit) {
        repository.getByBranchCustomer(selectedMonth.value.orEmpty(), {
            if (it.customers.isEmpty()) {
                emptyByBranchVisibility.set(true)
            } else {
                emptyByBranchVisibility.set(false)
            }
            setupByBranchList(it.customers)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupByBranchList(list: ArrayList<Customer>) {
        byBranchCustomers.clear()
        list.forEach {
            byBranchCustomers.add(CustomerItemViewModel(it.id, it.count.toString(), "", it.name, it.address, it.state, arrowVisibility = View.GONE) {})
        }
    }

    fun getByBrandsReport(cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit) {
        repository.getByBrandsReport(selectedMonth.value.orEmpty(), {
            if (it.brands.isEmpty()) {
                emptyByBrandsVisibility.set(true)
            } else {
                emptyByBrandsVisibility.set(false)
            }
            setupByBrandsList(it.brands)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupByBrandsList(list: ArrayList<Brand>) {
        byBrandsCustomers.clear()
        list.forEach {
            byBrandsCustomers.add(BrandReportViewModel(it.id?.toInt(), it.taskCount.toString(), it.name) {})
        }
    }

    fun getBySalesReport(cbOnSuccess:() -> Unit, cbOnError: (String?) -> Unit) {
        repository.getBySalesReport(selectedMonth.value.orEmpty(), {
            if (it.users.isEmpty()) {
                emptyBySalesVisibility.set(true)
            } else {
                emptyBySalesVisibility.set(false)
            }
            setupBySalesList(it.users)
            cbOnSuccess.invoke()
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupBySalesList(list: ArrayList<SalesTeam>) {
        bySalesReports.clear()
        list.forEach {
            bySalesReports.add(SalesViewModel(it.id, it.contactPersonAvatar, it.name, it.taskCount.toString(), it.otherVisitCount.toString(),
                it.failedCount.toString()) {})
        }
    }

    fun getCustomerByBranch( cbOnError: (String?) -> Unit) {
        isLoadingCustomer = true
        val brandIds = arrayListOf<Int?>()
        brandFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }
        repository.getCustomerByBranchReport(selectedReportBranchId, selectedMonth.value.orEmpty(), brandIds, pageCustomer, {
            if (it.customers.size < Constants.LIMIT_GET_DATA) {
                isLastPageCustomer = true
            }
            setupCustomerListByBranch(it.customers)
            pageCustomer++
            isLoadingCustomer = false
        }, {
            isLoadingCustomer = false
            cbOnError.invoke(it)
        })
    }

    private fun setupCustomerListByBranch(customers: ArrayList<Customer>) {
        val list = arrayListOf<CustomerItemViewModel>()
        customers.forEach { customer ->
            list.add(CustomerItemViewModel(customer.id, "", "", customer.name, "", customer.state) {
                val bundle = Bundle().apply {
                    val selectedCustomer = Customer(
                        id = customer.id,
                        state = customer.state,
                        name = customer.name,
                        address = customer.address,
                        contactPersonAvatar = customer.contactPersonAvatar
                    )
                    putString(SELECTED_CUSTOMER, selectedCustomer.json())
                }

                navController.navigate(R.id.action_customerListByBranch_to_customerDetailsFragment, bundle)
            })
        }
        val customersTemp = customerReportViewModelList.value
        if (!customersTemp.isNullOrEmpty() && pageCustomer > 1) {
            customersTemp.addAll(list)
            _customerReportViewModelList.value = customersTemp
        } else {
            _customerReportViewModelList.value = list
        }
        customerReportViewModelList.value?.let {
            if (it.isEmpty()) {
                emptyCustomerByBranchVisibility.set(true)
            } else {
                emptyCustomerByBranchVisibility.set(false)
            }
        }
    }

    fun getBranchSalesReport(cbOnError: (String?) -> Unit) {
        isLoadingSales = true
        val brandIds = arrayListOf<Int?>()
        brandFilterList.filter { it.isSelected }.forEach {
            brandIds.add(it.id)
        }
        repository.getSalesByBranchReport(
            selectedReportAreaId,
            selectedMonth.value.orEmpty(),
            brandIds,
            pageSales,
            {
                if (it.branches.size < Constants.LIMIT_GET_DATA) {
                    isLastPageSales = true
                }
                setupSalesReportList(it.branches)
                pageSales++
                isLoadingSales = false
            },
            {
                isLoadingCustomer = false
                cbOnError.invoke(it)
            })
    }

     private fun setupSalesReportList(branches: ArrayList<BranchSalesReport>){
         val list = arrayListOf<BranchSalesReportItemViewModel>()
         branches.forEach { branch ->
             list.add(BranchSalesReportItemViewModel(branch.name, branch.totalSales))
         }
         val branchesTemp = branchSalesReportViewModelList.value
         if (!branchesTemp.isNullOrEmpty() && pageCustomer > 1) {
             branchesTemp.addAll(list)
             _branchSalesReportViewModelList.value = branchesTemp
         } else {
             _branchSalesReportViewModelList.value = list
         }
     }

    fun getBranchListReportTitle(): String? {
        return getAppContext().resources.getString(R.string.branches_in).plus(" ").plus(selectedAreaReport.get()?.name)
    }

    fun getSalesListReportTitle(): String? {
        return getAppContext().resources.getString(R.string.sales_in).plus(" ").plus(selectedAreaReport.get()?.name)
    }

    fun getAreaFilterList(cbOnSuccess: () -> Unit) {
        repository.getAreas({
            areaFilterList.clear()
            areaDailyFilterList.clear()
            areaMonthlyFilterList.clear()
            areaSalesByArticleFilterList.clear()
            areaSalesByCustomerFilterList.clear()
            it?.areas?.forEach { area ->
                areaFilterList.add(CheckBoxFilterItemViewModel(area.id, area.name))
                areaDailyFilterList.add(CheckBoxFilterItemViewModel(area.id, area.name))
                areaMonthlyFilterList.add(CheckBoxFilterItemViewModel(area.id, area.name))
                areaSalesByArticleFilterList.add(CheckBoxFilterItemViewModel(area.id, area.name))
                areaSalesByCustomerFilterList.add(CheckBoxFilterItemViewModel(area.id, area.name))
            }
            cbOnSuccess.invoke()
        }, {})
    }


    fun getBrandFilterList(cbOnSuccess: () -> Unit) {
        repository.getBrands({
            brandFilterList.clear()
            brandSalesFilterList.clear()
            brandDailyFilterList.clear()
            brandMonthlyFilterList.clear()
            brandSalesByArticleFilterList.clear()
            brandSalesByCustomerFilterList.clear()
            it?.brands?.forEach {
                brandFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                brandSalesFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                brandDailyFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                brandMonthlyFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                brandSalesByArticleFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
                brandSalesByCustomerFilterList.add(CheckBoxFilterItemViewModel(it.id?.toInt(), it.name))
            }
            cbOnSuccess.invoke()
        }, {})
    }

    fun setBrandFilterTitle() {
        brandFilterTitle.set(getFilterTitle(brandFilterList, getAppContext().getString(R.string.all_brand)))
    }

    fun setAreaFilterTitle() {
        areaFilterTitle.set(getFilterTitle(areaFilterList, getAppContext().getString(R.string.all_area)))
    }

    fun setBrandDailyFilterTitle() {
        brandDailyFilterTitle.set(getFilterTitle(brandDailyFilterList, getAppContext().getString(R.string.all_brand)))
    }

    fun setAreaDailyFilterTitle() {
        areaDailyFilterTitle.set(getFilterTitle(areaDailyFilterList, getAppContext().getString(R.string.all_area)))
    }

    fun setBrandMonthlyFilterTitle() {
        brandMonthlyFilterTitle.set(getFilterTitle(brandMonthlyFilterList, getAppContext().getString(R.string.all_brand)))
    }

    fun setAreaMonthlyFilterTitle() {
        areaMonthlyFilterTitle.set(getFilterTitle(areaMonthlyFilterList, getAppContext().getString(R.string.all_area)))
    }

    fun setBrandSalesFilterTitle() {
        brandSalesFilterTitle.set(getFilterTitle(brandSalesFilterList, getAppContext().getString(R.string.all_brand)))
    }

    fun setBrandSalesByArticleFilterTitle() {
        brandSalesReportByArticleFilterTitle.set(getFilterTitle(brandSalesByArticleFilterList, getAppContext().getString(R.string.all_brand)))
    }

    fun setAreaSalesByArticleFilterTitle() {
        areaSalesReportByArticleFilterTitle.set(getFilterTitle(areaSalesByArticleFilterList, getAppContext().getString(R.string.all_area)))
    }

    fun setBrandSalesByCustomerFilterTitle() {
        brandSalesReportByCustomerFilterTitle.set(getFilterTitle(brandSalesByCustomerFilterList, getAppContext().getString(R.string.all_brand)))
    }

    fun setAreaSalesByCustomerFilterTitle() {
        areaSalesReportByCustomerFilterTitle.set(getFilterTitle(areaSalesByCustomerFilterList, getAppContext().getString(R.string.all_area)))
    }

    private fun getFilterTitle(filterList: ArrayList<CheckBoxFilterItemViewModel>, defaultTitle: String): String {
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
                filterList.size -> filterTitleTemp = defaultTitle
            }
        }
        return filterTitleTemp
    }

    fun getDailyReport(cbOnError:(String?) -> Unit) {
        isLoadingDailyReport = true
        var areaIds: ArrayList<Int?>? = arrayListOf()
        var brandIds: ArrayList<Int?>? = arrayListOf()
        areaDailyFilterList.forEach {
            if (it.isSelected) {
                areaIds?.add(it.id)
            }
        }
        brandDailyFilterList.forEach {
            if (it.isSelected) {
                brandIds?.add(it.id)
            }
        }
        emptyDailyReportVisibility.set(false)

        if (areaIds?.size == areaDailyFilterList.size) {
            areaIds = null
        }

        if (brandIds?.size == brandDailyFilterList.size) {
            brandIds = null
        }
        repository.getMyTeamDailyReport(areaIds, brandIds, selectedDateDailyReport.get(), pageDailyReport, LIMIT_GET_DATA, {
            if (it.users.size < Constants.LIMIT_GET_DATA) {
                isLastPageDailyReport = true
            }
            if (it.users.isNullOrEmpty()) {
                emptyDailyReportVisibility.set(true)
            } else {
                emptyDailyReportVisibility.set(false)
                setupDailyReportMyTeamList(it.users)
            }
            pageDailyReport++
            isLoadingDailyReport = false
        }, {
            isLoadingDailyReport = false
            cbOnError.invoke(it)
        })
    }

    private fun setupDailyReportMyTeamList(myTeams: ArrayList<SalesTeam>){
        val list = arrayListOf<DailyReportMyTeamItemViewModel>()
        myTeams.forEach { it ->
            list.add(DailyReportMyTeamItemViewModel(it.id, it.contactPersonAvatar, it.name, it.brandNames,
                it.areaNames, it.visitCount.toString(), it.otherVisitCount.toString(), it.failedCount.toString()){
                onOpenDailyReportDetails.value = it
            })
        }
        val teamsTemp = dailyReportMyTeamViewModelList.value
        if (!teamsTemp.isNullOrEmpty() && pageDailyReport > 1) {
            teamsTemp.addAll(list)
            _dailyReportMyTeamViewModelList.value = teamsTemp
        } else {
            _dailyReportMyTeamViewModelList.value = list
        }
    }

    fun getDailyReportDetails(salesId: Int, date: String?, cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        isLoadingDailyDetails = true
        repository.getVisitDetailsReport(salesId, DAILY, date = date, page = pageDailyDetails ,cbOnSuccess = {
            selectedSalesDailyReport.set(it.sales)
            if (it.sales.visits.size < Constants.LIMIT_GET_DATA) {
                isLastPageDailyDetails = true
                _visitDailyReportViewModelList.value = arrayListOf()
            }
            if (it.sales.visits.isNullOrEmpty() && pageDailyDetails == 1) {
                emptyVisitReportVisibility.set(true)
            } else {
                emptyVisitReportVisibility.set(false)
                setupVisitDailyReportDetailList(it.sales.visits)
            }
            pageDailyDetails++
            isLoadingDailyDetails = false
        }, cbOnError = {
            cbOnError.invoke(it)
        })

    }

    private fun setupVisitDailyReportDetailList(dailyVisits: ArrayList<DailyVisit>) {
        val list = arrayListOf<DailyVisitReportItemViewModel>()
        dailyVisits.forEachIndexed { index, dailyVisit ->
            val lineVisibility = if (index < dailyVisits.size - 1) {
                View.VISIBLE
            } else {
                View.GONE
            }
            list.add(DailyVisitReportItemViewModel(customerName = dailyVisit.customerName, dateTime = dailyVisit.checkInTime.getVisitSpentTime(dailyVisit.checkOut),
                spent = DateUtils.formateDate(dailyVisit.spent, HH_MM_SS, HHh_MMm_SSs).replace("0 h ","").replace(" 00 s",""),
                isFreeVisit = isFreeVisit(dailyVisit.visitType), notes = dailyVisit.notes.quotePrefix(), lineVisibility = lineVisibility))
        }
        _visitDailyReportViewModelList.value = list
    }

    fun isFreeVisit(reportType: String) : Boolean {
        return when (reportType) {
            FREE_VISIT -> true
            else -> false
        }
    }

    fun getMonthlyReport(cbOnError:(String?) -> Unit) {
        isLoadingMonthlyReport = true
        var areaIds: ArrayList<Int?>? = arrayListOf()
        var brandIds: ArrayList<Int?>? = arrayListOf()
        areaMonthlyFilterList.forEach {
            if (it.isSelected) {
                areaIds?.add(it.id)
            }
        }
        brandMonthlyFilterList.forEach {
            if (it.isSelected) {
                brandIds?.add(it.id)
            }
        }
        emptyMonthlyReportVisibility.set(false)

        if (areaIds?.size == areaMonthlyFilterList.size) {
            areaIds = null
        }

        if (brandIds?.size == brandMonthlyFilterList.size) {
            brandIds = null
        }
        repository.getMyTeamMonthlyReport(areaIds, brandIds, selectedMonthMonthlyReport.get(), pageMonthlyReport, LIMIT_GET_DATA, {
            if (it.users.size < Constants.LIMIT_GET_DATA) {
                isLastPageMonthlyReport = true
            }
            if (it.users.isNullOrEmpty()) {
                emptyMonthlyReportVisibility.set(true)
            } else {
                emptyMonthlyReportVisibility.set(false)
                setupMonthlyReportMyTeamList(it.users)
            }
            pageMonthlyReport++
            isLoadingMonthlyReport = false
        }, {
            isLoadingMonthlyReport = false
            cbOnError.invoke(it)
        })
    }

    private fun setupMonthlyReportMyTeamList(myTeams: ArrayList<SalesTeam>){
        val list = arrayListOf<MonthlyReportMyTeamItemViewModel>()
        myTeams.forEach { it ->
            val leftTitleList = arrayListOf<String>()
            val salesCountList = arrayListOf<String>()

            it.salesRevenuePerWeek.forEach {
                leftTitleList.add(it.brand.orEmpty())
                salesCountList.add(it.week1)
                salesCountList.add(it.week2)
                salesCountList.add(it.week3)
                salesCountList.add(it.week4)
                salesCountList.add(it.week5)
            }


            list.add(MonthlyReportMyTeamItemViewModel(it.id, it.contactPersonAvatar, it.name, it.brandNames,
                it.areaNames, it.visitCount.toString(), it.otherVisitCount.toString(), it.failedCount.toString(), it.totalSalesRevenue.shortNumberFormat(),
                leftTitleList, salesCountList){
                onOpenMonthlyReportDetails.value = it
            })
        }
        val teamsTemp = monthlyReportMyTeamViewModelList.value
        if (!teamsTemp.isNullOrEmpty() && pageMonthlyReport > 1) {
            teamsTemp.addAll(list)
            _monthlyReportMyTeamViewModelList.value = teamsTemp
        } else {
            _monthlyReportMyTeamViewModelList.value = list
        }
    }

    fun getMonthlyVisitDetails(salesId: Int, month: String?, cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        isLoadingMonthlyDetails = true
        repository.getVisitDetailsReport(salesId, MONTHLY, month = month, page = pageMonthlyDetails, cbOnSuccess = {
            selectedSalesMonthlyReport.set(it.sales)
            if (it.sales.customers.size < Constants.LIMIT_GET_DATA) {
                isLastPageMonthlyDetails = true
            }
            if (it.sales.customers.isNullOrEmpty() && pageMonthlyDetails == 1) {
                emptyMonthlyVisitReportVisibility.set(true)
            } else {
                emptyMonthlyVisitReportVisibility.set(false)
                setupMonthlyReportDetailList(it.sales.customers)
            }
            pageMonthlyDetails++
            isLoadingMonthlyDetails = false
        }, cbOnError = {
            cbOnError.invoke(it)
        })

    }

    private fun setupMonthlyReportDetailList(myTeams: ArrayList<CustomerDetailsReport>) {
        val list = arrayListOf<MonthlyVisitReportItemViewModel>()
        val teamsTemp = visitMonthlyReportViewModelList.value
        myTeams.forEach { it ->
            val visitCountList = arrayListOf<String>()
            val leftSalesList = arrayListOf<String>()
            val salesCountList = arrayListOf<String>()
            val salesBrandList = arrayListOf<String>()
            val salesCountBrandList = arrayListOf<String>()

            visitCountList.add(it.w1VisitCount.shortNumberFormat())
            visitCountList.add(it.w2VisitCount.shortNumberFormat())
            visitCountList.add(it.w3VisitCount.shortNumberFormat())
            visitCountList.add(it.w4VisitCount.shortNumberFormat())
            visitCountList.add(it.w5VisitCount.shortNumberFormat())

            visitCountList.add(it.w1OtherVisitCount.shortNumberFormat())
            visitCountList.add(it.w2OtherVisitCount.shortNumberFormat())
            visitCountList.add(it.w3OtherVisitCount.shortNumberFormat())
            visitCountList.add(it.w4OtherVisitCount.shortNumberFormat())
            visitCountList.add(it.w5OtherVisitCount.shortNumberFormat())

            visitCountList.add(it.w1FailedCount.shortNumberFormat())
            visitCountList.add(it.w2FailedCount.shortNumberFormat())
            visitCountList.add(it.w3FailedCount.shortNumberFormat())
            visitCountList.add(it.w4FailedCount.shortNumberFormat())
            visitCountList.add(it.w5FailedCount.shortNumberFormat())

            it.salesRevenuePerWeek.forEach {
                leftSalesList.add(it.brand)
                salesCountList.add(it.week1.shortNumberFormat())
                salesCountList.add(it.week2.shortNumberFormat())
                salesCountList.add(it.week3.shortNumberFormat())
                salesCountList.add(it.week4.shortNumberFormat())
                salesCountList.add(it.week5.shortNumberFormat())
            }

            it.salesRevenuePerBrand.forEach {
                salesBrandList.add(it.brand)
                salesCountBrandList.add(it.value)
            }

            list.add(MonthlyVisitReportItemViewModel(
                it.name,
                it.totalVisit.toString(),
                it.totalOtherVisit.toString(),
                it.totalFailed.toString(),
                visitCountList,
                leftSalesList,
                salesCountList,
                salesBrandList,
                salesCountBrandList
            ) {
                onOpenMonthlyReportDetails.value = it
            })
        }
        _visitMonthlyReportViewModelList.value = list
        if (!teamsTemp.isNullOrEmpty() && pageMonthlyDetails > 1) {
            teamsTemp.addAll(list)
            _visitMonthlyReportViewModelList.value = teamsTemp
        } else {
            _visitMonthlyReportViewModelList.value = list
        }
    }

    fun getReportSalesByArticle(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        if(!isLoadingSalesByArticle) {
            isLoadingSalesByArticle = true

            var areaIds: ArrayList<Int?>? = arrayListOf()
            var brandIds: ArrayList<Int?>? = arrayListOf()
            areaSalesByArticleFilterList.forEach {
                if (it.isSelected) {
                    areaIds?.add(it.id)
                }
            }
            brandSalesByArticleFilterList.forEach {
                if (it.isSelected) {
                    brandIds?.add(it.id)
                }
            }

            if (areaIds?.size == areaSalesByArticleFilterList.size) {
                areaIds = null
            }

            if (brandIds?.size == brandSalesByArticleFilterList.size) {
                brandIds = null
            }

            repository.getReportSalesByArticle(
                areaIds,
                brandIds,
                currentMonth.get(),
                searchKeywordReportArticle.get(),
                selectedSortBySalesReportArticle.get()?.type,
                selectedSortDirectionSalesReportArticle.get()?.type,
                pageSalesByArticle, {
                    if (it.sales.size < Constants.LIMIT_GET_DATA) {
                        isLastPageSalesByArticle = true
                    }
                    if (it.sales.isNullOrEmpty()) {
                        emptySalesReportByArticleVisibility.set(true)
                    } else {
                        emptySalesReportByArticleVisibility.set(false)
                        setupSalesByArticleReportData(it.sales)
                    }
                    pageSalesByArticle++
                    isLoadingSalesByArticle = false
                    cbOnSuccess.invoke()
                },
                {
                    cbOnError.invoke(it)
                })
        }
    }

    private fun setupSalesByArticleReportData(sales: ArrayList<SalesByArticleReport>) {
        val list = arrayListOf<SimpleViewModel>()
        val tempList = salesReportByArticleViewModelList.value
        if (pageSalesByArticle == 1) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.article_desc)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.size)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.quantity)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.value)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.mom)))
        }
        sales.forEach {
            list.add(CellItemViewModel(it.articleDesc.filterEmpty("-")))
            list.add(CellItemViewModel(it.size.shortNumberFormat()))
            list.add(CellItemViewModel(it.quantity.shortNumberFormat()))
            list.add(CellItemViewModel(it.value.shortNumberFormat()))
            list.add(CellItemViewModel(it.mom.shortNumberFormat()))
        }
        if (!tempList.isNullOrEmpty() && pageSalesByArticle > 1) {
            tempList.addAll(list)
            _salesReportByArticleViewModelList.value = tempList
        } else {
            _salesReportByArticleViewModelList.value = list
        }
    }

    fun getReportSalesByCustomer(cbOnError: (String?) -> Unit) {
        if (!isLoadingSalesByCustomer) {
            isLoadingSalesByCustomer = true

            var areaIds: ArrayList<Int?>? = arrayListOf()
            var brandIds: ArrayList<Int?>? = arrayListOf()
            areaSalesByCustomerFilterList.forEach {
                if (it.isSelected) {
                    areaIds?.add(it.id)
                }
            }
            brandSalesByCustomerFilterList.forEach {
                if (it.isSelected) {
                    brandIds?.add(it.id)
                }
            }

            if (areaIds?.size == areaSalesByCustomerFilterList.size) {
                areaIds = null
            }

            if (brandIds?.size == areaSalesByCustomerFilterList.size) {
                brandIds = null
            }

            repository.getReportSalesByCustomer(
                areaIds,
                brandIds,
                currentMonth.get(),
                searchKeywordReportCustomer.get(),
                selectedSortBySalesReportCustomer.get()?.type,
                selectedSortDirectionSalesReportCustomer.get()?.type,
                pageSalesByCustomer, {
                if (it.sales.size < Constants.LIMIT_GET_DATA) {
                    isLastPageSalesByCustomer = true
                }
                if (it.sales.isNullOrEmpty()) {
                    emptySalesReportByCustomerVisibility.set(true)
                } else {
                    emptySalesReportByCustomerVisibility.set(false)
                    setupSalesByCustomerReportData(it.sales)
                }
                pageSalesByCustomer++
                isLoadingSalesByCustomer = false
            }, {
                cbOnError.invoke(it)
            })
        }
    }

    private fun setupSalesByCustomerReportData(sales: ArrayList<SalesByCustomerReport>) {
        val list = arrayListOf<SimpleViewModel>()
        val tempList = salesReportByCustomerViewModelList.value
        if (pageSalesByCustomer == 1) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer_desc)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.product)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.km)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.dv)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.hb)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.sa)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.sb)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.kb)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.total)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.percentage)))
        }
        sales.forEach {
            list.add(CellItemViewModel(it.customer.filterEmpty("-")))
            list.add(CellItemViewModel(it.customerDesc.filterEmpty("-")))
            list.add(CellItemViewModel(it.product.filterEmpty("-")))
            list.add(CellItemViewModel(it.km.filterEmpty("-")))
            list.add(CellItemViewModel(it.dv.filterEmpty("-")))
            list.add(CellItemViewModel(it.hb.filterEmpty("-")))
            list.add(CellItemViewModel(it.sa.filterEmpty("-")))
            list.add(CellItemViewModel(it.sb.filterEmpty("-")))
            list.add(CellItemViewModel(it.kb.filterEmpty("-")))
            list.add(CellItemViewModel(it.total.shortNumberFormat()))
            list.add(CellItemViewModel(it.percentage.filterEmpty("-")))
        }
        if (!tempList.isNullOrEmpty() && pageSalesByCustomer > 1) {
            tempList.addAll(list)
            _salesReportByCustomerViewModelList.value = tempList
        } else {
            _salesReportByCustomerViewModelList.value = list
        }
    }

    fun getCustomerActiveRecap(cbOnError: (String?) -> Unit) {
        isLoadingCustomerActiveRecap = true
        repository.getCustomerActiveRecap(currentMonth.get(), pageCustomerActiveRecap, {
            if (it.customers.size < Constants.LIMIT_GET_DATA) {
                isLastPageCustomerActiveRecap = true
            }
            if (it.customers.isNullOrEmpty()) {
                emptyCustomerActiveRecapVisibility.set(true)
            } else {
                emptyCustomerActiveRecapVisibility.set(false)
                setupCustomerActiveRecapData(it.customers)
            }
            pageCustomerActiveRecap++
            isLoadingCustomerActiveRecap = false
        }, {
            cbOnError.invoke(it)
        })
    }

    private fun setupCustomerActiveRecapData(customers: ArrayList<CustomerActiveRecap>) {
        val list = arrayListOf<SimpleViewModel>()
        val tempList = customerActiveRecapViewModelList.value
        if (pageCustomerActiveRecap == 1) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.area_id)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.brand)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.total)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.new_customer)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.active_customer)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.inactive_customer)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.active_prev_month)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.inactive_prev_month)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.growth_customer)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.growth_active)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.growth_inactive)))
        }
        customers.forEach {
            list.add(CellItemViewModel(it.areaId.filterEmpty("-")))
            list.add(CellItemViewModel(it.brand.filterEmpty("-")))
            list.add(CellItemViewModel(it.total.filterEmpty("-")))
            list.add(CellItemViewModel(it.newCustomer.filterEmpty("-")))
            list.add(CellItemViewModel(it.activeCustomer.filterEmpty("-")))
            list.add(CellItemViewModel(it.inactiveCustomer.filterEmpty("-")))
            list.add(CellItemViewModel(it.activePreviousMonth.filterEmpty("-")))
            list.add(CellItemViewModel(it.inactivePreviousMonth.filterEmpty("-")))
            list.add(CellItemViewModel(it.growthCustomer.filterEmpty("-")))
            list.add(CellItemViewModel(it.growthActive.filterEmpty("-")))
            list.add(CellItemViewModel(it.growthInactive.filterEmpty("-")))
        }
        if (!tempList.isNullOrEmpty() && pageCustomerActiveRecap > 1) {
            tempList.addAll(list)
            _customerActiveRecapViewModelList.value = tempList
        } else {
            _customerActiveRecapViewModelList.value = list
        }
    }

    fun getCustomerActiveDetail(cbOnError: (String?) -> Unit) {
        if (!isLoadingCustomerActiveDetail) {
            isLoadingCustomerActiveDetail = true
            repository.getCustomerActiveDetail(currentMonth.get(), pageCustomerActiveDetail, {
                if (it.customers.size < Constants.LIMIT_GET_DATA) {
                    isLastPageCustomerActiveDetail = true
                }
                if (it.customers.isNullOrEmpty()) {
                    emptyCustomerActiveDetailVisibility.set(true)
                } else {
                    emptyCustomerActiveDetailVisibility.set(false)
                    setupCustomerActiveDetailData(it.customers)
                }
                pageCustomerActiveDetail++
                isLoadingCustomerActiveDetail = false
            }, {
                cbOnError.invoke(it)
            })
        }
    }

    private fun setupCustomerActiveDetailData(customers: ArrayList<CustomerActiveDetail>) {
        val list = arrayListOf<SimpleViewModel>()
        val tempList = customerActiveDetailViewModelList.value
        if (pageCustomerActiveDetail == 1) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.last_invoice)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.city)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.brand)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer_state)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.salesman)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer_visit)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.customer_brand_visit)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.is_visit)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.visit_date)))
        }
        customers.forEach {
            list.add(CellItemViewModel(it.lastInvoice.filterEmpty("-")))
            list.add(CellItemViewModel(it.customer.filterEmpty("-")))
            list.add(CellItemViewModel(it.city.filterEmpty("-")))
            list.add(CellItemViewModel(it.brand.filterEmpty("-")))
            list.add(CellItemViewModel(it.customerStatus.filterEmpty("-")))
            list.add(CellItemViewModel(it.salesman.filterEmpty("-")))
            list.add(CellItemViewModel(it.customerVisit.filterEmpty("-")))
            list.add(CellItemViewModel(it.customerBrandVisit.filterEmpty("-")))
            list.add(CellItemViewModel(it.isVisit.filterEmpty("-")))
            list.add(CellItemViewModel(it.visitDate.filterEmpty("-")))
        }
        if (!tempList.isNullOrEmpty() && pageCustomerActiveDetail > 1) {
            tempList.addAll(list)
            _customerActiveDetailViewModelList.value = tempList
        } else {
            _customerActiveDetailViewModelList.value = list
        }
    }

    fun getWeeklyWorkPlan(cbOnError: (String?) -> Unit) {
        if (!isLoadingWeeklyWorkPlan) {
            isLoadingWeeklyWorkPlan = true
            repository.getWeeklyWorkPlan(pageWeeklyWorkPlan, {
                if (it.visitPlans.size < Constants.LIMIT_GET_DATA) {
                    isLastPageWeeklyWorkPlan = true
                }
                if (it.visitPlans.isNullOrEmpty() && pageCustomerActiveDetail == 1) {
                    emptyWeeklyWorkPlanVisibility.set(true)
                } else {
                    emptyWeeklyWorkPlanVisibility.set(false)
                    setupWeeklyWorkPlanData(it.visitPlans)
                }
                pageWeeklyWorkPlan++
                isLoadingWeeklyWorkPlan = false
            }, {
                cbOnError.invoke(it)
            })
        }
    }

    private fun setupWeeklyWorkPlanData(visitPlans: ArrayList<WorkPlan>) {
        val list = arrayListOf<SimpleViewModel>()
        val tempList = weeklyWorkPlanViewModelList.value
        if (pageWeeklyWorkPlan == 1) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.id)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.branch)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.address_number)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.sales_name)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.brand)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.cabang)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.sales_target)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.sales_total)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.total_target)))
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.sisa)))
        }
        visitPlans.forEach {
            list.add(CellItemViewModel(it.id.filterEmpty("-")))
            list.add(CellItemViewModel(it.branch.filterEmpty("-")))
            list.add(CellItemViewModel(it.addressNumber.filterEmpty("-")))
            list.add(CellItemViewModel(it.salesman.filterEmpty("-")))
            list.add(CellItemViewModel(it.brand.filterEmpty("-")))
            list.add(CellItemViewModel(it.cabang.filterEmpty("-")))
            list.add(CellItemViewModel(it.targetPenjualan.filterEmpty("-")))
            list.add(CellItemViewModel(it.jumlahPenjualan.filterEmpty("-")))
            list.add(CellItemViewModel(it.totalTarget.filterEmpty("-")))
            list.add(CellItemViewModel(it.sisa.filterEmpty("-")))
        }
        if (!tempList.isNullOrEmpty() && pageWeeklyWorkPlan > 1) {
            tempList.addAll(list)
            _weeklyWorkPlanViewModelList.value = tempList
        } else {
            _weeklyWorkPlanViewModelList.value = list
        }
    }

    fun getCustomerLocation(cbOnSuccess:(CustomersResponse) -> Unit, cbOnError: (String?) -> Unit) {
        repository.getCustomerLocation({
            cbOnSuccess.invoke(it)
        }, {
            cbOnError.invoke(it)
        })
    }

}
