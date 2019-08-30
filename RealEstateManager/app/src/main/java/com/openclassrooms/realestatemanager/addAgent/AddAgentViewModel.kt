package com.openclassrooms.realestatemanager.addAgent

import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.utils.extensions.isCorrectEmail
import com.openclassrooms.realestatemanager.utils.extensions.isCorrectName
import com.openclassrooms.realestatemanager.utils.extensions.isCorrectPhoneNumber
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-07-25
 */
class AddAgentViewModel (
        private val agentRepository: AgentRepository
) : BaseViewModel<AddAgentViewState>(), REMViewModel<AddAgentIntent, AddAgentResult> {

    private var currentViewState = AddAgentViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private var addAgentsJob: Job? = null

     override fun actionFromIntent(intent: AddAgentIntent) {
        when(intent) {
            is AddAgentIntent.AddAgentToDBIntent -> {
                addAgentToDBRequest(
                        intent.pictureUrl,
                        intent.firstName,
                        intent.lastName,
                        intent.email,
                        intent.phoneNumber)

        }
        }
    }

     override fun resultToViewState(result: Lce<AddAgentResult>) {
        currentViewState = when (result){
            is Lce.Content ->{
                when(result.packet){
                    is AddAgentResult.AddAgentToDBResult -> {
                        currentViewState.copy(
                                isSaved = true,
                                errors = null,
                                isLoading = false)
                    }
                }
            }

            is Lce.Loading ->{
                currentViewState.copy(isLoading = true)

            }

            is Lce.Error ->{
                when(result.packet){
                    is AddAgentResult.AddAgentToDBResult -> {
                        currentViewState.copy(
                                errors = result.packet.errorSource,
                                isLoading = false,
                                isSaved = false)
                    }
                }

            }
        }
    }

    private fun addAgentToDBRequest(urlPicture: String?,
                                            firstName: String,
                                            lastName: String,
                                            email: String,
                                            phoneNumber: String){
        resultToViewState(Lce.Loading())

        if(addAgentsJob?.isActive == true) addAgentsJob?.cancel()

        fun checkErrors(): List<ErrorSourceAddAgent>{
            val listErrorInputs = mutableListOf<ErrorSourceAddAgent>()
            if(!firstName.isCorrectName()) listErrorInputs.add(ErrorSourceAddAgent.FIRST_NAME_INCORRECT)
            if(!lastName.isCorrectName()) listErrorInputs.add(ErrorSourceAddAgent.LAST_NAME_INCORRECT)
            if(!email.isCorrectEmail()) listErrorInputs.add(ErrorSourceAddAgent.EMAIL_INCORRECT)
            if(!phoneNumber.isCorrectPhoneNumber()) listErrorInputs.add(ErrorSourceAddAgent.PHONE_INCORRECT)

            return listErrorInputs
        }

        val listErrors = checkErrors()

        if (listErrors.isEmpty()) {
            addAgentsJob = launch {
                val agent = Agent(null, firstName, lastName, email, phoneNumber, urlPicture)
                agentRepository.createAgent(agent)
                resultToViewState(Lce.Content(AddAgentResult.AddAgentToDBResult(null)))
            }
        }
        else{
            resultToViewState(Lce.Error(AddAgentResult.AddAgentToDBResult(listErrors)))
        }

    }
}
