package com.openclassrooms.realestatemanager.mviBase

/**
 * Created by galou on 2019-08-20
 */
interface REMView<S : REMViewState> {
    fun configureViewModel()
    fun render(state: S?)
}