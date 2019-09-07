package com.openclassrooms.realestatemanager.searchResult

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyActivity
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyIntent
import com.openclassrooms.realestatemanager.listProperties.ActionTypeList
import com.openclassrooms.realestatemanager.listProperties.ListPropertyView

class SearchResultActivity : BaseCurrencyActivity<ListPropertyView>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindLayout()
        configureViewModel()
        configureToolbar(R.drawable.close_icon)
        configureAndShowView()
    }

    //--------------------
    // CONFIGURE UI
    //--------------------

    //------Toolbar---------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_currency_only, menu)
        menuToolbar = menu
        viewModel.actionFromIntent(BaseCurrencyIntent.GetCurrentCurrencyIntent)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_toolbar_currency -> {
                viewModel.actionFromIntent(BaseCurrencyIntent.ChangeCurrencyIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createNewView(): ListPropertyView {
        return ListPropertyView.newInstance(ActionTypeList.SEARCH_RESULT.actionName)
    }
}
