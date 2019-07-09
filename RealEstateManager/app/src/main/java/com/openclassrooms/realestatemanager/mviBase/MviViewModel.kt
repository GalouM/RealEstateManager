package com.openclassrooms.realestatemanager.mviBase

import io.reactivex.Observable

/**
 * Created by galou on 2019-07-04
 */

interface MviViewModel < I : MviIntent, S : MviViewState>{
    fun processIntents(intent: Observable<I>)
    fun states(): Observable<S>
}