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
import com.worka.eroyal.databinding.FragmentRkbVisitBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 10/06/20.
 */
class RKBVisitFragment : BaseFragment(){

    private val viewModel: MyTeamViewModel by sharedViewModel()

    private lateinit var binding: FragmentRkbVisitBinding

    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rkb_visit, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        with(viewModel) {
            rkbVisitsItemViewModel.observe(viewLifecycleOwner, Observer {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
            })
        }

        getData()
        setupTable()
    }

    private fun getData() {
        viewModel.getRKBVisits()
    }

    private fun setupTable() {
        adapter = GenericAppAdapter(arrayListOf())
        binding.rvRkbVisits.layoutManager = GridLayoutManager(context, Constants.ME_RKB_VISITS)
        binding.rvRkbVisits.adapter = adapter

    }
}
