package com.openclassrooms.realestatemanager.detailsProperty


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ContentFrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.addProperty.ActionType
import com.openclassrooms.realestatemanager.addProperty.AddPropertyActivity
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.extensions.toSqFt
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.ACTION_TYPE_ADD_PROPERTY
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.RC_CODE_ADD_PROPERTY
import com.openclassrooms.realestatemanager.utils.showSnackBar

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailsPropertyView : Fragment(), REMView<DetailsPropertyViewState> {

    @BindView(R.id.details_view_description) lateinit var description: TextView
    @BindView(R.id.details_view_surface) lateinit var surface: TextView
    @BindView(R.id.details_view_rooms) lateinit var rooms: TextView
    @BindView(R.id.details_view_bed) lateinit var bedRooms: TextView
    @BindView(R.id.details_view_bath) lateinit var bathRooms: TextView
    @BindView(R.id.details_view_location_city) lateinit var city: TextView
    @BindView(R.id.details_view_location_street) lateinit var street: TextView
    @BindView(R.id.details_view_location_details) lateinit var details: TextView
    @BindView(R.id.details_view_location_zip) lateinit var zipCode: TextView
    @BindView(R.id.details_view_location_country) lateinit var country: TextView
    @BindView(R.id.details_view_map) lateinit var map: ImageView

    private lateinit var viewModel: DetailsPropertyViewModel

    private var surfaceProperty: Double? = null

    private lateinit var currentCurrency: Currency

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details_property_view, container, false)
        ButterKnife.bind(this, view)

        configureViewModel()
        currencyObserver()
        viewModel.actionFromIntent(DetailsPropertyIntent.FetchDetailsIntent)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_CODE_ADD_PROPERTY) onPropertyModified(resultCode)
    }

    private fun onPropertyModified(resultCode: Int){
        when(resultCode){
            RESULT_OK -> {
                showSnackBarMessage(getString(R.string.property_modified))
                viewModel.actionFromIntent(DetailsPropertyIntent.FetchDetailsIntent)
            }
            else -> showSnackBarMessage(getString(R.string.error_modification))
        }

    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarModifyClickListener(){
        viewModel.actionFromIntent(DetailsPropertyIntent.ModifyPropertyIntent)
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(DetailsPropertyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    private fun currencyObserver(){
        viewModel.currency.observe(this, Observer {currency ->
            currentCurrency = currency
            renderChangeCurrency(currentCurrency)
        })
    }

    //--------------------
    // STATE AND INTENT
    //--------------------

    override fun render(state: DetailsPropertyViewState?) {
        if (state == null) return

        state.property?.let {
            renderFetchedProperty(it, state.address!!)
        }

        if(state.modifyProperty) renderModifyProperty()

        renderIsLoading(state.isLoading)
    }

    private fun renderIsLoading(loading: Boolean){

    }

    private fun renderModifyProperty(){
        val intent = Intent(activity, AddPropertyActivity::class.java)
        intent.putExtra(ACTION_TYPE_ADD_PROPERTY, ActionType.MODIFY_PROPERTY.actionName)
        startActivityForResult(intent, RC_CODE_ADD_PROPERTY)
    }

    private fun renderFetchedProperty(property: Property, address: Address){
        surfaceProperty = property.surface
        configureSurfaceUnitDisplay(currentCurrency)
        description.text = property.description
        rooms.text = property.rooms.toString()
        property.bedrooms?.let{
            bedRooms.text = it.toString()
        }
        property.bathrooms?.let{
            bathRooms.text = it.toString()
        }
        street.text = address.street
        city.text = address.city
        zipCode.text = String.format("%s %s", address.state, address.postalCode)
        country.text = address.country
        Glide.with(this).load(address.mapIconUrl).into(map)
    }

    private fun renderChangeCurrency(currency: Currency){
        configureSurfaceUnitDisplay(currency)
    }

    private fun configureSurfaceUnitDisplay(currency: Currency){
        surfaceProperty?.let{
            when(currency){
                Currency.EURO -> surface.text = String.format(getString(R.string.surface_m2_details), surfaceProperty.toString())
                Currency.DOLLAR -> surface.text = String.format(getString(R.string.ft_2_surface_details), surfaceProperty!!.toSqFt().toString())
            }
        }
    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }



}
