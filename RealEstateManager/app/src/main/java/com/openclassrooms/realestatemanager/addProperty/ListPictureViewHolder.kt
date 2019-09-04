package com.openclassrooms.realestatemanager.addProperty

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import kotlinx.android.synthetic.main.list_pictures_added_item.view.*
import java.lang.ref.WeakReference

/**
 * Created by galou on 2019-09-04
 */
class ListPictureViewHolder(view: View) : RecyclerView.ViewHolder(view){

    @BindView(R.id.pictures_added_rv_picture) lateinit var pictureImage: ImageView
    @BindView(R.id.pictures_added_rv_input_layout) lateinit var inputLayout: TextInputLayout
    @BindView(R.id.pictures_added_rv_description) lateinit var description: EditText
    @BindView(R.id.pictures_added_rv_delete) lateinit var deleteButton: ImageButton
    @BindView(R.id.pictures_added_rv_drag) lateinit var dragButton: ImageButton

    private lateinit var callbackWeakRef: WeakReference<ListPictureAdapter.Listener>

    init {
        ButterKnife.bind(this, view)
    }

    fun updateWithPicture(urlPicture: String, glide: RequestManager, callback: ListPictureAdapter.Listener){
        callbackWeakRef = WeakReference(callback)
        glide.load(urlPicture).apply(RequestOptions.centerCropTransform()).into(pictureImage)
    }

    @OnClick(R.id.pictures_added_rv_delete)
    fun onClickDeleteButton(){
        val callback = callbackWeakRef.get()
        callback?.let{ callback.onClickDeleteButton(adapterPosition)}
    }
}