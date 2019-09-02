package com.openclassrooms.realestatemanager.searchProperty

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyActivity
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyIntent

class SearchActivity : BaseCurrencyActivity<SearchPropertyView>(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindLayout()
        configureViewModel()
        configureToolbar(null)
        configureAndShowView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_validate_button, menu)
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
            R.id.menu_validate_activity_check -> {
                view!!.toolBarValidateClickListener()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun createNewView(): SearchPropertyView {
        return SearchPropertyView()
    }
}
