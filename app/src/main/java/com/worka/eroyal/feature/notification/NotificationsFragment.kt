package com.worka.eroyal.feature.notification

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentNotificationBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-19.
 */
class NotificationsFragment : BaseFragment() {

    val viewModel: NotificationViewModel by sharedViewModel()

    private lateinit var binding: FragmentNotificationBinding
    private var adapter: GenericAppAdapter<NotificationItemViewModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.toolbar.tvTitle.text = getString(R.string.notifications)
        binding.toolbar.imgAvatar.visibility = View.INVISIBLE
        binding.viewModel = viewModel
        mActivity.showLoading()
        getData()
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnDeleteNotifications.setOnClickListener {
            context?.let {ctx ->
                CustomInfoDialog(ctx, context?.getString(R.string.are_you_sure_delete_all_notifications),
                    onDismiss = {
                        deleteAllNotification()
                    })
            }

        }
    }

    fun getData(){
        viewModel.getNotifications({
            setupList()
            mActivity.hideLoading()
        }, {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })
    }

    fun deleteAllNotification(){
        mActivity.showLoading()
        viewModel.deleteAllNotification({
            getData()
        },{
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })
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

    fun setupList() {
        adapter = GenericAppAdapter(viewModel.getNotificationList {
            adapter?.notifyDataSetChanged()
        })
        binding.rvNotifications.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvNotifications.adapter = adapter

        adapter?.list?.let {
            if (it.isEmpty()) {
                binding.viewEmptyNotification.visibility = View.VISIBLE
                binding.btnDeleteNotifications.visibility = View.INVISIBLE
            } else {
                binding.btnDeleteNotifications.visibility = View.VISIBLE
                binding.viewEmptyNotification.visibility = View.GONE
            }
        }
    }

    fun onBackPressed() {
        mActivity.onBackPressed()
    }
}