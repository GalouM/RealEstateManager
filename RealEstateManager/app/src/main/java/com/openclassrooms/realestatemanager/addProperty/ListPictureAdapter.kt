package com.openclassrooms.realestatemanager.addProperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R

/**
 * Created by galou on 2019-09-04
 */
class ListPictureAdapter(
        var urlPictures: List<String>, private val glide: RequestManager, private val callback: Listener
) : RecyclerView.Adapter<ListPictureViewHolder>() {

    interface Listener{
        fun onClickDeleteButton(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPictureViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_pictures_added_item, parent, false)

        return ListPictureViewHolder(view)
    }

    override fun getItemCount(): Int {
        return urlPictures.size
    }

    override fun onBindViewHolder(holder: ListPictureViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}