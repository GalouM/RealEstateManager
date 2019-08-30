package com.openclassrooms.realestatemanager.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.utils.extensions.config

/**
 * Created by galou on 2019-08-02
 */

fun showSnackBar(view: View, message: String){
    val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackBar.config(view.context)
    snackBar.show()
}