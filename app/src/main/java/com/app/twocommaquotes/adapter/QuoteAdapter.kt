package com.app.twocommaquotes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.twocommaquotes.R
import com.app.twocommaquotes.databinding.ItemTextBinding
import com.app.twocommaquotes.model.QuoteModelNew

class QuoteAdapter : ListAdapter<QuoteModelNew, QuoteAdapter.MyViewHolder>(DiffUtil()) {

    class MyViewHolder(binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root) {
        val myBinding = binding
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<QuoteModelNew>() {
        override fun areItemsTheSame(oldItem: QuoteModelNew, newItem: QuoteModelNew): Boolean {
            return oldItem.q == newItem.q
        }

        override fun areContentsTheSame(oldItem: QuoteModelNew, newItem: QuoteModelNew): Boolean {
            return oldItem.q == newItem.q
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.myBinding.tvQuoteText.resources.getColor(R.color.colorWhite)
        holder.myBinding.quotesList = currentList[position].h.toString()
    }

}