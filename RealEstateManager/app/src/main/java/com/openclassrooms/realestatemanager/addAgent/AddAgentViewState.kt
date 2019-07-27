package com.openclassrooms.realestatemanager.addAgent

/**
 * Created by galou on 2019-07-25
 */
data class AddAgentViewState(
        val isLoading: Boolean = false,
        val isSaved: Boolean = false,
        val errors: List<ErrorSourceAddAgent>? = null
)

sealed class AddAgentIntent{
    data class AddAgentToDBIntent(val pictureUrl: String?,
                                  val firstName: String,
                                  val lastName: String,
                                  val email: String,
                                  val phoneNumber: String) : AddAgentIntent()
}

sealed class AddAgentResult{
    data class AddAgentToDBResult(val errorSource: List<ErrorSourceAddAgent>?) : AddAgentResult()
}