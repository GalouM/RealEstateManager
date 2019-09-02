package com.openclassrooms.realestatemanager.searchResult

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.listProperties.ActionTypeList
import com.openclassrooms.realestatemanager.listProperties.ListPropertyView
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.showSnackBar

class SearchResultActivity : AppCompatActivity(), REMView<SearchResultViewState> {

    @BindView(R.id.activity_toolbar) lateinit var toolbar: Toolbar

    private var menuToolbar: Menu? = null

    private var listPropertyView: ListPropertyView? = null
    private lateinit var viewModel: SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        ButterKnife.bind(this)
        configureViewModel()
        configureToolbar()
        configureAndShowView()
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.close_icon)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //--------------------
    // CONFIGURE UI
    //--------------------

    //------Toolbar---------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_currency_only, menu)
        menuToolbar = menu
        viewModel.actionFromIntent(SearchResultIntent.GetCurrentCurrencyIntent)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_currency_only_currency -> {
                viewModel.actionFromIntent(SearchResultIntent.ChangeCurrencyIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureAndShowView(){
        listPropertyView = supportFragmentManager.findFragmentById(R.id.activity_frame_layout) as ListPropertyView?
        if(listPropertyView == null){
            listPropertyView = ListPropertyView.newInstance(ActionTypeList.SEARCH_RESULT.actionName)
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.activity_frame_layout, listPropertyView!!)
                    .commit()
        }
    }

    //--------------------
    // VIEWMODEL CONNECTION
    //--------------------

    override fun configureViewModel() {
        val viewModelFactory = Injection.providesViewModelFactory(this)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SearchResultViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    override fun render(state: SearchResultViewState?) {
        if(state == null) return
        renderChangeCurrency(state.currency)
    }

    private fun renderChangeCurrency(currency: Currency){
        menuToolbar?.let {
            when(currency){
                Currency.EURO -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_currency_only_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.euro_icon)
                }
                Currency.DOLLAR -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_currency_only_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                }
            }
        }

    }

}
