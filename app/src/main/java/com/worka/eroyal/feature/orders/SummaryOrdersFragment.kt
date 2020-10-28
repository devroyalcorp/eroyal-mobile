package com.worka.eroyal.feature.orders

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentSummaryOrdersBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 30/06/20.
 */
class SummaryOrdersFragment : BaseFragment() {

    private lateinit var binding: FragmentSummaryOrdersBinding
    private val viewModel: OrdersViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary_orders, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        context?.let { ctx ->
            viewModel.setTitlePage(ctx.getString(R.string.summary_order))

            with(binding) {
                btnDownload.setOnClickListener {
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.downloading_so),
                        Toast.LENGTH_SHORT
                    ).show()
                    this@SummaryOrdersFragment.viewModel.getSoDocs()
                }
            }

            mActivity.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (viewModel.downloadId == id) {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.so_downloaded_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btnDownload.isEnabled = false
                    }
                }

            }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

    }
}
