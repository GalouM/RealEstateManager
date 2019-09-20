package com.openclassrooms.realestatemanager.baseCurrency

import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.BaseFragmentREM
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.displayData
import java.lang.ref.WeakReference

/**
 * Created by galou on 2019-09-02
 */


abstract class BaseCurrencyActivity<F : BaseFragmentREM> : AppCompatActivity(), REMView<BaseCurrencyViewState>,
        BaseFragmentREM.OnLoading {



    @BindView(R.id.activity_toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.activity_progressbar) lateinit var progressBar: ProgressBar

    protected var menuToolbar: Menu? = null
    protected var view: F? = null
    protected lateinit var viewModel: BaseCurrencyViewModel

    abstract fun createNewView(): F

    protected fun configureToolbar(homeIcon: Int?) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        homeIcon?.let{actionBar?.setHomeAsUpIndicator(it)}

        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected fun configureAndShowView(){
        view = supportFragmentManager.findFragmentById(R.id.activity_frame_layout) as F?
        if(view == null){
            view = createNewView()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.activity_frame_layout, view!!)
                    .commit()
        }
        setupCallbackToView()
    }

    protected fun setAndBindLayout(){
        setContentView(R.layout.activity_base)
        ButterKnife.bind(this)
    }

    private fun setupCallbackToView(){
        if(view is BaseFragmentREM){
            (view as BaseFragmentREM).callbackLoading = WeakReference(this)
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
        ).get(BaseCurrencyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    override fun render(state: BaseCurrencyViewState?) {
        if(state == null) return
        renderChangeCurrency(state.currency)
    }

    private fun renderChangeCurrency(currency: Currency){
        menuToolbar?.let {
            when(currency){
                Currency.EURO -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_toolbar_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.euro_icon)
                }
                Currency.DOLLAR -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_toolbar_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                }
            }
        }

    }

    override fun displayLoading(loading: Boolean) {
        progressBar.visibility = if(loading) View.VISIBLE else View.GONE
    }
}