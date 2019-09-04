package com.openclassrooms.realestatemanager.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by galou on 2019-09-04
 */

fun generateName(): String{
    val timeStamp: String = SimpleDateFormat(DATE_FORMAT_FOR_NAME).format(Date())
    return timeStamp.toString()
}