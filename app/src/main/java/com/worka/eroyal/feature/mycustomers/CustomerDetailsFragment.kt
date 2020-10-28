package com.worka.eroyal.feature.mycustomers

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants.SELECTED_CUSTOMER
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.toGson
import com.worka.eroyal.databinding.FragmentCustomerDetailsBinding
import com.worka.eroyal.feature.common.FragmentPagerAdapter
import com.worka.eroyal.feature.mycustomers.bill.BillFragment
import com.worka.eroyal.feature.mycustomers.market.MarketCustomerFragment
import com.worka.eroyal.feature.mycustomers.orders.OrdersCustomerFragment
import com.worka.eroyal.feature.mycustomers.notes.NotesCustomerFragment
import com.worka.eroyal.feature.mycustomers.promo.PromoCustomerFragment
import com.worka.eroyal.feature.mycustomers.sales.SalesCustomerFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-14.
 */
class CustomerDetailsFragment : BaseFragment() {

    private val viewModel: MyCustomerViewModel by sharedViewModel()
    lateinit var binding: FragmentCustomerDetailsBinding
    private lateinit var fragmentPagerAdapter: FragmentPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_details, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity.showLoading()
        binding.viewModel = viewModel
        arguments?.getString(SELECTED_CUSTOMER)?.let {
            viewModel.navController = Navigation.findNavController(context as Activity, R.id.nav_report_fragment)
            viewModel.selectedCustomer = it.toGson(Customer::class.java)
            binding.layoutToolbar.visibility = View.GONE
        }
        binding.toolbar.tvTitle.text = viewModel.selectedCustomer?.name

        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.getCustomerDetails({
            mActivity.hideLoading()
        }, {
            mActivity.hideLoading()
        })
        setTabAndPager()
    }

    private fun setTabAndPager() {
        val fragments = listOf<Fragment>(
            SalesCustomerFragment(),
            BillFragment(),
            NotesCustomerFragment(),
            MarketCustomerFragment(),
            PromoCustomerFragment(),
            OrdersCustomerFragment())

        val titles = listOf(
            context?.getString(R.string.sales).orEmpty(),
            context?.getString(R.string.orders).orEmpty(),
            context?.getString(R.string.notes).orEmpty(),
            context?.getString(R.string.market).orEmpty(),
            context?.getString(R.string.gallery).orEmpty(),
            context?.getString(R.string.program).orEmpty())

        val icons = listOf(
            R.drawable.ic_sales_inactive,
            R.drawable.ic_order_inactive,
            R.drawable.ic_last_note_inactive,
            R.drawable.ic_marketshare_inactive,
            R.drawable.ic_promo_inactive,
            R.drawable.ic_program_inactive)

        val activeIcons = listOf(
            R.drawable.ic_sales,
            R.drawable.ic_order_active,
            R.drawable.ic_last_note,
            R.drawable.ic_marketshare,
            R.drawable.ic_promo,
            R.drawable.ic_program)

        fragmentPagerAdapter = FragmentPagerAdapter(childFragmentManager, fragments, titles)

        binding.vpCustomerDetails.adapter = fragmentPagerAdapter
        binding.vpCustomerDetails.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabLayout
            )
        )
        binding.vpCustomerDetails.offscreenPageLimit = 1
        binding.tabLayout.tabRippleColor = null
        binding.tabLayout.setupWithViewPager(binding.vpCustomerDetails)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val textView = view?.findViewById<TextView>(R.id.tab_title)
                val imgView = view?.findViewById<ImageView>(R.id.img_menu_tab)
                context?.let { ctx ->
                    textView?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGreen))
                    imgView?.setImageResource(activeIcons[tab.position])
                }
                binding.vpCustomerDetails.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view?.findViewById<TextView>(R.id.tab_title)
                val imgView = view?.findViewById<ImageView>(R.id.img_menu_tab)
                context?.let { ctx ->
                    textView?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrey))
                    imgView?.setImageResource(icons[tab.position])
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val view = tab.customView
                val textView = view?.findViewById<TextView>(R.id.tab_title)
                val imgView = view?.findViewById<ImageView>(R.id.img_menu_tab)
                context?.let { ctx ->
                    textView?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGreen))
                    imgView?.setImageResource(activeIcons[tab.position])
                }
                binding.vpCustomerDetails.currentItem = tab.position
            }
        })

        titles.forEachIndexed { index: Int, text: String ->
            binding.tabLayout.getTabAt(index)?.setCustomView(R.layout.item_tab_customer_details)
            val title = binding.tabLayout.getTabAt(index)?.customView?.findViewById<TextView>(R.id.tab_title)
            context?.let {ctx ->
                title?.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrey))
            }
            title?.text = text
        }
        icons.forEachIndexed { index, drawable ->
            val icon = binding.tabLayout.getTabAt(index)?.customView?.findViewById<ImageView>(R.id.img_menu_tab)
            icon?.setImageResource(drawable)
        }

        Handler().postDelayed({
            binding.vpCustomerDetails.currentItem = 0
            val tab = binding.tabLayout.getTabAt(0)
            tab?.select()
        }, 300)

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
        if (!viewModel.navController.popBackStack()) {
            finishToRight()
        }
    }
}
