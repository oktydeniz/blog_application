package com.example.blogapp.util

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.bumptech.glide.request.RequestOptions

fun ImageView.downloadFromUrl(url: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher_round)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}

fun placeHolderProgressBar(c: Context): CircularProgressDrawable {
    return CircularProgressDrawable(c).apply {
        strokeWidth = 8f
        centerRadius = 48f
        start()
    }
}

@BindingAdapter("android:downloadUrl")
fun downloadImage(v: ImageView, url: String?) {
    val allUrl = "http://192.168.1.63:8000/storage/profiles/$url"
    Log.i("Download Image", "downloadImage: DownloadImg Prof : $allUrl")
    v.downloadFromUrl(allUrl, placeHolderProgressBar(v.context))
}

@BindingAdapter("android:downloadUrlPost")
fun downloadImagePost(v: ImageView, url: String?) {
    val allUrl = "http://192.168.1.63:8000/storage/posts/$url"
    Log.i("Download Image", "downloadImage: DownloadImg : $allUrl")
    v.downloadFromUrl(allUrl, placeHolderProgressBar(v.context))
}