package com.worka.eroyal.feature.me

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.DatePickerDialog
import com.worka.eroyal.data.user.User
import com.worka.eroyal.databinding.FragmentMePagerBinding
import com.worka.eroyal.feature.common.FragmentPagerAdapter
import com.worka.eroyal.feature.main.MainActivity
import com.worka.eroyal.feature.me.statistic.StatisticMeFragment
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.getDateFormat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class MePagerFragment: BaseFragment() {

    private val viewModel: MeViewModel by sharedViewModel()
    private lateinit var binding: FragmentMePagerBinding
    private lateinit var adapterFragment: FragmentPagerAdapter

    private val datePickerDialog: DatePickerDialog? by lazy {
        context?.let { ctx ->
            val minDate = Calendar.getInstance().time
            minDate.month = minDate.month - 1
            val maxDate = Calendar.getInstance().time
            DatePickerDialog(ctx, minDate = minDate.time, maxDate = maxDate.time) { calendar ->
                setSelectedDate(calendar)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me_pager, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(binding) {
            toolbar.btnBack.visibility = View.GONE
            toolbar.btnLeftDrawer.visibility = View.VISIBLE
            toolbar.tvTitle.text = context?.resources?.getString(R.string.me)
            this.viewModel = this@MePagerFragment.viewModel

            this@MePagerFragment.viewModel.selectedDate.value = Calendar.getInstance().getDateFormat(DateUtils.EEEE_D_MMM_YYYY)

            when (mActivity) {
                is MainActivity -> (mActivity as MainActivity).binding.drawerLayout.setScrimColor(
                    resources.getColor(android.R.color.transparent)
                )
            }

            toolbar.btnLeftDrawer.setOnClickListener {
                when (mActivity) {
                    is MainActivity -> (mActivity as MainActivity).binding.drawerLayout.openDrawer(
                        Gravity.LEFT
                    )
                }
            }

            btnDatePicker.setOnClickListener {
                datePickerDialog?.show()
            }
        }

        viewModel.selectedDate.observe(this, androidx.lifecycle.Observer {
            viewModel.titleText.set(it)
        })

        setupPager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun updateUserImageProfile(user: User){
        viewModel.userImageProfile.set(user.imageProfile)
    }

    private fun setupPager() {
        adapterFragment = FragmentPagerAdapter(childFragmentManager, listOf(
            MeFragment(),
            StatisticMeFragment()
        ))
        binding.vpMe.adapter = adapterFragment
        binding.tabLayout.setupWithViewPager(binding.vpMe)
        binding.vpMe.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                with(viewModel) {
                    when (position) {
                        0 -> {
                            titleText.set(currentMonth.get())
                            btnDatePickerVisibility.set(View.INVISIBLE)
                        }
                        1 -> {
                            titleText.set(selectedDate.value)
                            btnDatePickerVisibility.set(View.VISIBLE)
                        }
                    }
                }
            }

        })
    }

    private fun setSelectedDate(date: Calendar) {
        viewModel.selectedDate.value = date.getDateFormat(DateUtils.EEEE_D_MMM_YYYY)
    }
}
