package com.openclassrooms.realestatemanager.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R

/**
 * Created by galou on 2019-09-24
 */

fun ImageView.loadImage(imageUrl: String, fallbackImage: String?, glide: RequestManager){
    glide.load(imageUrl).apply(RequestOptions.centerCropTransform())
            .error(glide.load(fallbackImage)
                    .error(glide.load(R.drawable.refresh_icon)))
            .into(this)
}