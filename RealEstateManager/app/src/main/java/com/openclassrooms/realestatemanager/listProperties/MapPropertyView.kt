package com.openclassrooms.realestatemanager.listProperties


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
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
import com.openclassrooms.realestatemanager.mainActivity.MainActivity
import com.openclassrooms.realestatemanager.utils.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


/**
 * A simple [Fragment] subclass.
 *
 */
class MapPropertyView : BaseViewListProperties(),
        MainActivity.OnClickChangeCurrencyListener, MainActivity.OnListPropertiesChangeListener {

    @BindView(R.id.map_view_map) lateinit var mapView: MapView
    @BindView(R.id.map_view_button) lateinit var buttonCenter: Button

    private var userLocationBounds: LatLngBounds? = null
    private var userLastKnowLocation: LatLng? = null

    private var propertiesNearBy = mutableListOf<PropertyForListDisplay>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_property_view, container, false)
        ButterKnife.bind(this, view)
        configureViewModel()
        mapView.onCreate(savedInstanceState)
        fetchUserLocation()
        setupCurrencyListener()
        setupRefreshPropertiesListener()
        return view
    }

    private fun setupCurrencyListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setOnClickChangeCurrencyMap(this)
        }
    }

    private fun setupRefreshPropertiesListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setListPropertiesChangeMap(this)
        }
    }

    override fun onChangeCurrency(currency: Currency) {
        displayPropertiesAround(currency)
    }

    override fun onListPropertiesChange() {
        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------
    override fun renderListProperties(properties: List<PropertyForListDisplay>){
        propertiesNearBy.clear()
        properties.forEach { property ->
            val position = LatLng(property.lat, property.lng)
            if(userLocationBounds!!.contains(position)){
                    propertiesNearBy.add(property)
                displayPropertiesAround(Currency.EURO)

            }

        }
    }

    override fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty){
        when(errorSource){
            ErrorSourceListProperty.NO_PROPERTY_IN_DB -> showSnackBarMessage(getString(R.string.no_properties))
            ErrorSourceListProperty.CAN_T_ACCESS_DB -> showSnackBarMessage(getString(R.string.unknow_error))
        }
    }

    override fun renderIsLoading() {

    }

    private fun displayPropertiesAround(currency: Currency) {
        mapView.getMapAsync { mapbox ->
            mapbox.clear()
            val iconFactory = IconFactory.getInstance(activity!!.applicationContext)
            propertiesNearBy.forEach {
                val positionProperty = LatLng(it.lat, it.lng)
                val drawable = if (it.sold) R.drawable.icon_location_sold else R.drawable.icon_location_normal
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
                    userLastKnowLocation = LatLng(
                            lastKnowLocation.latitude,
                            lastKnowLocation.longitude
                    )
                    userLocationBounds = userLastKnowLocation?.toBounds(2500.0)
                    viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)

                    mapboxMap.addOnCameraMoveListener {
                        val newLocation = mapboxMap.cameraPosition.target
                        if(newLocation != lastKnowLocation){
                            buttonCenter.visibility = View.VISIBLE
                        }

                    }
                } else{
                    showSnackBarMessage(getString(R.string.no_gps))
                }
            }

        }

    }

    private fun centerCameraOnUser(){
        mapView.getMapAsync{mapboxMap ->
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(userLastKnowLocation!!))
            buttonCenter.visibility = View.GONE
        }
    }

    @OnClick(R.id.map_view_button)
    fun centerCameraButtonListener(){
        centerCameraOnUser()
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

}
