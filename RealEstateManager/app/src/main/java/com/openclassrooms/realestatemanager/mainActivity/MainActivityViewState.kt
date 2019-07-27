package com.openclassrooms.realestatemanager.mainActivity

/**
 * Created by galou on 2019-07-04
 */

data class MainActivityViewState(
        val isOpenAddProperty:Boolean = false,
        val errorSource: ErrorSource? = null,
        val isLoading: Boolean = false
)

sealed class MainActivityResult{
    object OpenAddPropertyResult: MainActivityResult()
}

sealed class MainActivityIntent{
    object OpenAddPropertyActivityIntent : MainActivityIntent()
}

