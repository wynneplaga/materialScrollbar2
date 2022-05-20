package com.wynneplaga.materialscrollbar2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wynneplaga.materialScrollBar2.inidicators.AlphabeticIndicator
import com.wynneplaga.materialScrollBar2.inidicators.CustomIndicator
import com.wynneplaga.materialScrollBar2.inidicators.DateTimeIndicator
import com.wynneplaga.materialscrollbar2.databinding.ViewTextHolderBinding
import java.util.*

class MainAdapter: ListAdapter<String, MainAdapter.SampleViewHolder>(StringDiff()), AlphabeticIndicator.INameableAdapter, CustomIndicator.ICustomAdapter, DateTimeIndicator.IDateableAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(ViewTextHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.binding.textView.text = currentList[position]
    }

    class SampleViewHolder(val binding: ViewTextHolderBinding): RecyclerView.ViewHolder(binding.root)

    class StringDiff: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean  = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }

    override fun getCharacterForElement(element: Int) = currentList[element].first()

    override fun getCustomStringForElement(element: Int): String = currentList[element]

    override fun getDateForElement(element: Int): Date {
        return Date((0..System.currentTimeMillis()).random())
    }

}