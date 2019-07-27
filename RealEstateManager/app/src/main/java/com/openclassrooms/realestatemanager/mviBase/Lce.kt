package com.openclassrooms.realestatemanager.mviBase

/**
 * Created by galou on 2019-07-25
 */
sealed class Lce<T> {
    class Loading<T>: Lce<T>()
    data class Content<T>(val packet: T): Lce<T>()
    data class Error<T>(val packet: T): Lce<T>()
}