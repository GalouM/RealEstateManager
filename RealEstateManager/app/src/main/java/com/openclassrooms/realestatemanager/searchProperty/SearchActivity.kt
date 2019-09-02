package com.openclassrooms.realestatemanager.searchProperty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Currency

class SearchActivity : AppCompatActivity(), SearchPropertyView.OnCurrencyChangedListener {

    @BindView(R.id.activity_toolbar) lateinit var toolbar: Toolbar

    private var searchPropertyView: SearchPropertyView? = null

    private var menuToolbar: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        ButterKnife.bind(this)
        configureToolbar()
        configureAndShowView()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if(fragment is SearchPropertyView){
            fragment.setOnCurrencyChangedListener(this)
        }
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_validate_button, menu)
        menuToolbar = menu
        searchPropertyView?.configureCurrentCurrency()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) finish()
        searchPropertyView!!.toolBarClickListener(item?.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun configureAndShowView(){
        searchPropertyView = supportFragmentManager.findFragmentById(R.id.activity_frame_layout) as SearchPropertyView?
        if(searchPropertyView == null){
            searchPropertyView = SearchPropertyView()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.activity_frame_layout, searchPropertyView!!)
                    .commit()
        }
    }

    override fun onClickCurrency(currency: Currency) {
        menuToolbar?.let{
            when(currency){
                Currency.EURO -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_validate_activity_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.euro_icon)
                }
                Currency.DOLLAR -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_validate_activity_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                }
            }
        }

    }
}
