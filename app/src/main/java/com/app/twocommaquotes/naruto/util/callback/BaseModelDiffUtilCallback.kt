package com.app.twocommaquotes.naruto.util.callback

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.app.twocommaquotes.naruto.util.model.BaseModelNaruto

class BaseModelDiffUtilCallback<T : BaseModelNaruto> : DiffUtil.ItemCallback<T>() {
	override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
		return oldItem.id == newItem.id
	}

	@SuppressLint("DiffUtilEquals")
	override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
		return oldItem == newItem
	}
}
