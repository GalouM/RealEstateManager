package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-08-11
 */
object CurrencyRepository {

    private val currencyLD = MutableLiveData<Currency>()
    val currency: LiveData<Currency> = currencyLD


    private var currentCurrency = Currency.EURO

    fun getCurrentCurrency() = currentCurrency

    fun setCurrency(){
        currencyLD.value = when(currentCurrency){
            Currency.EURO -> {Currency.DOLLAR}
            Currency.DOLLAR -> Currency.EURO
        }
        currentCurrency = when(currentCurrency){
            Currency.EURO -> {Currency.DOLLAR}
            Currency.DOLLAR -> Currency.EURO
        }
    }

}