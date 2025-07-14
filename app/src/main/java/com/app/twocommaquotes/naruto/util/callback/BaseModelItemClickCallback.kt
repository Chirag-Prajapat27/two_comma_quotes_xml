package com.app.twocommaquotes.naruto.util.callback

import androidx.cardview.widget.CardView
import com.app.twocommaquotes.naruto.util.model.BaseModelNaruto

interface BaseModelItemClickCallback {

	fun onItemClick(item: BaseModelNaruto, itemCard: CardView)

}
