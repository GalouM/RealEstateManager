package com.openclassrooms.realestatemanager.extensions

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R

/**
 * Created by galou on 2019-08-02
 */

fun Snackbar.config(context: Context){
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(12,12,12,12)
    this.view.layoutParams = params

    this.view.background = ContextCompat.getDrawable(context, R.drawable.bg_snackbar)

    ViewCompat.setElevation(this.view, 6f)
}