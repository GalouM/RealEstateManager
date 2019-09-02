package com.openclassrooms.realestatemanager.searchResult

import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel

/**
 * Created by galou on 2019-08-30
 */
class SearchResultViewModel(private val currencyRepository: CurrencyRepository) : BaseViewModel<SearchResultViewState>(),
        REMViewModel<SearchResultIntent, SearchResultResult> {

    private var currentViewState = SearchResultViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    override fun actionFromIntent(intent: SearchResultIntent) {
        when(intent){
            is SearchResultIntent.ChangeCurrencyIntent -> changeCurrency()
            is SearchResultIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
        }
    }

    override fun resultToViewState(result: Lce<SearchResultResult>) {
        if(result is Lce.Content && result.packet is SearchResultResult.ChangeCurrencyResult ){
            currentViewState = currentViewState.copy(currency = result.packet.currency)
        }
    }

    private fun changeCurrency(){
        currencyRepository.setCurrency()
        emitCurrentCurrency()

    }

    private fun emitCurrentCurrency(){
        val currency = currencyRepository.currency.value!!
        val result: Lce<SearchResultResult> = Lce.Content(SearchResultResult.ChangeCurrencyResult(currency))

        resultToViewState(result)
    }
}