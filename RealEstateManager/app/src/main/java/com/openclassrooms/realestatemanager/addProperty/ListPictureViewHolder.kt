package com.openclassrooms.realestatemanager.addProperty

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
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
import com.openclassrooms.realestatemanager.data.PhotoForDisplay
import java.lang.ref.WeakReference

/**
 * Created by galou on 2019-09-04
 */
class ListPictureViewHolder(view: View) : RecyclerView.ViewHolder(view){

    @BindView(R.id.pictures_added_rv_picture) lateinit var pictureImage: ImageView
    @BindView(R.id.pictures_added_rv_input_layout) lateinit var inputLayout: TextInputLayout
    @BindView(R.id.pictures_added_rv_description) lateinit var description: EditText
    @BindView(R.id.pictures_added_rv_delete_button) lateinit var deleteButton: ImageButton
    @BindView(R.id.pictures_added_rv_drag) lateinit var dragButton: ImageButton
    @BindView(R.id.pictures_added_rv_foreground) lateinit var foreground: ImageView

    private lateinit var callbackWeakRef: WeakReference<ListPictureAdapter.Listener>
    private lateinit var photo: PhotoForDisplay

    init {
        ButterKnife.bind(this, view)
    }

    fun updateWithPicture(photo: PhotoForDisplay, glide: RequestManager, callback: ListPictureAdapter.Listener){
        this.photo = photo
        callbackWeakRef = WeakReference(callback)
        val pictureToDisplay = photo.uriThumbnail ?: photo.uriPicture
        glide.load(pictureToDisplay).apply(RequestOptions.centerCropTransform()).into(pictureImage)
        photo.description?.let {
            description.setText(it)
        }
        updateForeground()
        setDragButtonListener()
    }

    fun updateForeground(){
        if(adapterPosition == 0){
            foreground.visibility = View.VISIBLE
        } else {
            foreground.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setDragButtonListener() {
        dragButton.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                val callback = callbackWeakRef.get()
                callback?.let { callback.onDrag(this) }
            }
            return@setOnTouchListener true
        }
    }

    @OnClick(R.id.pictures_added_rv_delete)
    fun onClickDeleteButton(){
        val callback = callbackWeakRef.get()
        callback?.let{ callback.onClickDeleteButton(photo)}
    }
    
    fun createPhotoWithDescription(){
        photo.description = description.text.toString()
    }
    
    fun showError(message: String){
        if(description.text.isNullOrBlank()){
            inputLayout.error = message
        } else{
            inputLayout.isErrorEnabled = false
        }
    }


}