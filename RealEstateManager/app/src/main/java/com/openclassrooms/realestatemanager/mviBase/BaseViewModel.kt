package com.openclassrooms.realestatemanager.mviBase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by galou on 2019-07-25
 */
abstract class BaseViewModel<S : REMViewState> : ViewModel(), CoroutineScope {

    private val compositeJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + compositeJob

    protected val viewStateLD = MutableLiveData<S>()
    val viewState: LiveData<S>
        get() = viewStateLD

    override fun onCleared() {
        compositeJob.cancel()
        super.onCleared()
    }
}