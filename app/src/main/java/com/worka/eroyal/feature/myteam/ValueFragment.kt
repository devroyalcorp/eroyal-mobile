package com.worka.eroyal.feature.myteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentValueBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/06/20.
 */
class ValueFragment : BaseFragment() {

    private val viewModel: MyTeamViewModel by sharedViewModel()

    private lateinit var binding: FragmentValueBinding

    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    private var brandFilterDialog: CheckBoxFilterDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_value, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        with(viewModel) {
            valueVisitsItemViewModel.observe(viewLifecycleOwner, Observer {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
                mActivity.hideLoading()
            })
        }

        with(binding) {
            btnBrandFilter.setOnClickListener {
                brandFilterDialog?.let {
                    it.show()
                } ?: run {
                    context?.let { ctx ->
                        CustomInfoDialog(
                            ctx,
                            ctx.getString(R.string.filter_not_ready_yet)
                        )
                    }
                }
            }
        }

        getData()
        setupTable()
        setupBrandFilterDialog()
    }

    private fun getData() {
        mActivity.showLoading()
        viewModel.getSalesValue()
    }

    private fun setupTable() {
        adapter = GenericAppAdapter(arrayListOf())
        binding.rvValue.layoutManager = GridLayoutManager(context, Constants.ME_SALES_VALUE)
        binding.rvValue.adapter = adapter

    }

    private fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(
                ctx,
                ctx.getString(R.string.select_which_brand_to_show),
                viewModel.valueBrandFilterList
            ) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    valueBrandFilterList = list
                    setValueBrandFilterTitle()
                }
                getData()
            }
        }
    }
}
