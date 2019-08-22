package com.openclassrooms.realestatemanager.mviBase

/**
 * Created by galou on 2019-08-20
 */
interface REMViewModel<I : REMIntent, R : REMResult> {
    fun actionFromIntent(intent: I)
    fun resultToViewState(result: Lce<R>)
}