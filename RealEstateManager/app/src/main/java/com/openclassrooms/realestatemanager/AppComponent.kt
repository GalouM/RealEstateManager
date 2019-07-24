package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.mainActivity.MainActivity
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by galou on 2019-07-24
 */
@Module
class AppModule(private val app: REMApp){

    //@Provides
    //@Singleton
    //fun providesAgenDao(): AgentDao = AgentDao
}

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}