package com.r.cohen.poolsidefm.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

@BindingAdapter("loadWithGlide")
fun ImageView.loadwithGlide(url: String) {
    val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .into(this)
}