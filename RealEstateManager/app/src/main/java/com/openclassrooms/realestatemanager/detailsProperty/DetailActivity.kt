package com.openclassrooms.realestatemanager.detailsProperty

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyActivity
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyIntent

class DetailActivity : BaseCurrencyActivity<DetailsPropertyView>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        ButterKnife.bind(this)
        configureViewModel()
        configureToolbar(null)
        configureAndShowView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_detail_property, menu)
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
            R.id.menu_details_property_modify -> {
                view!!.toolBarModifyClickListener()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun configureAndShowView(){
        view = supportFragmentManager.findFragmentById(R.id.activity_frame_layout) as DetailsPropertyView?
        if(view == null){
            view = DetailsPropertyView()
            addFragmentToManager()
        }
    }
}
