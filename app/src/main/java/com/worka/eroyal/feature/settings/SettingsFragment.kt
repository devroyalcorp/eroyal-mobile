package com.worka.eroyal.feature.settings

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-21.
 */
class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by sharedViewModel()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.toolbar.tvTitle.text = getString(R.string.settings)
        binding.viewModel = viewModel
        viewModel.navController = Navigation.findNavController(context as Activity, R.id.navSettingsFragment)
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

    override fun onResume() {
        super.onResume()
        binding.invalidateAll()
        binding.notifyChange()
    }

    fun onBackPressed() {
        if (!viewModel.navController.popBackStack()) {
            finishToRight()
        }
    }
}