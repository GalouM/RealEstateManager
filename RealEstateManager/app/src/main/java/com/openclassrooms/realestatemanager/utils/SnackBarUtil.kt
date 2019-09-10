package com.openclassrooms.realestatemanager.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.extensions.config

/**
 * Created by galou on 2019-08-02
 */

fun showSnackBar(view: View, message: String){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        config(view.context)
        show()
    }
}

fun showSnackBarWithButonview(view: View, message: String, listener: SnackBarListener, action: SnackBarAction){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        config(view.context)
        setAction(action.actionName) {listener.onSnackBarButtonClick(action)}
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