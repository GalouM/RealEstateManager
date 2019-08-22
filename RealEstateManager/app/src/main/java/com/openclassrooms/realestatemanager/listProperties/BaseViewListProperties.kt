package com.openclassrooms.realestatemanager.listProperties

import androidx.appcompat.widget.ContentFrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.showSnackBar

/**
 * Created by galou on 2019-08-21
 */
abstract class BaseViewListProperties : Fragment(), REMView<PropertyListViewState> {

    protected lateinit var viewModel: ListPropertyViewModel

    protected abstract fun renderListProperties(properties: List<PropertyForListDisplay>)
    protected abstract fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty)
    protected abstract fun renderIsLoading()

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

        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    override fun render(state: PropertyListViewState?) {
        if (state == null) return

        state.listProperties?.let {
            renderListProperties(state.listProperties)
        }

        state.errorSource?.let { renderErrorFetchingProperty(it) }

        if(state.isLoading) renderIsLoading()

    }

    protected fun openPropertyDetails(propertyId: Int){

    }

    protected fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}