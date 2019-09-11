package com.openclassrooms.realestatemanager.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.KEY_PREF
import com.openclassrooms.realestatemanager.utils.KEY_PREF_CURRENCY

/**
 * Created by galou on 2019-08-11
 */
class CurrencyRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)

    private val currencyLD = MutableLiveData<Currency>()
    val currency: LiveData<Currency>
        get(){
            if(currencyLD.value == null) currencyLD.value = getCurrencyFromPrefs()
            return currencyLD
        }

    fun setCurrency(){
        currencyLD.value = when(currencyLD.value){
            Currency.EURO -> Currency.DOLLAR
            Currency.DOLLAR -> Currency.EURO
            else -> Currency.EURO
        }
        sharedPreferences?.let {
            val editor = sharedPreferences.edit()
            editor.putString(KEY_PREF_CURRENCY, currency.value?.nameCurrency)
            editor.apply()
        }
    }

    private fun getCurrencyFromPrefs(): Currency{
        sharedPreferences?.getString(KEY_PREF_CURRENCY, Currency.EURO.nameCurrency)?.let{
            return Currency.valueOf(it)
        }
        return Currency.EURO
    }

    companion object{
        @Volatile
        private var INSTANCE: CurrencyRepository? = null
        fun getCurrencyRepository(context: Context): CurrencyRepository {
            return INSTANCE
                    ?: synchronized(this){
                        val instance = CurrencyRepository(context)
                        INSTANCE = instance
                        return instance
                    }
        }
    }

}