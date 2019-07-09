package com.openclassrooms.realestatemanager.mviBase

import io.reactivex.Observable

/**
 * Created by galou on 2019-07-04
 */
interface MviView<I : MviIntent, S : MviViewState>{
    fun intents(): Observable<I>
    fun render(state: S)
}