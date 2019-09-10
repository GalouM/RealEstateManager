package com.openclassrooms.realestatemanager.listProperties

import android.content.Intent
import android.util.Log
import androidx.appcompat.widget.ContentFrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import com.openclassrooms.realestatemanager.detailsProperty.DetailActivity
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mainActivity.MainActivity
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.ACTION_TYPE_LIST_PROPERTY
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.showSnackBar

/**
 * Created by galou on 2019-08-21
 */
abstract class BaseViewListProperties : Fragment(), REMView<PropertyListViewState> {

    protected lateinit var viewModel: ListPropertyViewModel
    protected var currentCurrency: Currency? = null

    protected abstract fun renderListProperties(properties: List<PropertyWithAllData>)
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

    protected fun configureActionType(){
        val argument = arguments?.getString(ACTION_TYPE_LIST_PROPERTY, ActionTypeList.ALL_PROPERTIES.actionName)
        argument?.let{
            val actionType = ActionTypeList.valueOf(it)
            viewModel.actionFromIntent(PropertyListIntent.SetActionTypeIntent(actionType))
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    override fun render(state: PropertyListViewState?) {
        if (state == null) return
        if(state.openDetails) {
            renderOpenPropertyDetails()
            return
        }

        if(state.listProperties != null && currentCurrency != null && !state.isLoading){
            renderListProperties(state.listProperties)
        }

        state.errorSource?.let { renderErrorFetchingProperty(it) }

        renderIsLoading(state.isLoading)

    }

    private fun renderOpenPropertyDetails(){
        if(activity is MainActivity){
            (activity as MainActivity).openDetailsProperty()
        } else {
            val intent = Intent(activity!!, DetailActivity::class.java)
            startActivity(intent)
        }
    }

    protected fun currencyObserver(){
        viewModel.currency.observe(this, Observer {currency ->
            currentCurrency = currency
            renderChangeCurrency(currentCurrency!!)
        })
    }

    protected fun setPropertyPicked(property: PropertyWithAllData){
        viewModel.actionFromIntent(PropertyListIntent.OpenPropertyDetailIntent(property))

    }

    protected fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<CoordinatorLayout>(R.id.base_activity_main_layout)
        showSnackBar(viewLayout, message)

    }
}