package com.worka.eroyal.feature.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worka.eroyal.BR

open class GenericViewHolder<out V : ViewDataBinding>(private val viewDataBinding: V) : RecyclerView.ViewHolder(viewDataBinding.root) {

    fun getBinding(): V {
        return viewDataBinding
    }

}

open class GenericAppAdapter<T>(var list: List<T>) : ListAdapter<T, GenericViewHolder<*>>(TaskDiffCallback()) {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return (list[position] as SimpleViewModel).layoutId()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {
        val bind = DataBindingUtil.bind<ViewDataBinding>(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
            ?: throw IllegalArgumentException()
        return GenericViewHolder(bind)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<*>, position: Int) {
        val model = list[position]
        holder.getBinding().setVariable(BR.rowViewModel, model)
        holder.getBinding().executePendingBindings()
    }
}