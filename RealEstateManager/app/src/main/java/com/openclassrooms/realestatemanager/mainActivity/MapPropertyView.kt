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
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapquest.mapping.MapQuest
import com.mapquest.mapping.maps.MapView
import com.mapquest.mapping.maps.MyLocationPresenter
import com.mapquest.mapping.utils.MapUtils
import com.mapzen.android.lost.api.LocationServices
import com.mapzen.android.lost.api.LostApiClient
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.extensions.toBounds
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
class MapPropertyView : Fragment(), LostApiClient.ConnectionCallbacks, MainActivity.OnClickChangeCurrencyListener {

    @BindView(R.id.map_view_map) lateinit var mapView: MapView

    private var lostApiClient: LostApiClient? = null

    private lateinit var viewModel: ListPropertyViewModel

    private var userLocationBounds: LatLngBounds? = null

    private var propertiesNearBy = mutableListOf<PropertyForListDisplay>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_property_view, container, false)
        MapQuest.start(activity!!.applicationContext)
        ButterKnife.bind(this, view)
        configureViewModel()
        mapView.onCreate(savedInstanceState)
        lostApiClient = LostApiClient.Builder(activity!!.applicationContext).addConnectionCallbacks(this).build()
        fetchUserLocation()
        setupCurrencyListener()
        return view
    }

    private fun setupCurrencyListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setOnClickChangeCurrency(this)
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
            propertiesNearBy.forEach {
                val positionProperty = LatLng(it.lat, it.lng)
                val markerOption = MarkerOptions().apply {
                    position = positionProperty
                    title = it.type
                    snippet = when (currency) {
                        Currency.EURO -> "${it.price.toEuroDisplay()}€"
                        Currency.DOLLAR -> "$${it.price.toDollarDisplay()}"
                    }
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

        lostApiClient?.connect()

    }

    private fun displayUserLocation(locationUser: LatLng){
        mapView.getMapAsync { mapbox ->
            MyLocationPresenter(mapView, mapbox, null).apply {
                setInitialZoomLevel(18.0)
                setFollowCameraAngle(50.0)
                setLockNorthUp(false)
                setFollow(true)
                onStart()
            }
            mapbox.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, 14.0))

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

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        val location = LocationServices.FusedLocationApi.getLastLocation(lostApiClient!!)
        location?.let{
            val latLng = LatLng(location.latitude, location.longitude)
            userLocationBounds = latLng.toBounds(2500.0)
            viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
            displayUserLocation(latLng)
        }
    }

    override fun onConnectionSuspended() {
        showSnackBarMessage(getString(R.string.no_gps))
    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}
