package com.openclassrooms.realestatemanager.addProperty

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
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
import com.openclassrooms.realestatemanager.data.entity.Picture
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
    lateinit var picture: Picture

    init {
        ButterKnife.bind(this, view)
    }

    fun updateWithPicture(picture: Picture, glide: RequestManager, callback: ListPictureAdapter.Listener){
        this.picture = picture
        callbackWeakRef = WeakReference(callback)
        val pictureToDisplay = picture.thumbnailUrl ?: picture.url
        glide.load(pictureToDisplay).apply(RequestOptions.centerCropTransform()).into(pictureImage)
        description.setText(picture.description)

        updateForeground()
        setDragButtonListener()

        description.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                fetchDescription()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
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
                callback?.let { callback.onDragItemRV(this) }
            }
            return@setOnTouchListener true
        }
    }

    @OnClick(R.id.pictures_added_rv_delete)
    fun onClickDeleteButton(){
        val callback = callbackWeakRef.get()
        callback?.let{ callback.onClickDeleteButton(picture)}
    }
    
    fun showError(message: String){
        if(description.text.isNullOrBlank()){
            inputLayout.error = message
        } else{
            inputLayout.isErrorEnabled = false
        }
    }

    private fun fetchDescription(){
        val callback = callbackWeakRef.get()
        callback?.let{ callback.onPictureDescriptionEntered(this.adapterPosition, description.text.toString())}
    }


}