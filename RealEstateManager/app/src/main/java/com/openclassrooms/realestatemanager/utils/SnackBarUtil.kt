package com.openclassrooms.realestatemanager.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.extensions.config

/**
 * Created by galou on 2019-08-02
 */

fun showSnackBar(view: View, message: String){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        config(view.context)
        getView().findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
        show()
    }
}

fun showSnackBarWithAction(view: View, message: String, listener: SnackBarListener, action: SnackBarAction){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        config(view.context)
        setAction(action.actionName) {listener.onSnackBarButtonClick(action)}
        setActionTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
        getView().findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
        show()
    }
}

interface SnackBarListener{
    fun onSnackBarButtonClick(action: SnackBarAction)
}

enum class SnackBarAction(val actionName: Int){
    SHOW_ORIGINAL(R.string.show_original),
    SAVE_DRAFT(R.string.save_draft)
}