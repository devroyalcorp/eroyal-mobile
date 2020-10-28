package com.worka.eroyal.feature.report.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentTableReportPagerBinding
import com.worka.eroyal.feature.common.FragmentPagerAdapter
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/04/20.
 */
class TableReportPagerFragment: BaseFragment() {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentTableReportPagerBinding
    private lateinit var adapterFragment: FragmentPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_table_report_pager, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        setupPager()
    }

    private fun setupPager() {
        val pagers = arrayListOf<Fragment>()
        pagers.add(SalesReportByArticleFragment())
        pagers.add(SalesReportByCustomerFragment())
        pagers.add(CustomerActiveRecapFragment())
        pagers.add(CustomerActiveDetailFragment())
        pagers.add(WeeklyWorkPlanFragment())
        pagers.add(StoreLocationFragment())

        val titles = listOf(
            context?.getString(R.string.sales_bo_article).orEmpty(),
            context?.getString(R.string.sales_bo_customer).orEmpty(),
            context?.getString(R.string.customer_active_recap).orEmpty(),
            context?.getString(R.string.customer_active_detail).orEmpty(),
            context?.getString(R.string.weekly_work_plan).orEmpty(),
            context?.getString(R.string.store_location).orEmpty())

        adapterFragment = FragmentPagerAdapter(childFragmentManager, pagers, titles)
        binding.vpTableReport.adapter = adapterFragment
        binding.vpTableReport.offscreenPageLimit = 1
        binding.tabLayoutTableReport.setupWithViewPager(binding.vpTableReport)

        binding.vpTableReport.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                checkSelectedPage()
            }
        })

        titles.forEachIndexed { index: Int, text: String ->
            binding.tabLayoutTableReport.getTabAt(index)?.setCustomView(R.layout.item_tab_table_report)
            val title = binding.tabLayoutTableReport.getTabAt(index)?.customView?.findViewById<TextView>(R.id.tab_title)
            title?.text = text
        }

        viewModel.onSalesReportTabCheckPage.observe(this, Observer {
            checkSelectedPage()
        })

    }

    private fun checkSelectedPage() {
        if (binding.vpTableReport.currentItem == 4) {
            viewModel.monthPickerVisibility.set(View.INVISIBLE)
        } else {
            viewModel.monthPickerVisibility.set(View.VISIBLE)
        }
    }
}
