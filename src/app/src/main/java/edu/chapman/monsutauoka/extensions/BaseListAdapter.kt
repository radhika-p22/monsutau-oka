package edu.chapman.monsutauoka.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BaseListAdapter<T>(
    private val layoutRes: Int,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val onBind: (View, T, Int) -> Unit
) : ListAdapter<T, BaseListAdapter<T>.BaseViewHolder>(diffCallback) {

    inner class BaseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: T, position: Int) {
            onBind(view, item, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}
