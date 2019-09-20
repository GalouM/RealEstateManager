package com.openclassrooms.realestatemanager.mviBase

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * Created by galou on 2019-09-20
 */
abstract class BaseFragmentREM : Fragment(){

    interface OnLoading{
        fun displayLoading(loading: Boolean)
    }

    var callbackLoading: WeakReference<OnLoading>? = null
    protected var loading: Boolean = false

    protected fun renderLoading(loading: Boolean){
        callbackLoading?.get()?.displayLoading(loading)
        this.loading = loading
    }
}