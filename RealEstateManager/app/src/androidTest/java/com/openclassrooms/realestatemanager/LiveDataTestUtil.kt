package com.openclassrooms.realestatemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by galou on 2019-07-04
 */

@Throws(InterruptedException::class)
fun <T> LiveData<T>.waitForValue(): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object : Observer<T>{
        override fun onChanged(t: T) {
            data[0] = t
            latch.countDown()
            this@waitForValue.removeObserver(this)
        }
    }

    this.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)

    return data[0] as T
}