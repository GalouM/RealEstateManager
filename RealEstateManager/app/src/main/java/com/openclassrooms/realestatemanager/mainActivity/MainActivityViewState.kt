package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-07-04
 */

data class MainActivityViewState(
        val isOpenAddProperty:Boolean = false,
        val errorSource: ErrorSourceMainActivity? = null,
        val isLoading: Boolean = false,
        val currency: Currency = Currency.EURO
)

sealed class MainActivityResult{
    object OpenAddPropertyResult: MainActivityResult()
    data class ChangeCurrencyResult(val currency: Currency) : MainActivityResult()
}

sealed class MainActivityIntent{
    object OpenAddPropertyActivityIntent : MainActivityIntent()
    object ChangeCurrencyIntent : MainActivityIntent()
    object GetCurrentCurrencyIntent : MainActivityIntent()
}

