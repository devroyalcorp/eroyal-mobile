package com.worka.eroyal.feature.tasks.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.worka.eroyal.R
import com.worka.eroyal.base.Constants
import com.worka.eroyal.databinding.LayoutDropTaskDialogBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-13.
 */

object DropTaskDialogHelper {
        fun confirmationDialog(context: Context?, cbOnConfirm: (String?) -> Unit): Dialog? {
            var selectedReason: String? = null
            return context?.run {
                val dropTaskDialogBinding = DataBindingUtil.inflate<LayoutDropTaskDialogBinding>(LayoutInflater.from(context),
                    R.layout.layout_drop_task_dialog, null, false).apply {

                    rgDropReason.setOnCheckedChangeListener { group, checkedId ->
                        when(checkedId) {
                            rbWrongAddress.id -> selectedReason = Constants.WRONG_ADDRESS
                            rbTargetDoesntExist.id -> selectedReason = Constants.TARGET_DOESNT_EXIST
                        }
                        btnConfirmDropTask.isEnabled = true
                    }
                }
                Dialog(context).apply {
                    setContentView(dropTaskDialogBinding.root)
                    val colorDrawable = context?.resources?.getColor(android.R.color.transparent)?.let { ColorDrawable(it) }
                    window?.setBackgroundDrawable(colorDrawable)
                    dropTaskDialogBinding.btnCloseDropTask.setOnClickListener {
                        dismiss()
                    }

                    dropTaskDialogBinding.btnConfirmDropTask.setOnClickListener {
                        cbOnConfirm.invoke(selectedReason)
                        dismiss()
                    }
                }
            }
        }

}