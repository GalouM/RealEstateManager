package com.openclassrooms.realestatemanager.utils.extensions

import android.widget.EditText

/**
 * Created by galou on 2019-08-29
 */

fun EditText.toDouble(): Double{
    return this.text.toString().toDouble()
}

fun EditText.toInt(): Int{
    return this.text.toString().toInt()
}

fun EditText.toString(): String{
    return this.text.toString()
}