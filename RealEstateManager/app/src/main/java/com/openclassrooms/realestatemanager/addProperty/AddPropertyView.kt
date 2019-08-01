package com.openclassrooms.realestatemanager.addProperty


import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty

/**
 * A simple [Fragment] subclass.
 *
 */
class AddPropertyView : Fragment() {

    @BindView(R.id.add_property_view_dropdown_type) lateinit var dropdowPropertyType: AutoCompleteTextView
    @BindView(R.id.add_property_view_price) lateinit var priceText: EditText
    @BindView(R.id.add_property_view_surface) lateinit var surfaceText: EditText
    @BindView(R.id.add_property_view_room) lateinit var roomText: EditText
    @BindView(R.id.add_property_view_bedroom) lateinit var bedroomText: EditText
    @BindView(R.id.add_property_view_bathroom) lateinit var bathroomText: EditText
    @BindView(R.id.add_property_view_description) lateinit var descriptionText: EditText
    @BindView(R.id.add_property_view_address) lateinit var addressText: EditText
    @BindView(R.id.add_property_view_neighbourhood) lateinit var neighbourhoodText: EditText
    @BindView(R.id.add_property_view_since) lateinit var onMarketSinceText: EditText
    @BindView(R.id.add_property_view_sold_on) lateinit var soldOnText: EditText
    @BindView(R.id.add_property_view_sold_switch) lateinit var soldSwithch: Switch
    @BindView(R.id.add_property_view_dropdown_agent) lateinit var dropdowAgent: AutoCompleteTextView
    @BindView(R.id.add_property_view_nearby_school) lateinit var schoolBox: CheckBox
    @BindView(R.id.add_property_view_nearby_buses) lateinit var busesBox: CheckBox
    @BindView(R.id.add_property_view_nearby_park) lateinit var parkBox: CheckBox
    @BindView(R.id.add_property_view_nearby_playground) lateinit var playgroundBox: CheckBox
    @BindView(R.id.add_property_view_nearby_subway) lateinit var subwayBox: CheckBox
    @BindView(R.id.add_property_view_nearby_shop) lateinit var shopBox: CheckBox

    private lateinit var viewModel: AddPropertyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_property_view, container, false)
        ButterKnife.bind(this, view)

        configureViewModel()


        ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, TypeProperty.values())
                .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dropdowPropertyType.setAdapter(adapter) }

        return view


    }

    fun toolBarClickListener(buttonId: Int?){
        when(buttonId){
            R.id.menu_add_property_activity_currency -> viewModel.actionFromIntent(AddPropertyIntent.ChangeCurrencyIntent)
            R.id.menu_add_property_activity_check -> fetchInfoPropertyFromUI()
        }
    }

    private fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(AddPropertyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    private fun render(viewState: AddPropertyViewState?){
        if (viewState == null) return
        Log.e("tag", viewState.toString())
        if(viewState.isSaved) {
            Log.e("tag", "result received view")
            activity!!.finish()
        }
        if(viewState.errors != null){
            Log.e("erros", viewState.errors.toString())
        }
    }

    private fun fetchInfoPropertyFromUI(){
        val listAmenities = mutableListOf<TypeAmenity>()
        if(schoolBox.isChecked) listAmenities.add(TypeAmenity.SCHOOL)
        if(parkBox.isChecked) listAmenities.add(TypeAmenity.PARK)
        if(busesBox.isChecked) listAmenities.add(TypeAmenity.BUSES)
        if(subwayBox.isChecked) listAmenities.add(TypeAmenity.SUBWAY)
        if(shopBox.isChecked) listAmenities.add(TypeAmenity.SHOP)
        if(playgroundBox.isChecked) listAmenities.add(TypeAmenity.PLAYGROUND)

        val typeProperty = Converters.toTypeProperty(dropdowPropertyType.text.toString())

        viewModel.actionFromIntent(AddPropertyIntent.AddPropertyToDBIntent(
                typeProperty, priceText.text.toString(), surfaceText.text.toString(), roomText.text.toString(),
                bedroomText.text.toString(), bathroomText.text.toString(), descriptionText.text.toString(),
                addressText.text.toString(), neighbourhoodText.text.toString(), onMarketSinceText.text.toString(),
                soldSwithch.isChecked, soldOnText.text.toString(), 1, listAmenities, null, null)
        )
    }

    @OnClick(R.id.add_property_view_sold_switch)
    fun clickSoldButtonListener(){
        if(soldSwithch.isChecked){
            soldOnText.visibility = View.VISIBLE
        } else {
            soldOnText.visibility = View.GONE
        }

    }




}
