package com.openclassrooms.realestatemanager.mviBase

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by galou on 2019-07-25
 */
abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val compositeJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + compositeJob

    override fun onCleared() {
        compositeJob.cancel()
        super.onCleared()
    }
}