package com.openclassrooms.realestatemanager.detailsProperty


import android.os.Bundle
import android.util.Log
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
import com.neovisionaries.i18n.CountryCode

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.Currency
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

    private lateinit var callback: OnCurrencyChangedListener

    private lateinit var viewModel: DetailsPropertyViewModel

    interface OnCurrencyChangedListener{
        fun onClickCurrency(currency: Currency)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details_property_view, container, false)
        ButterKnife.bind(this, view)

        configureViewModel()
        viewModel.actionFromIntent(DetailsPropertyIntent.FetchDetailsIntent)
        return view
    }

    fun configureCurrentCurrency(){
        viewModel.actionFromIntent(DetailsPropertyIntent.GetCurrentCurrencyIntent)
    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarClickListener(buttonId: Int?){
        when(buttonId){
            R.id.menu_details_property_currency -> viewModel.actionFromIntent(DetailsPropertyIntent.ChangeCurrencyIntent)
            R.id.menu_details_property_modify -> viewModel.actionFromIntent(DetailsPropertyIntent.ModifyPropertyIntent)
        }
    }

    fun setOnCurrencyChangedListener(callback: OnCurrencyChangedListener){
        this.callback = callback
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

    //--------------------
    // STATE AND INTENT
    //--------------------

    override fun render(state: DetailsPropertyViewState?) {
        if (state == null) return

        state.property?.let {
            renderFetchedProperty(it, state.address!!)
        }
    }

    private fun renderIsLoading(){

    }

    private fun renderFetchedProperty(property: Property, address: Address){
        description.text = property.description
        surface.text = property.surface.toString()
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
        country.text = CountryCode.getByCode(address.country).getName()
        Glide.with(this).load(address.mapIconUrl).into(map)
        Log.e("map", address.mapIconUrl)
    }



}