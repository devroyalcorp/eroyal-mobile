package com.worka.eroyal.feature.tasks

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.FLOW_TYPE
import com.worka.eroyal.base.Constants.TASK
import com.worka.eroyal.base.Constants.VISIT
import com.worka.eroyal.databinding.FragmentTasksBinding
import com.worka.eroyal.extensions.getInitialName
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TasksFragment : BaseFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.title.observeForever {
            binding.toolbar.tvTitle.text = it
        }
        viewModel.navController = Navigation.findNavController(context as Activity, R.id.nav_task_fragment)
        val navGraph = viewModel.navController.navInflater.inflate(R.navigation.task_navigation)
        when(arguments?.getString(FLOW_TYPE)){
            TASK -> {
                binding.toolbar.tvTitle.text = getString(R.string.tasks)
                navGraph.startDestination = R.id.taskListFragment
            }
            VISIT -> {
                binding.toolbar.tvTitle.text = getString(R.string.visits)
                navGraph.startDestination = R.id.followUpVisitFragment
            }
        }
        viewModel.navController.graph = navGraph
        binding.toolbar.avatarUrl = viewModel.user.imageProfile
        binding.toolbar.initial = viewModel.user.name.getInitialName()
        arguments?.getBoolean(Constants.IS_FROM_BOTTOM_BAR, false)?.let {
            if (it) {
                binding.toolbar.btnBack.visibility = View.INVISIBLE
            }
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
        with(viewModel) {
            if (navController.currentDestination?.id == R.id.signatureFormFragment) {
                if (isBackButtonPressedOnce) {
                    mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    isBackButtonPressedOnce = false
                    goBack()
                } else {
                    isBackButtonPressedOnce = true
                    Toast.makeText(
                        context,
                        resources.getString(R.string.press_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler().postDelayed({ isBackButtonPressedOnce = false }, 1000)
                }
            } else {
                goBack()
            }
        }
    }

    private fun goBack() {
        if (!viewModel.navController.popBackStack()) {
            finishToRight()
        }
    }
}
