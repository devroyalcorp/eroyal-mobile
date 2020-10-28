package com.worka.eroyal.feature.mycustomers

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.databinding.FragmentMyCustomerListBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-10.
 */
class MyCustomerListFragment: BaseFragment(), Paginate.Callbacks {

    private val viewModel: MyCustomerViewModel by sharedViewModel()

    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    lateinit var binding: FragmentMyCustomerListBinding

    private var areaFilterDialog: CheckBoxFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_customer_list, container, false)
        return  binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.tvTitle.text = context?.resources?.getString(R.string.my_customers)
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPage = false
            viewModel.page = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupList()
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }

        with(binding) {
            btnAreaFilter.setOnClickListener {
                areaFilterDialog?.let {
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

        with(viewModel) {

            if (brandFilterList.isNullOrEmpty()) {
                getBrandFilterList {
                    mActivity.hideLoading()
                    setupBrandFilterDialog()
                }
            }

            if (areaFilterList.isNullOrEmpty()) {
                getAreaFilterList {
                    mActivity.hideLoading()
                    setupAreaFilterDialog()
                }
            }

            customerViewModelList.observe(viewLifecycleOwner, Observer {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
            })
        }

        initSearch()
    }

    fun setupList() {
        binding.rvMyCustomer.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMyCustomer.adapter = adapter

        Paginate.with(binding.rvMyCustomer, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    fun getData(){
        mActivity.hideKeyboard()
        if (viewModel.page == 1) {
            mActivity.showLoading()
        }
        viewModel.isLoading = true
        viewModel.getMyCustomer({
            mActivity.hideLoading()
            mActivity.hideKeyboard()
        }, {
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }, {
            viewModel.navController.navigate(R.id.action_myCustomerListFragment_to_customerDetailsFragment)
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

    private fun initSearch() {
        handler = Handler()
        with(binding) {
            svMyCustomer.setOnQueryTextListener(getQueryListener())
        }
    }

    private fun getQueryListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                with(viewModel) {
                    searchKeyword.set(query)
                    page = 1
                    isLastPage = false
                    getData()
                    binding.svMyCustomer.clearFocus()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                with(viewModel) {
                    if (null != runnable) {
                        handler?.removeCallbacks(runnable)
                    }

                    runnable = Runnable {
                        searchKeyword.set(newText)
                        page = 1
                        isLastPage = false
                        getData()
                        binding.svMyCustomer.clearFocus()
                    }
                    handler?.postDelayed(runnable, 1000)
                }
                return true
            }
        }
    }

    private fun setupAreaFilterDialog() {
        context?.let { ctx ->
            areaFilterDialog = CheckBoxFilterDialog(
                ctx,
                ctx.getString(R.string.select_which_area_to_show),
                viewModel.areaFilterList
            ) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    areaFilterList = list
                    setAreaFilterTitle()
                    page = 1
                }
                getData()
            }
        }
    }

    private fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(
                ctx,
                ctx.getString(R.string.select_which_brand_to_show),
                viewModel.brandFilterList
            ) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    brandFilterList = list
                    setBrandFilterTitle()
                    page = 1
                }
                getData()
            }
        }
    }

    fun onBackPressed() {
        finishToRight()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPage

    override fun isLoading(): Boolean = viewModel.isLoading

    override fun onLoadMore() {
        getData()
    }
}
