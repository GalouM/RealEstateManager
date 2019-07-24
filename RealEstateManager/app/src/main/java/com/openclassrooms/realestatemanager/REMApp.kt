package com.openclassrooms.realestatemanager

import android.app.Application

/**
 * Created by galou on 2019-07-23
 */

class REMApp : Application(){
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        //appComponent = AppComponent
    }

}