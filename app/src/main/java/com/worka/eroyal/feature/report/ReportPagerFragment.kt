package com.worka.eroyal.feature.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.orhanobut.hawk.Hawk
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.monthpicker.MonthPickerDialog
import com.worka.eroyal.databinding.FragmentReportPagerBinding
import com.worka.eroyal.feature.common.FragmentPagerAdapter
import com.worka.eroyal.feature.report.byarea.ByAreaReportFragment
import com.worka.eroyal.feature.report.dailyvisitreport.DailyVisitReportFragment
import com.worka.eroyal.feature.report.monthlyvisitreport.MonthlyVisitReportFragment
import com.worka.eroyal.feature.report.saleschart.SalesTabFragment
import com.worka.eroyal.feature.report.table.TableReportPagerFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-16.
 */
class ReportPagerFragment : BaseFragment() {

    private val viewModel: ReportViewModel by sharedViewModel()
    lateinit var binding: FragmentReportPagerBinding
    lateinit var adapterFragment: FragmentPagerAdapter

    private var monthPickerDialogDialog: MonthPickerDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_pager, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupPager()
        binding.viewModel = viewModel
        binding.tvMonth.setOnClickListener {
            if (monthPickerDialogDialog == null) {
               setupDatePicker()
            }
            monthPickerDialogDialog?.show()
        }
        viewModel.setDefaultMonth()
    }

    fun setupDatePicker(){
        monthPickerDialogDialog = context?.let { ctx ->
            MonthPickerDialog(ctx) {
                setMonthText(it)
            }
        }
    }

    fun setMonthText(text: String) {
        with(viewModel) {
            currentMonth.set(text)
            selectedMonth.value = text
            isGetStatisticEnabled = true
        }
    }

    fun setupPager(){
        val menu = Hawk.get<String>(Constants.MENU_ITEM).orEmpty()
        val pagers = arrayListOf<Fragment>()
        pagers.add(ByAreaReportFragment())
        pagers.add(SalesTabFragment())
        pagers.add(DailyVisitReportFragment())
        pagers.add(MonthlyVisitReportFragment())
        pagers.add(TableReportPagerFragment())
        /*pagers.add(MostCustomerReportFragment())
        if (menu.contains(Constants.MY_BRANCH_REPORT)){
            pagers.add(ByBranchReportFragment())
        }
        if (menu.contains(Constants.MY_BRANDS_REPORT)){
            pagers.add(ByBrandsReportFragment())
        }
        if (menu.contains(Constants.BY_SALES_REPORT)){
            pagers.add(BySalesReportFragment())
        }*/
        adapterFragment = FragmentPagerAdapter(childFragmentManager, pagers)
        binding.vpReport.adapter = adapterFragment
        binding.vpReport.offscreenPageLimit = 1
        binding.tabLayout.setupWithViewPager(binding.vpReport)

        binding.vpReport.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 4) {
                    viewModel.onSalesReportTabCheckPage.invoke()
                } else {
                    viewModel.monthPickerVisibility.set(View.VISIBLE)
                }
            }

        })
    }

}
