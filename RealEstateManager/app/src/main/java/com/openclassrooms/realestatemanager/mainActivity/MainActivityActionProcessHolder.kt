package com.openclassrooms.realestatemanager.mainActivity

import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.mainActivity.MainActivityResult.OpenAddPropertyResult.Success
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by galou on 2019-07-05
 */
class MainActivityActionProcessHolder @Inject constructor(private val agentRepository: AgentRepository){

    private val openAddPropertyProcessor =
            ObservableTransformer<MainActivityAction.OpenAddPropertyAction, MainActivityResult.OpenAddPropertyResult>
            { actions -> actions.flatMap { action ->
            agentRepository.getAllAgents()
                    .map { agents -> Success(agents) }
                    .cast(MainActivityResult.OpenAddPropertyResult::class.java)
                    .onErrorReturn(MainActivityResult.OpenAddPropertyResult::Failure)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWith(MainActivityResult.OpenAddPropertyResult.InFlight)

        }
    }


    internal var actionProcessor = ObservableTransformer<MainActivityAction, MainActivityResult> { actions ->
        actions.publish{shared ->
            shared.ofType(MainActivityAction.OpenAddPropertyAction::class.java).compose(openAddPropertyProcessor)
        }
    }
}