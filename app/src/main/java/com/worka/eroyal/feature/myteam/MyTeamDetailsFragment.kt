package com.worka.eroyal.feature.myteam

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentMyTeamDetailsBinding
import com.worka.eroyal.feature.common.FragmentPagerAdapter
import com.worka.eroyal.feature.report.table.CustomerActiveDetailFragment
import com.worka.eroyal.feature.report.table.CustomerActiveRecapFragment
import com.worka.eroyal.feature.report.table.SalesReportByArticleFragment
import com.worka.eroyal.feature.report.table.SalesReportByCustomerFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 09/06/20.
 */
class MyTeamDetailsFragment: BaseFragment() {

    private val viewModel: MyTeamViewModel by sharedViewModel()

    private lateinit var binding: FragmentMyTeamDetailsBinding
    private lateinit var adapterFragment: FragmentPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_team_details, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(binding) {
            this.viewModel = this@MyTeamDetailsFragment.viewModel
            toolbar.tvTitle.text = context?.resources?.getString(R.string.my_team)
            toolbar.imgAvatar.visibility = View.INVISIBLE

            toolbar.btnBack.setOnClickListener {
                onBackPressed()
            }
        }
        setupPager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    onBackPressed()
                    return true
                }
                return false
            }
        })
    }

    fun onBackPressed() {
       viewModel.navController?.popBackStack()
    }

    private fun setupPager() {
        val pagers = arrayListOf<Fragment>()
        pagers.add(ValueFragment())
        pagers.add(QuantityFragment())
        pagers.add(RKBVisitFragment())
        pagers.add(RKBVisitChartFragment())

        adapterFragment = FragmentPagerAdapter(childFragmentManager, pagers)
        binding.vpMyTeam.adapter = adapterFragment
        binding.vpMyTeam.offscreenPageLimit = 1
        binding.tabLayout.setupWithViewPager(binding.vpMyTeam)

    }
}
