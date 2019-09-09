package com.openclassrooms.realestatemanager.addProperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Picture

/**
 * Created by galou on 2019-09-04
 */
class ListPictureAdapter(
        var pictures: List<Picture>, private val glide: RequestManager, private val callback: Listener,
        val fragment: AddPropertyView
) : RecyclerView.Adapter<ListPictureViewHolder>() {

    private val listViewHolder = mutableListOf<ListPictureViewHolder>()

    interface Listener{
        fun onClickDeleteButton(picture: Picture)
        fun onDragItemRV(viewHolder: ListPictureViewHolder)
        fun onPictureDescriptionEntered(position: Int, description: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPictureViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_pictures_added_item, parent, false)
        val viewHolder = ListPictureViewHolder(view)
        listViewHolder.add(viewHolder)

        return viewHolder
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: ListPictureViewHolder, position: Int) {
        holder.updateWithPicture(pictures[position], glide, callback)
    }

    fun update(pictures: List<Picture>){
        this.pictures = pictures.toMutableList()
        notifyDataSetChanged()
    }

    fun updateForegroundViewHolder(){
        listViewHolder.forEach { it.updateForeground() }
    }

    fun showErrorViewHolder(message: String){
        listViewHolder.forEach {
            it.showError(message)
        }
    }
}