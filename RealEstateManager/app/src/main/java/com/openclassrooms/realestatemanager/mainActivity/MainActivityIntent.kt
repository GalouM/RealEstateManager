package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.mviBase.MviIntent
import io.reactivex.Observable

/**
 * Created by galou on 2019-07-04
 */

interface MainActivityIntent : MviIntent {
    object InitialIntent : MainActivityIntent
    object OpenAddPropertyActivityIntent : MainActivityIntent
}