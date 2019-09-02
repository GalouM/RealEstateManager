package com.openclassrooms.realestatemanager.baseCurrency

import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-09-02
 */

data class BaseCurrencyViewState(
        val currency: Currency = Currency.EURO
) : REMViewState

sealed class BaseCurrencyResult : REMResult {
    data class ChangeCurrencyResult(val currency: Currency) : BaseCurrencyResult()
}

sealed class BaseCurrencyIntent : REMIntent {
    object ChangeCurrencyIntent : BaseCurrencyIntent()
    object GetCurrentCurrencyIntent : BaseCurrencyIntent()
}