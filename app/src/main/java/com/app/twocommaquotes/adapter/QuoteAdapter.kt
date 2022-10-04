package com.app.twocommaquotes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.twocommaquotes.R
import com.app.twocommaquotes.databinding.ItemTextBinding
import com.app.twocommaquotes.model.Result

class QuoteAdapter : ListAdapter< Result, QuoteAdapter.MyViewHolder>(DiffUtil()) {

    class MyViewHolder(binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root) {
        val myBinding = binding
    }

    class DiffUtil :  androidx.recyclerview.widget.DiffUtil.ItemCallback<Result> () {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.myBinding.quotesList = currentList[position]
        holder.myBinding.tvQuoteText.resources.getColor(R.color.colorWhite)
    }

}