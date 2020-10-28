package com.worka.eroyal.feature.myteam

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
import com.worka.eroyal.databinding.FragmentListMyTeamBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */
class MyTeamListFragment : BaseFragment(), Paginate.Callbacks {

    private lateinit var binding: FragmentListMyTeamBinding
    private val viewModel: MyTeamViewModel by sharedViewModel()
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    private var areaFilterDialog: CheckBoxFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_my_team, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.tvTitle.text = context?.resources?.getString(R.string.my_team)
        binding.toolbar.imgAvatar.visibility = View.INVISIBLE

        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageMyTeam = false
            viewModel.myTeamPage = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupList()

        with(binding) {

            binding.toolbar.btnBack.setOnClickListener {
                onBackPressed()
            }

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

            myTeamItemViewModel.observe(viewLifecycleOwner, Observer {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
            })

            onSelectMyTeam.observe(viewLifecycleOwner, Observer {
                navController?.navigate(R.id.action_myTeamListFragment_to_myTeamDetailsFragment)
            })
        }

        initSearch()

    }

    fun setupList() {
        binding.rvMyTeam.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMyTeam.adapter = adapter

        Paginate.with(binding.rvMyTeam, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()

        adapter?.notifyDataSetChanged()
    }

    private fun getData() {
        if (viewModel.myTeamPage == 1) {
            mActivity.showLoading()
        }
        viewModel.getMyTeam({
            mActivity.hideLoading()
        }, {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })
    }

    private fun initSearch() {
        handler = Handler()
        with(binding) {
            svMyTeam.setOnQueryTextListener(getQueryListener())
        }
    }

    private fun getQueryListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                with(viewModel) {
                    emptyMyTeamVisibility.set(false)
                    searchKeyword.set(query)
                    myTeamPage = 1
                    isLastPageMyTeam = false
                    getData()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    with(viewModel) {
                        emptyMyTeamVisibility.set(false)
                        if (null != runnable) {
                            handler?.removeCallbacks(runnable)
                        }

                        runnable = Runnable {
                            searchKeyword.set(newText)
                            myTeamPage = 1
                            isLastPageMyTeam = false
                            binding.svMyTeam.clearFocus()
                            getData()
                        }
                        handler?.postDelayed(runnable, 1000)
                    }
                }
                return true
            }
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
                    myTeamPage = 1
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
                    myTeamPage = 1
                }
                getData()
            }
        }
    }

    fun onBackPressed() {
        finishToRight()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageMyTeam
    override fun isLoading(): Boolean = viewModel.isLoadingMyTeam
    override fun onLoadMore() {
        getData()
    }
}
