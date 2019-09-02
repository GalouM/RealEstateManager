package com.openclassrooms.realestatemanager.baseCurrency

import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel

/**
 * Created by galou on 2019-09-02
 */
class BaseCurrencyViewModel(private val currencyRepository: CurrencyRepository) : BaseViewModel<BaseCurrencyViewState>(),
        REMViewModel<BaseCurrencyIntent, BaseCurrencyResult> {

    private var currentViewState = BaseCurrencyViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    override fun actionFromIntent(intent: BaseCurrencyIntent) {
        when(intent){
            is BaseCurrencyIntent.ChangeCurrencyIntent -> changeCurrency()
            is BaseCurrencyIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
        }
    }

    override fun resultToViewState(result: Lce<BaseCurrencyResult>) {
        if(result is Lce.Content && result.packet is BaseCurrencyResult.ChangeCurrencyResult ){
            currentViewState = currentViewState.copy(currency = result.packet.currency)
        }
    }

    private fun changeCurrency(){
        currencyRepository.setCurrency()
        emitCurrentCurrency()

    }

    private fun emitCurrentCurrency(){
        val currency = currencyRepository.currency.value!!
        val result: Lce<BaseCurrencyResult> = Lce.Content(BaseCurrencyResult.ChangeCurrencyResult(currency))

        resultToViewState(result)
    }
}