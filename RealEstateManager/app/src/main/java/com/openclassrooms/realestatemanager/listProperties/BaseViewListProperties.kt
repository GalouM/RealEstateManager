package com.openclassrooms.realestatemanager.listProperties

import android.content.Intent
import androidx.appcompat.widget.ContentFrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.detailsProperty.DetailActivity
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.showSnackBar

/**
 * Created by galou on 2019-08-21
 */
abstract class BaseViewListProperties : Fragment(), REMView<PropertyListViewState> {

    protected lateinit var viewModel: ListPropertyViewModel
    protected lateinit var currentCurrency: Currency

    protected abstract fun renderListProperties(properties: List<PropertyForListDisplay>)
    protected abstract fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty)
    protected abstract fun renderIsLoading(loading: Boolean)
    protected abstract fun renderChangeCurrency(currency: Currency)

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(ListPropertyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })


    }

    override fun onResume() {
        super.onResume()
        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    override fun render(state: PropertyListViewState?) {
        if (state == null) return

        state.listProperties?.let {
            renderListProperties(it)
        }

        state.errorSource?.let { renderErrorFetchingProperty(it) }

        renderIsLoading(state.isLoading)

        if(state.openDetails) renderOpenPropertyDetails()

    }

    private fun renderOpenPropertyDetails(){
        val intent = Intent(activity!!, DetailActivity::class.java)
        startActivity(intent)
    }

    protected fun currencyObserver(){
        viewModel.currency.observe(this, Observer {currency ->
            currentCurrency = currency
            renderChangeCurrency(currentCurrency)
        })
    }

    protected fun setPropertyPicked(propertyId: Int){
        viewModel.actionFromIntent(PropertyListIntent.OpenPropertyDetailIntent(propertyId))

    }

    protected fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}