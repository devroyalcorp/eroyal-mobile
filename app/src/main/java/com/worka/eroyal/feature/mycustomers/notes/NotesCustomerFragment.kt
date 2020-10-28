package com.worka.eroyal.feature.mycustomers.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentCustomerNotesBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.MyCustomerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-23.
 */
class NotesCustomerFragment: BaseFragment(), Paginate.Callbacks {
    private val viewModel: MyCustomerViewModel by sharedViewModel()
    private lateinit var binding: FragmentCustomerNotesBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_notes, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupList()
        viewModel.noteViewModelList.observeForever {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        }
    }

    fun setupList() {
        adapter = GenericAppAdapter(arrayListOf())
        viewModel.isLastNotesPage = false
        viewModel.notesPage = 1

        binding.rvNotes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvNotes.adapter = adapter

        Paginate.with(binding.rvNotes, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    fun getData(){
        viewModel.isLoading = true
        viewModel.getNotes{
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastNotesPage

    override fun isLoading(): Boolean = viewModel.isLoading

    override fun onLoadMore() {
        getData()
    }
}
