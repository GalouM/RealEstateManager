package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.mviBase.MviAction

/**
 * Created by galou on 2019-07-05
 */
sealed class MainActivityAction : MviAction{
    object OpenAddPropertyAction : MainActivityAction()
}