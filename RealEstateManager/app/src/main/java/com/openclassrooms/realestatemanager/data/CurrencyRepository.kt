package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-08-11
 */
object CurrencyRepository {

    private var currentCurrency = Currency.EURO

    fun getCurrentCurrency() = currentCurrency

    fun setCurrency(){
        currentCurrency = when(currentCurrency){
            Currency.EURO -> Currency.DOLLAR
            Currency.DOLLAR -> Currency.EURO
        }
    }

}