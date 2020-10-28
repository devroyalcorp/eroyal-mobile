package com.worka.eroyal.component.customcomponent


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import androidx.lifecycle.MutableLiveData
import com.worka.eroyal.R
import kotlinx.android.synthetic.main.view_toggle.view.*


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-09.
 */

@InverseBindingMethods(InverseBindingMethod(type = Boolean::class, attribute = "checked"))
class ToggleView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var checked: MutableLiveData<Boolean> = MutableLiveData()

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        View.inflate(context, R.layout.view_toggle, this)
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ToggleView, 0, 0)

        val active = typedArray.getText(R.styleable.ToggleView_activeText)
        val inactive = typedArray.getText(R.styleable.ToggleView_inactiveText)
        checked.value = false
        typedArray.recycle()

        tv_active_toggle.text = active
        tv_inactive_toggle.text = inactive

        checked.value?.let { isSelected = it }

        toggle_active.setOnClickListener {
            this.isSelected = false
            checked.value = false
        }
        toggle_inactive.setOnClickListener {
            this.isSelected = true
            checked.value = true
        }
        checked.observeForever {
            isSelected = it
        }

    }

    fun onCheckedChange(cb: (Boolean) -> Unit) {
        checked.observeForever {
            cb.invoke(it)
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            toggle_inactive.visibility = View.GONE
            toggle_active.visibility = View.VISIBLE
        } else {
            toggle_inactive.visibility = View.VISIBLE
            toggle_active.visibility = View.GONE
        }

    }
}

@BindingAdapter("check")
fun ToggleView.setChecked(check: Boolean) {
    if (check != checked.value) {
        checked.value = check
    }
}

@InverseBindingAdapter(attribute = "check")
fun ToggleView.getChecked(): Boolean {
    return this.checked.value ?: false
}

@BindingAdapter("checkAttrChanged")
fun ToggleView.setCheckedListener(onCheckedChanged: InverseBindingListener?) {
    this.onCheckedChange {
        onCheckedChanged?.onChange()
    }
}

