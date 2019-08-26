package com.openclassrooms.realestatemanager.extensions

import com.openclassrooms.realestatemanager.utils.Currency
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToLong

/**
 * Created by galou on 2019-07-27
 */

fun Double.toEuro(currency: Currency): Double{
    if(currency == Currency.EURO) return this

    return (this * 0.812).roundToLong().toDouble()
}

fun Double.toDollar():Double{
    return (this * 1.137).roundToLong().toDouble()
}

fun Double.toSqMeter(currency: Currency): Double{
    if(currency == Currency.EURO) return this

    return (this * 0.092903).roundToLong().toDouble()
}

fun Double.toSqFt(): Double {
    return (this * 10.7639).roundToLong().toDouble()
}

fun Double.toEuroDisplay(): String{
    return NumberFormat.getNumberInstance(Locale.FRANCE).format(this)
}

fun Double.toDollarDisplay(): String{
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}