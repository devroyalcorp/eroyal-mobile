package com.worka.eroyal.feature.report.saleschart

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
import com.worka.eroyal.databinding.FragmentSalesTableBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/06/20.
 */
class SalesTableFragment : BaseFragment() {

    private lateinit var binding: FragmentSalesTableBinding

    private val viewModel: ReportViewModel by sharedViewModel()

    private var adapter: GenericAppAdapter<SimpleViewModel> ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_table, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.salesReportViewModelList.observe(this, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })

        setupTable()
    }

    private fun setupTable() {
        adapter = GenericAppAdapter(arrayListOf())
        binding.rvSalesTable.layoutManager = GridLayoutManager(context, Constants.TABLE_SALES_REPORT)
        binding.rvSalesTable.adapter = adapter
    }
}
