package com.openclassrooms.realestatemanager.mainActivity

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.mviBase.MviViewModel
import com.openclassrooms.realestatemanager.utils.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * Created by galou on 2019-07-04
 */
class MainActivityViewModel(
        private val mainActivityActionProcessHolder: MainActivityActionProcessHolder
) : ViewModel(), MviViewModel<MainActivityIntent, MainActivityViewState>{

    private val intentsSubject: PublishSubject<MainActivityIntent> = PublishSubject.create()
    private val statesObservable: Observable<MainActivityViewState> = compose()
    private val disposables = CompositeDisposable()

    private val intentFilter: ObservableTransformer<MainActivityIntent, MainActivityIntent>
    get() = ObservableTransformer { intents ->
        intents.publish { shared ->
            Observable.merge<MainActivityIntent>(
                    shared.ofType(MainActivityIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(MainActivityIntent.InitialIntent::class.java)
            )
        }
    }

    override fun processIntents(intent: Observable<MainActivityIntent>) {
        disposables.add(intent.subscribe(intentsSubject::onNext))
    }

    override fun states(): Observable<MainActivityViewState> = statesObservable

    private fun compose(): Observable<MainActivityViewState> {
        return intentsSubject
                .compose<MainActivityIntent>(intentFilter)
                .map<MainActivityAction>(this::actionFromIntent)
                .compose(mainActivityActionProcessHolder.actionProcessor)
                .scan(MainActivityViewState.idle(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)

    }

    private fun actionFromIntent(intent: MainActivityIntent): MainActivityAction{
        return when(intent){
            is MainActivityIntent.OpenAddPropertyActivityIntent -> MainActivityAction.OpenAddPropertyAction
            else -> throw Exception("No Intent found")
        }

    }

    override fun onCleared() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    companion object {

        private val reducer = BiFunction { previousState: MainActivityViewState, result: MainActivityResult ->
            when (result) {
                is MainActivityResult.OpenAddPropertyResult -> when (result) {
                    is MainActivityResult.OpenAddPropertyResult.Success -> previousState.copy(
                            isOpenAddProperty = true,
                            isError = false,
                            errorSource = null)
                    is MainActivityResult.OpenAddPropertyResult.Failure -> previousState.copy(isError = true)
                    is MainActivityResult.OpenAddPropertyResult.InFlight -> previousState
                }
            }
        }
    }
}