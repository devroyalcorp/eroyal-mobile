package com.worka.eroyal.feature.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentTaskDetailsBinding
import com.worka.eroyal.feature.tasks.dialog.DropTaskDialogHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class TaskDetailsFragment: BaseFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentTaskDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_details, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        binding.btnDropTask.setOnClickListener {
            DropTaskDialogHelper.confirmationDialog(context) {
                mActivity.showLoading()
                viewModel.dropTaskReason.set(it)
                viewModel.dropTask({
                    mActivity.hideLoading()
                    viewModel.navController.popBackStack()
                }, {
                    mActivity.hideLoading()
                    context?.let { ctx -> CustomInfoDialog(ctx, it) }
                })
            }?.show()
        }

        binding.btnCheckIn.setOnClickListener {
            mActivity.requestForPermissions(Constants.LOCATION_PERMISSIONS, Constants.REQUEST_LOCATION, grantResults = { _, permissionResult ->
                if (permissionResult == PermissionResult.GRANTED) {
                    viewModel.navController.navigate(R.id.action_taskDetailsFragment_to_checkInMapsFragment)
                } else {
                    context?.let { ctx -> CustomInfoDialog(ctx, getString(R.string.location_permission_required)) }
                }
            })
        }
    }

}