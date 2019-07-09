package com.openclassrooms.realestatemanager.utils

import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport

/**
 * Created by galou on 2019-07-05
 */

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any, U : Any> Observable<T>.notOfType(clazz: Class<U>): Observable<T> {
    return filter{!clazz.isInstance(it)}
}