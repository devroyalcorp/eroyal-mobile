package com.worka.eroyal.feature.mycustomers.promo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.paginate.Paginate
import com.stfalcon.imageviewer.StfalconImageViewer
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentCustomerPromoBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.MyCustomerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-24.
 */
class PromoCustomerFragment: BaseFragment(), Paginate.Callbacks {
    private val viewModel: MyCustomerViewModel by sharedViewModel()
    private lateinit var binding: FragmentCustomerPromoBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_promo, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupList()
        viewModel.promoViewModelList.observeForever {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        }
    }

    fun setupList() {
        adapter = GenericAppAdapter(arrayListOf())
        viewModel.isLastPromoPage = false
        viewModel.promoPage = 1

        binding.rvPromo.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPromo.adapter = adapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvPromo)

        Paginate.with(binding.rvPromo, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    fun getData(){
        viewModel.isPromoLoading = true
        viewModel.getPromo({
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }, { position, url ->
            context?.let { ctx ->

                StfalconImageViewer.Builder<String>(ctx, viewModel.getImageUrls()) { view, imageUrl ->
                    Glide.with(ctx).load(imageUrl).into(view)
                }.withBackgroundColorResource(android.R.color.black).withStartPosition(position).show()
            }
        })
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPromoPage

    override fun isLoading(): Boolean = viewModel.isPromoLoading

    override fun onLoadMore() {
        getData()
    }
}
