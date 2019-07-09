package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.mviBase.MviViewState

/**
 * Created by galou on 2019-07-04
 */

data class MainActivityViewState(
        val isOpenAddProperty:Boolean,
        val isError: Boolean,
        val errorSource: ErrorSource?
) : MviViewState {

    companion object {
        fun idle(): MainActivityViewState {
            return MainActivityViewState(
                    isOpenAddProperty = false,
                    isError = false,
                    errorSource = null
            )
        }
    }
}
