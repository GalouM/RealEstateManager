package com.openclassrooms.realestatemanager.detailsProperty


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.ContentFrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.addProperty.ActionType
import com.openclassrooms.realestatemanager.addProperty.AddPropertyActivity
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.extensions.*
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderView

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
    @BindView(R.id.details_view_location_zip) lateinit var zipCode: TextView
    @BindView(R.id.details_view_location_country) lateinit var country: TextView
    @BindView(R.id.details_view_map) lateinit var map: ImageView
    @BindView(R.id.details_view_slider_pictures) lateinit var sliderPictures: SliderView
    @BindView(R.id.detail_view_main_layout) lateinit var mainLayout: ScrollView
    @BindView(R.id.detail_view_price_icon) lateinit var priceIcon: ImageView
    @BindView(R.id.detail_view_price) lateinit var price: TextView
    @BindView(R.id.detail_view_type) lateinit var type: TextView
    @BindView(R.id.detail_view_amenity_one) lateinit var amenity1: ImageView
    @BindView(R.id.detail_view_amenity_two) lateinit var amenity2: ImageView
    @BindView(R.id.detail_view_amenity_three) lateinit var amenity3: ImageView
    @BindView(R.id.detail_view_amenity_four) lateinit var amenity4: ImageView
    @BindView(R.id.detail_view_amenity_five) lateinit var amenity5: ImageView
    @BindView(R.id.detail_view_amenity_six) lateinit var amenity6: ImageView


    private lateinit var viewModel: DetailsPropertyViewModel

    private var currentCurrency: Currency? = null

    private lateinit var amenitiesImageView: List<ImageView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details_property_view, container, false)
        ButterKnife.bind(this, view)
        initiateAmenitiesIVList()

        configureViewModel()
        currencyObserver()
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

    private fun initiateAmenitiesIVList(){
        amenitiesImageView = listOf(amenity1, amenity2, amenity3, amenity4, amenity5, amenity6)
    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarModifyClickListener(){
        val intent = Intent(activity, AddPropertyActivity::class.java)
        intent.putExtra(ACTION_TYPE_ADD_PROPERTY, ActionType.MODIFY_PROPERTY.actionName)
        startActivityForResult(intent, RC_CODE_ADD_PROPERTY)
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
            renderChangeCurrency()
        })
    }

    //--------------------
    // STATE AND INTENT
    //--------------------

    override fun render(state: DetailsPropertyViewState?) {
        if (state == null) return

        if(state.property != null && currentCurrency != null){
            renderFetchedProperty(state.property)
        }

        state.pictures?.let { renderFetchedPictures(it) }
        state.address?.let { renderFetchedAddress(it) }
        state.amenities?.let { renderFetchedAmenities(it) }

        renderIsLoading(state.isLoading)
    }

    private fun renderIsLoading(loading: Boolean){

    }

    private fun renderFetchedProperty(property: Property){
        val surfaceProperty = property.surface
        val priceProperty = property.price
        var iconPrice: Int? = null
        when(currentCurrency){
            Currency.EURO -> {
                surface.text = String.format(getString(R.string.surface_m2_details), surfaceProperty.toString())
                iconPrice = R.drawable.euro_icon
                price.text = String.format(getString(R.string.price_euro_details), priceProperty.toEuroDisplay())
            }
            Currency.DOLLAR -> {
                surface.text = String.format(getString(R.string.ft_2_surface_details), surfaceProperty.toSqFt().toString())
                price.text = String.format(getString(R.string.price_dollar_details), priceProperty.toDollar().toDollarDisplay())
                iconPrice = R.drawable.dollar_icon
            }
        }

        priceIcon.setImageResource(iconPrice!!)

        description.text = property.description
        rooms.text = property.rooms.toString()
        property.bedrooms?.let{
            bedRooms.text = it.toString()
        }
        property.bathrooms?.let{
            bathRooms.text = it.toString()
        }

        if(property.sold){
            mainLayout.setBackgroundColor(
                    ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimaryLight)
            )
        } else{
            mainLayout.setBackgroundColor(Color.WHITE)
        }
        type.text = property.type.typeName

    }

    private fun renderFetchedAddress(address: Address){
        street.text = address.street
        city.text = address.city
        zipCode.text = String.format("%s %s", address.state, address.postalCode)
        country.text = address.country
        Glide.with(this).load(address.mapIconUrl).into(map)
    }

    private fun renderFetchedPictures(pictures: List<Picture>){
        if(pictures.isEmpty()){
            sliderPictures.visibility = View.GONE
        } else {
            val adapter = SliderPhotoAdapter(
                    activity!!.applicationContext, pictures.sortedBy { it.orderNumber }, Glide.with(this)
            )
            sliderPictures.apply{
                sliderAdapter = adapter
                setIndicatorAnimation(IndicatorAnimations.SCALE)
            }
        }
    }

    private fun renderFetchedAmenities(amenities: List<Amenity>){
        amenitiesImageView.forEach {it.setImageResource(0)}

        if(amenities.size <= amenitiesImageView.size){
            amenities.sortedBy { it.type }.forEachIndexed { index, amenity ->
                amenitiesImageView[index].setImageResource(amenity.toDrawable())
            }
        }
    }


    private fun renderChangeCurrency(){
        viewModel.actionFromIntent(DetailsPropertyIntent.DisplayDetailsIntent)
    }


    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }

}
