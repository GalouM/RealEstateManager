package com.openclassrooms.realestatemanager.extensions

import java.util.*

/**
 * Created by galou on 2019-07-27
 */

fun Date.isCorrectOnMarketDate(): Boolean{
    val todayDate = Calendar.getInstance().time
    if(this.after(todayDate)) return false

    return true

}

fun Date.isCorrectSoldDate(onMarketDate: Date): Boolean{
    val todayDate = Calendar.getInstance().time
    return !onMarketDate.after(this) && !this.after(todayDate)
}