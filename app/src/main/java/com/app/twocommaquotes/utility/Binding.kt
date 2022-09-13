package com.app.twocommaquotes.utility


import android.view.View
import android.view.View.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.app.twocommaquotes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

// for Price with "MRP RS." placeHolder and Strike text
/*@BindingAdapter("setMRPPrice")
fun AppCompatTextView.setMRPPrice(price: Any?) {
    text = buildString {
        append("MRP Rs. ")
        append(price)
    }
//    paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
}*/

@BindingAdapter("setImage")
fun AppCompatImageView.setImage(url: String?) {

    if (!url.isNullOrEmpty()) {
        Glide.with(this).load(url)
            .placeholder(R.drawable.ic_launcher_background).into(this)
    } else {
        setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_background, null)
        )
    }
}

@BindingAdapter("setRadiusImage", "imageRadius")
fun AppCompatImageView.setCropImage(url: String?, radius: Int?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this).asBitmap().load(url).centerCrop()
            .transform(RoundedCorners(radius!!))
            .placeholder(R.drawable.ic_launcher_background).into(this)
    } else {
        setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_background, null)
        )
    }
}

//for change image on click
@BindingAdapter("selectAddress")
fun AppCompatImageView.selectAddress(selected: Int) {
    if (selected == 1) {
        setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_background, null)
        )
    } else {
        setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_foreground, null)
        )

    }
}

//for any View Visibility
@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) VISIBLE else GONE
}
