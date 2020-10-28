package com.worka.eroyal.feature.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.DatePickerDialog
import com.worka.eroyal.databinding.FragmentTaskListBinding
import com.worka.eroyal.extensions.getCurrentDate
import com.worka.eroyal.feature.common.GenericAppAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class TaskListFragment : BaseFragment() {

    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: GenericAppAdapter<TaskItemViewModel>
    private  var adapterProgress: GenericAppAdapter<TargetProgressItemViewModel>? = null

    private val datePickerDialog: DatePickerDialog? by lazy {
        context?.let { ctx ->
            val minDate = Calendar.getInstance().time
            val maxDate = Calendar.getInstance().time
            maxDate.month = maxDate.month + 1
            DatePickerDialog(ctx, minDate = minDate.time, maxDate = maxDate.time) { calendar ->
                viewModel.setSelectedDate(calendar)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.title.value = context?.getString(R.string.tasks)
        viewModel.dateDisplay.set(getCurrentDate())
        getData()

        binding.srTaskList.setOnRefreshListener {
            binding.srTaskList.isRefreshing = true
            getData()
        }

        binding.btnDatePicker.setOnClickListener {
            datePickerDialog?.show()
        }

        viewModel.date.observe(this, androidx.lifecycle.Observer {
            getData()
        })
    }

    fun getData(){
        mActivity.showLoading()
        viewModel.getTasksData({
            binding.srTaskList.isRefreshing = false
            mActivity.hideLoading()
            setupTaskList()
            if (adapterProgress == null) {
                setupTaskProgressList()
            }
        }, {
            binding.srTaskList.isRefreshing = false
            mActivity.hideLoading()
        })
    }

    fun setupTaskList() {
        adapter = GenericAppAdapter(viewModel.getTaskList{
            viewModel.navController.navigate(R.id.taskDetailsFragment)
        })
        binding.rvTask.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvTask.adapter = adapter
    }

    fun setupTaskProgressList() {
        adapterProgress = GenericAppAdapter(viewModel.getTaskProgressList())
        binding.rvProgress.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProgress.adapter = adapterProgress
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvProgress)
    }

}
