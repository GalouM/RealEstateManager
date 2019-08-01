package com.openclassrooms.realestatemanager.extensions

import com.openclassrooms.realestatemanager.addProperty.Currency
import kotlin.math.roundToLong

/**
 * Created by galou on 2019-07-27
 */

fun Double.toEuro(currency: Currency): Double{
    if(currency == Currency.EURO) return this

    return (this * 0.812).roundToLong().toDouble()
}

fun Double.toSqMeter(currency: Currency): Double{
    if(currency == Currency.EURO) return this

    return (this * 0.092903).roundToLong().toDouble()

}