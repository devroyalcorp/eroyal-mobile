package com.worka.eroyal.feature.report.byarea

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants.ROLE
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.data.report.Role
import com.worka.eroyal.databinding.FragmentByAreaReportBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.common.StatisticReportPagerAdapter
import com.worka.eroyal.feature.common.StockReportPagerAdapter
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-28.
 */
class ByAreaReportFragment : BaseFragment() {
    private val viewModel: ReportViewModel by sharedViewModel()
    private lateinit var binding: FragmentByAreaReportBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null
    private var reportInfoadapter: GenericAppAdapter<SimpleViewModel>? = null
    private var pagerAdapter: StatisticReportPagerAdapter? = null
    private var stockPagerAdapter: StockReportPagerAdapter? = null

    private var areaFilterDialog: CheckBoxFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_by_area_report, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(viewModel) {
            onSelectedReportType.observe(this@ByAreaReportFragment, Observer { type ->
                when (type) {
                    context?.getString(R.string.branches) -> viewModel.navController.navigate(R.id.BranchReportListFragment)
                    context?.getString(R.string.sales) -> viewModel.navController.navigate(R.id.salesReportListFragment)
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        viewModel.titlePage.value = viewModel.sessionStorage.get(ROLE, Role::class.java).name.plus(" ").plus(context?.getString(R.string.reports))

        Handler().postDelayed({
            setupList()
            setupPager()
            setupStockPager()
        },500)

        with(viewModel) {

            onFinishedGetData.observe(this@ByAreaReportFragment, Observer {
                    pagerAdapter?.data = viewModel.chartData
                    stockPagerAdapter?.data = viewModel.stockData
                    adapter?.notifyDataSetChanged()
                    reportInfoadapter?.notifyDataSetChanged()
                    pagerAdapter?.notifyDataSetChanged()
                    stockPagerAdapter?.notifyDataSetChanged()
                    binding.vpReport.adapter = pagerAdapter
                    binding.vpStock.adapter = stockPagerAdapter
                    isLoadingShow.set(false)
                    binding.shimmerReportByArea.stopShimmer()
                    isGetStatisticEnabled = false
            })
            onErrorGetData.observe(this@ByAreaReportFragment, Observer {
                isLoadingShow.set(false)
                    context?.let { ctx ->
                        CustomInfoDialog(ctx, it)
                    }
            })

            onUpdateAreaChart.observe(this@ByAreaReportFragment, Observer {
                adapter?.notifyDataSetChanged()
            })
        }

        binding.btnAreaFilter.setOnClickListener {
            areaFilterDialog?.adapter?.list = viewModel.areaFilterList
            areaFilterDialog?.show()
        }

        binding.btnBrandFilter.setOnClickListener {
            brandFilterDialog?.adapter?.list = viewModel.brandFilterList
            brandFilterDialog?.show()
        }

        viewModel.selectedMonth.observe(viewLifecycleOwner, Observer {
            if (areaFilterDialog == null || brandFilterDialog == null) {
                getFilterData()
            } else {
                getStatisticData()
            }
        })

    }

    fun getFilterData() {
        viewModel.getAreaFilterList {
            setupAreaFilterDialog()
            if (viewModel.isGetStatisticEnabled) {
                getStatisticData()
            }
        }

        viewModel.getBrandFilterList {
            setupBrandFilterDialog()
        }
    }

    fun setupAreaFilterDialog() {
        context?.let { ctx ->
            areaFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.available_regions), viewModel.areaFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                viewModel.areaFilterList = list
                viewModel.setAreaFilterTitle()
                getStatisticData()
            }
        }
    }

    fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_brand_to_show), viewModel.brandFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                viewModel.brandFilterList = list
                viewModel.setBrandFilterTitle()
                getStatisticData()
            }
        }
    }

    fun getStatisticData() {
            binding.shimmerReportByArea.startShimmer()
            viewModel.isLoadingShow.set(true)
            viewModel.getStatisticArea()
    }

    fun setupList() {
        adapter = GenericAppAdapter(viewModel.areaChartList)
        binding.rvAreaChart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAreaChart.adapter = adapter

        reportInfoadapter = GenericAppAdapter(viewModel.reportInfoList)
        binding.rvReportInfo.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvReportInfo.adapter = reportInfoadapter
    }

    fun setupPager() {
        pagerAdapter = StatisticReportPagerAdapter(viewModel.chartData)
        binding.vpReport.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.vpReport)
        binding.vpReport.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                viewModel.setStatisticReportTitle(position)
            }

        })
    }

    fun setupStockPager() {
        stockPagerAdapter = StockReportPagerAdapter(viewModel.stockData)
        binding.vpStock.adapter = stockPagerAdapter
        binding.tabLayoutStock.setupWithViewPager(binding.vpStock)
        binding.vpStock.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                viewModel.setStockReportTitle(position)
            }

        })
    }
}
