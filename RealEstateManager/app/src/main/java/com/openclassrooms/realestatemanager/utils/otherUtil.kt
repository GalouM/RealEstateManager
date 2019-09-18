package com.openclassrooms.realestatemanager.utils

import android.util.Log
import java.util.*

/**
 * Created by galou on 2019-09-10
 */

fun displayData(message: String){
    Log.e("REMData", message)
}

var idGenerated: String = ""
    get() {
        field = UUID.randomUUID().toString()
        return field
    }

val todaysDate: Date = Calendar.getInstance(Locale.CANADA).time