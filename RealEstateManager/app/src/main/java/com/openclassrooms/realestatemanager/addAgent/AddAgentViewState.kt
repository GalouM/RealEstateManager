package com.openclassrooms.realestatemanager.addAgent

import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState

/**
 * Created by galou on 2019-07-25
 */
data class AddAgentViewState(
        val isLoading: Boolean = false,
        val isSaved: Boolean = false,
        val errors: List<ErrorSourceAddAgent>? = null
) : REMViewState

sealed class AddAgentIntent : REMIntent{
    data class AddAgentToDBIntent(val pictureUrl: String?,
                                  val urlFromDevice: String?,
                                  val firstName: String,
                                  val lastName: String,
                                  val email: String,
                                  val phoneNumber: String) : AddAgentIntent()
}

sealed class AddAgentResult : REMResult{
    data class AddAgentToDBResult(val errorSource: List<ErrorSourceAddAgent>?) : AddAgentResult()
}