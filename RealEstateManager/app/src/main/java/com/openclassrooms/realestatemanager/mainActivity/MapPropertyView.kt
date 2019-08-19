package com.openclassrooms.realestatemanager.mainActivity


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ContentFrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.extensions.toBounds
import com.openclassrooms.realestatemanager.extensions.toDollar
import com.openclassrooms.realestatemanager.extensions.toDollarDisplay
import com.openclassrooms.realestatemanager.extensions.toEuroDisplay
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.PERMS_LOCALISATION
import com.openclassrooms.realestatemanager.utils.RC_LOCATION_PERMS
import com.openclassrooms.realestatemanager.utils.showSnackBar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


/**
 * A simple [Fragment] subclass.
 *
 */
class MapPropertyView : Fragment(), MainActivity.OnClickChangeCurrencyListener {

    @BindView(R.id.map_view_map) lateinit var mapView: MapView

    private lateinit var viewModel: ListPropertyViewModel

    private var userLocationBounds: LatLngBounds? = null

    private var propertiesNearBy = mutableListOf<PropertyForListDisplay>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_property_view, container, false)
        ButterKnife.bind(this, view)
        configureViewModel()
        mapView.onCreate(savedInstanceState)
        fetchUserLocation()
        setupCurrencyListener()
        return view
    }

    private fun setupCurrencyListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setOnClickChangeCurrencyMap(this)
        }
    }

    override fun onChangeCurrency(currency: Currency) {
        displayPropertiesAround(currency)
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    private fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(ListPropertyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })

    }

    private fun render(viewState: PropertyListViewState?) {
        if (viewState == null) return

        viewState.listProperties?.let {
            renderListProperties(viewState.listProperties)
        }

        viewState.errorSource?.let { renderErrorFetchingProperty(it) }


    }

    private fun renderListProperties(properties: List<PropertyForListDisplay>){
        propertiesNearBy.clear()
        properties.forEach { property ->
            val position = LatLng(property.lat, property.lng)
            if(userLocationBounds!!.contains(position)){
                    propertiesNearBy.add(property)
                displayPropertiesAround(Currency.EURO)

            }

        }
    }

    private fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty){
        when(errorSource){
            ErrorSourceListProperty.NO_PROPERTY_IN_DB -> showSnackBarMessage(getString(R.string.no_properties))
            ErrorSourceListProperty.CAN_T_ACCESS_DB -> showSnackBarMessage(getString(R.string.unknow_error))
        }
    }

    private fun displayPropertiesAround(currency: Currency) {
        mapView.getMapAsync { mapbox ->
            mapbox.clear()
            val iconFactory = IconFactory.getInstance(activity!!.applicationContext)
            propertiesNearBy.forEach {
                val positionProperty = LatLng(it.lat, it.lng)
                val drawable = if(it.sold) R.drawable.icon_location_sold else R.drawable.icon_location_normal
                val markerIcon = iconFactory.fromResource(drawable)
                val markerOption = MarkerOptions().apply {
                    position = positionProperty
                    title = it.type
                    snippet = when (currency) {
                        Currency.EURO -> "${it.price.toEuroDisplay()}€"
                        Currency.DOLLAR -> "$${it.price.toDollar().toDollarDisplay()}"
                    }
                    icon = markerIcon
                }
                mapbox.addMarker(markerOption)



            }
        }
    }

    @AfterPermissionGranted(RC_LOCATION_PERMS)
    fun fetchUserLocation(){
        if(! EasyPermissions.hasPermissions(context!!, PERMS_LOCALISATION)) {
            EasyPermissions.requestPermissions(
                    this, getString(R.string.storage_perm_request), RC_LOCATION_PERMS, PERMS_LOCALISATION)
            return
        }

        displayUserLocation()

    }

    @SuppressLint("MissingPermission")
    private fun displayUserLocation(){
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
                val locationComponent = mapboxMap.locationComponent.apply {
                    activateLocationComponent(LocationComponentActivationOptions.builder(activity!!.applicationContext, style).build())
                    isLocationComponentEnabled = true
                    cameraMode = CameraMode.TRACKING
                    renderMode = RenderMode.COMPASS
                }
                val lastKnowLocation = locationComponent.lastKnownLocation
                if(lastKnowLocation != null){
                    val latLngBounds = LatLng(
                            lastKnowLocation.latitude,
                            lastKnowLocation.longitude
                    )
                    userLocationBounds = latLngBounds.toBounds(2500.0)
                    viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
                } else{
                    showSnackBarMessage(getString(R.string.no_gps))
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

     override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

     override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}
