package com.worka.eroyal.extensions

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.worka.eroyal.R

@BindingAdapter("circleLoadImage", "initial")
fun ImageView.circleLoadImage(imageSource: Uri?, initial: String?) {
    urCircleImage(imageSource, initial)
}

fun ImageView.urCircleImage(imageSource: Uri?, initial: String?) {
    val drawable = TextDrawable.builder()
        .buildRound(initial, ContextCompat.getColor(context, R.color.colorPlaceHolder))

    Glide.with(context)
        .load(imageSource)
        .apply(RequestOptions.circleCropTransform().placeholder(drawable))
        .into(this)
}

@BindingAdapter("circleLoadImage", "initial")
fun ImageView.circleLoadImage(imageSource: String?, initial: String?) {
    urCircleImage(imageSource, initial)
}

fun ImageView.urCircleImage(imageSource: String?, initial: String?) {
    val drawable = TextDrawable.builder()
        .buildRound(initial, ContextCompat.getColor(context, R.color.colorPlaceHolder))

    Glide.with(context)
        .load(imageSource.orEmpty())
        .apply(RequestOptions.circleCropTransform().placeholder(drawable))
        .into(this)
}

@BindingAdapter("loadImage")
fun ImageView.loadImage(imageId: Int) {
    Glide.with(context)
        .load(imageId)
        .into(this)
}
@BindingAdapter("loadResource")
fun ImageView.loadResource(imageId: Int) {
    loadResourceImage(imageId, R.drawable.bg_circle_placeholder)
}

fun ImageView.loadResourceImage(imageId: Int, placeholder: Int) {
    Glide.with(context)
        .load(imageId)
        .apply(RequestOptions().placeholder(placeholder))
        .into(this)
}


@BindingAdapter("imageUrlRadius", "radius")
fun ImageView.loadImageRadius(imageUrlRadius: String?, radius: Int) {
    if(!imageUrlRadius.isNullOrBlank()) {
        loadResourceImageRadius(imageUrlRadius, radius)
    } else {
        visibility = View.GONE
    }
}

fun ImageView.loadResourceImageRadius(imageId: String?, radius: Int) {
    Glide.with(context)
        .load(imageId)
        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radius)))
        .into(this)
}

@BindingAdapter("imageUrlRadius", "radius")
fun ImageView.loadImageRadius(imageUrlRadius: Uri?, radius: Int) {
    loadResourceImageRadius(imageUrlRadius, radius)
}

fun ImageView.loadResourceImageRadius(imageId: Uri?, radius: Int) {
    Glide.with(context)
        .load(imageId)
        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radius)))
        .into(this)
}
