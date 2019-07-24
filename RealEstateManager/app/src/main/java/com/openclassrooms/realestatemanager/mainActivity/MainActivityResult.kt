package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.mviBase.MviResult

/**
 * Created by galou on 2019-07-05
 */
sealed class MainActivityResult : MviResult {
    data class OpenAddPropertyResult(val errorSource: ErrorSource?): MainActivityResult()
}