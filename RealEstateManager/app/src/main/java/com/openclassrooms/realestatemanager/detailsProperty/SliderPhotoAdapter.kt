package com.openclassrooms.realestatemanager.detailsProperty

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.smarteist.autoimageslider.SliderViewAdapter

/**
 * Created by galou on 2019-09-07
 */
class SliderPhotoAdapter(val context: Context, val picture: List<Picture>, val glide: RequestManager)
    : SliderViewAdapter<SliderPhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?): SliderPhotoViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.picture_slider_layout_item, null)
        return SliderPhotoViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderPhotoViewHolder?, position: Int) {
        viewHolder?.updateWithPicture(picture[position], glide)
    }

    override fun getCount(): Int {
        return picture.size
    }
}