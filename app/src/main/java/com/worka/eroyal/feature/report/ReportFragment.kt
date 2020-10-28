package com.worka.eroyal.feature.report

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentReportBinding
import com.worka.eroyal.extensions.getInitialName
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-29.
 */
class ReportFragment: BaseFragment() {

    private val viewModel: ReportViewModel by sharedViewModel()
    private lateinit var binding: FragmentReportBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.navController = Navigation.findNavController(context as Activity, R.id.nav_report_fragment)
        binding.toolbar.avatarUrl = viewModel.user.imageProfile
        binding.toolbar.initial = viewModel.user.name.getInitialName()
        viewModel.titlePage.value = context?.getString(R.string.my_report)
        viewModel.titlePage.observeForever {
            binding.toolbar.tvTitle.text = it
        }
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }

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
        if (!viewModel.navController.popBackStack()) {
            finishToRight()
        }
    }

}
