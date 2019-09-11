package com.openclassrooms.realestatemanager.listProperties


import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import com.openclassrooms.realestatemanager.mainActivity.MainActivity
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.extensions.*
import java.lang.ref.WeakReference


/**
 * A simple [Fragment] subclass.
 *
 */
class MapPropertyView : BaseViewListProperties(),
       MainActivity.OnListPropertiesChangeListener, MapboxMap.OnInfoWindowClickListener,
MainActivity.OnTabSelectedListener{

    @BindView(R.id.map_view_map) lateinit var mapView: MapView
    @BindView(R.id.map_view_button) lateinit var buttonCenter: Button

    private var userLocationBounds: LatLngBounds? = null
    private var userLastKnowLocation: LatLng? = null

    private var propertiesNearBy = mutableListOf<PropertyWithAllData>()

    private var mapBoxMap: MapboxMap? = null
    private lateinit var contextApp: Context


    companion object {

        fun newInstance(actionType: String) = MapPropertyView().apply {
            arguments = bundleOf(ACTION_TYPE_LIST_PROPERTY to actionType)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_property_view, container, false)
        ButterKnife.bind(this, view)
        contextApp = activity!!
        configureViewModel()
        setupCallbackToActivity()
        displayMap(savedInstanceState)

        return view
    }

    private fun setupCallbackToActivity(){
        if(activity is MainActivity){
            (activity as MainActivity).callbackMapPropertiesRefresh = WeakReference(this)
            (activity as MainActivity).callbackTabListener = WeakReference(this)
        }
    }

    override fun onListPropertiesChange() {
        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    private fun displayMap(savedInstanceState: Bundle?){
        if(isInternetAvailable(contextApp) && isGPSAvailable(contextApp)) {
            configureActionType()
            currencyObserver()
            mapView.onCreate(savedInstanceState)
            displayData()
        }
    }

    private fun displayData(){
        if(requestPermissionLocation(this)){
            displayUserLocation()
        }
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------
    override fun renderListProperties(properties: List<PropertyWithAllData>){
        propertiesNearBy.clear()
        properties.forEach { property ->
            val position = LatLng(property.address[0].latitude, property.address[0].longitude)
            userLocationBounds?.let{
                if(it.contains(position)){
                    propertiesNearBy.add(property)
                    displayPropertiesAround(currentCurrency!!)
                }
            }
        }
    }

    override fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty){
        when(errorSource){
            ErrorSourceListProperty.NO_PROPERTY_IN_DB -> showSnackBarMessage(getString(R.string.no_properties))
            ErrorSourceListProperty.CAN_T_ACCESS_DB -> showSnackBarMessage(getString(R.string.unknow_error))
        }
    }

    override fun renderIsLoading(loading: Boolean) {

    }

    override fun renderChangeCurrency(currency: Currency) {
        mapBoxMap?.let { displayPropertiesAround(currency) }

    }

    //--------------------
    // MAP ACTIONS
    //--------------------

    private fun displayPropertiesAround(currency: Currency) {
        mapBoxMap?.clear()
        mapBoxMap?.onInfoWindowClickListener = this
        val iconFactory = IconFactory.getInstance(contextApp.applicationContext)
        propertiesNearBy.forEach {
            val positionProperty = LatLng(it.address[0].latitude, it.address[0].longitude)
            val drawable = if (it.property.sold) R.drawable.icon_location_sold else R.drawable.icon_location_normal
            val snippet = when (currency) {
                Currency.EURO -> "${it.property.price.toEuroDisplay()}€"
                Currency.DOLLAR -> "$${it.property.price.toDollar().toDollarDisplay()}"
            }
            val markerREM = MarkerREMOptions()
                    .title(it.property.type.typeName)
                    .position(positionProperty)
                    .snippet(snippet)
                    .icon(iconFactory.fromResource(drawable))
            markerREM.idRem = it.property.id
            mapBoxMap?.addMarker(markerREM)
        }

    }

    private fun displayUserLocation(){
        mapView.getMapAsync {
            mapBoxMap = it
            mapBoxMap?.setStyle(Style.MAPBOX_STREETS) { style ->

                fun getAndDisplayUserLocation(): LocationComponent{
                    return mapBoxMap!!.locationComponent.apply {
                        activateLocationComponent(LocationComponentActivationOptions.builder(contextApp.applicationContext, style).build())
                        isLocationComponentEnabled = true
                        cameraMode = CameraMode.TRACKING
                        renderMode = RenderMode.COMPASS
                    }
                }
                val lastKnowLocation = getAndDisplayUserLocation().lastKnownLocation
                if(lastKnowLocation != null){
                    computerUserLocationBound(lastKnowLocation)
                    viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
                    addCameraMovementListener(lastKnowLocation)


                } else{
                    showSnackBarMessage(getString(R.string.no_gps))
                }
            }

        }

    }

    private fun computerUserLocationBound(userLocation: Location){
        userLastKnowLocation = LatLng(
                userLocation.latitude,
                userLocation.longitude
        )
        userLocationBounds = userLastKnowLocation?.toBounds(2500.0)

    }

    private fun addCameraMovementListener(userLocation: Location){
        mapBoxMap!!.addOnCameraMoveListener {
            val newLocation = mapBoxMap!!.cameraPosition.target
            if(newLocation.isEqualTo(userLocation, 3)){
                buttonCenter.visibility = View.INVISIBLE
            } else {
                buttonCenter.visibility = View.VISIBLE
            }
        }
    }

    private fun centerCameraOnUser(){
        mapBoxMap?.animateCamera(CameraUpdateFactory.newLatLng(userLastKnowLocation!!))

    }

    @OnClick(R.id.map_view_button)
    fun centerCameraButtonListener(){
        centerCameraOnUser()
    }

    override fun onInfoWindowClick(marker: Marker): Boolean {
        val markerREM = marker as MarkerREM
        setPropertyPicked(propertiesNearBy.find{it.property.id == markerREM.idRem}!!)
        return false
    }

    override fun onMapSelectedListener() {
        if(!isInternetAvailable(contextApp) || !isGPSAvailable(contextApp)){
            showSnackBarMessage(getString(R.string.no_gps_data))
        }

    }

    //--------------------
    // LIFE STATE MAP
    //--------------------

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

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
}
