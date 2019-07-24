package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.mviBase.MviViewState

/**
 * Created by galou on 2019-07-04
 */

data class MainActivityViewState(
        val isOpenAddProperty:Boolean = true,
        val errorSource: ErrorSource? = null
) : MviViewState
