package com.openclassrooms.realestatemanager.searchResult

import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-08-30
 */
data class SearchResultViewState(
        val currency: Currency = Currency.EURO
) : REMViewState

sealed class SearchResultResult : REMResult {
    data class ChangeCurrencyResult(val currency: Currency) : SearchResultResult()
}

sealed class SearchResultIntent : REMIntent {
    object ChangeCurrencyIntent : SearchResultIntent()
    object GetCurrentCurrencyIntent : SearchResultIntent()
}