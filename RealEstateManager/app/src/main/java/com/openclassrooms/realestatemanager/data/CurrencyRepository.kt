package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-08-11
 */
object CurrencyRepository {

    private val currencyLD = MutableLiveData<Currency>()
    val currency: LiveData<Currency>
        get(){
            if(currencyLD.value == null) currencyLD.value = Currency.EURO
            return currencyLD
        }

    fun setCurrency(){
        currencyLD.value = when(currencyLD.value){
            Currency.EURO -> Currency.DOLLAR
            Currency.DOLLAR -> Currency.EURO
            else -> Currency.EURO
        }
    }

}