package com.worka.eroyal.feature.report.saleschart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.RadioButtonFilterDialog
import com.worka.eroyal.data.base.SingleFilterData
import com.worka.eroyal.databinding.FragmentSalesTabBinding
import com.worka.eroyal.feature.common.FragmentPagerAdapter
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/06/20.
 */
class SalesTabFragment: BaseFragment() {

    private lateinit var binding: FragmentSalesTabBinding

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var adapterFragment: FragmentPagerAdapter

    private var filterAreaDialog: RadioButtonFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_tab, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        setupPager()

        with(binding) {
            btnBrandFilter.setOnClickListener {
                brandFilterDialog?.let{
                    it.show()
                } ?: run {
                    context?.let { ctx -> CustomInfoDialog(ctx, ctx.getString(R.string.filter_not_ready_yet)) }
                }
            }

            btnAreaFilter.setOnClickListener {
                filterAreaDialog?.let { it.show() } ?: run {
                    setupAreaDialog()
                    setupBrandFilterDialog()
                }
            }
        }

        viewModel.selectedMonth.observe(this, Observer {
            getData()
        })
    }

    private fun setupPager() {
        val pagers = arrayListOf<Fragment>()
        pagers.add(SalesTableFragment())
        pagers.add(SalesGraphFragment())

        val titles = listOf(
            context?.getString(R.string.table).orEmpty(),
            context?.getString(R.string.graph).orEmpty())

        adapterFragment = FragmentPagerAdapter(childFragmentManager, pagers, titles)
        binding.vpSalesChart.adapter = adapterFragment
        binding.vpSalesChart.offscreenPageLimit = 1
        binding.vpSalesChart.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabLayoutSalesChart
            )
        )
        binding.tabLayoutSalesChart.setupWithViewPager(binding.vpSalesChart)
        binding.tabLayoutSalesChart.tabRippleColor = null
        binding.tabLayoutSalesChart.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view?.findViewById<TextView>(R.id.tab_title)
                context?.let { ctx ->
                    textView?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGreen))
                }
                binding.vpSalesChart.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view?.findViewById<TextView>(R.id.tab_title)
                context?.let { ctx ->
                    textView?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrey))
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view?.findViewById<TextView>(R.id.tab_title)
                context?.let { ctx ->
                    textView?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGreen))
                }
                binding.vpSalesChart.currentItem = tab.position
            }
        })

        titles.forEachIndexed { index: Int, text: String ->
            binding.tabLayoutSalesChart.getTabAt(index)?.setCustomView(R.layout.item_tab_text)
            val title = binding.tabLayoutSalesChart.getTabAt(index)?.customView?.findViewById<TextView>(R.id.tab_title)
            title?.text = text
        }

        binding.vpSalesChart.currentItem = 0
        binding.tabLayoutSalesChart.getTabAt(0)?.customView?.isSelected = true
        val tab = binding.tabLayoutSalesChart.getTabAt(0)
        tab?.select()
    }

    private fun setupAreaDialog() {
        val list = arrayListOf<SingleFilterData>()

        viewModel.areaFilterList.forEach {
            list.add(SingleFilterData(it.id, it.name))
        }
        filterAreaDialog = context?.let {
            RadioButtonFilterDialog(it, it.getString(R.string.select_which_area_to_show), list) { data ->
                with(viewModel) {
                    areaSalesFilterTitle.set(list.find { it.id == data }?.value)
                    defaultStateSalesPageVisibility.set(false)
                    selectedSalesAreaId = data
                    getData()
                }
            }
        }

        filterAreaDialog?.show()
    }

    private fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_brand_to_show), viewModel.brandSalesFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    brandSalesFilterList = list
                    setBrandSalesFilterTitle()
                }
                getData()
            }
        }
    }


    private fun getData() {
        mActivity.showLoading()
        viewModel.getSalesChart({
            mActivity.hideLoading()
        }) {
            mActivity.hideLoading()
        }
    }
}
