package com.openclassrooms.realestatemanager.mainActivity

import android.content.Context
import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-07-04
 */

data class MainActivityViewState(
        val isOpenAddProperty:Boolean = false,
        val errorSource: ErrorSourceMainActivity? = null,
        val isLoading: Boolean = false,
        val currency: Currency = Currency.EURO,
        val propertyAdded: Int? = null
) : REMViewState

sealed class MainActivityResult : REMResult{
    object OpenAddPropertyResult: MainActivityResult()
    data class ChangeCurrencyResult(val currency: Currency) : MainActivityResult()
    data class UpdateDataFromNetwork(val errorSource: ErrorSourceMainActivity?, val numberPropertyAdded: Int?) : MainActivityResult()
}

sealed class MainActivityIntent : REMIntent{
    object OpenAddPropertyActivityIntent : MainActivityIntent()
    object ChangeCurrencyIntent : MainActivityIntent()
    object GetCurrentCurrencyIntent : MainActivityIntent()
    data class UpdatePropertyFromNetwork(val context: Context) : MainActivityIntent()
}

