package com.openclassrooms.realestatemanager.searchProperty


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty
import com.openclassrooms.realestatemanager.utils.extensions.toDouble
import com.openclassrooms.realestatemanager.utils.extensions.toInt

/**
 * A simple [Fragment] subclass.
 */
class SearchPropertyView : Fragment(), REMView<SeachPropertyViewState>, ListAgentSearchAdapter.ListenerCheckBox {

    @BindView(R.id.search_select_all_agents) lateinit var selectAllAgents: CheckBox
    @BindView(R.id.search_view_rv_agents) lateinit var recyclerViewAgents: RecyclerView
    @BindView(R.id.search_view_min_price) lateinit var minPrice: EditText
    @BindView(R.id.search_view_max_price) lateinit var maxPrice: EditText
    @BindView(R.id.search_view_min_surface) lateinit var minSurface: EditText
    @BindView(R.id.search_view_max_surface) lateinit var maxSurface: EditText
    @BindView(R.id.search_view_min_rooms) lateinit var minRooms: EditText
    @BindView(R.id.search_view_min_bedrooms) lateinit var minBedrooms: EditText
    @BindView(R.id.search_view_min_bathrooms) lateinit var minBathrooms: EditText
    @BindView(R.id.search_view_neighborhood) lateinit var neighborhood: EditText
    @BindView(R.id.search_view_is_available) lateinit var isAvailable: CheckBox
    @BindView(R.id.checkbox_nearby_school) lateinit var schoolBox: CheckBox
    @BindView(R.id.checkbox_nearby_buses) lateinit var busesBox: CheckBox
    @BindView(R.id.checkbox_nearby_park) lateinit var parkBox: CheckBox
    @BindView(R.id.checkbox_nearby_playground) lateinit var playgroundBox: CheckBox
    @BindView(R.id.checkbox_nearby_subway) lateinit var subwayBox: CheckBox
    @BindView(R.id.checkbox_nearby_shop) lateinit var shopBox: CheckBox
    @BindView(R.id.search_view_flat_check) lateinit var flatBox: CheckBox
    @BindView(R.id.search_view_townhouse_check) lateinit var townhouseBox: CheckBox
    @BindView(R.id.search_view_penthouse_check) lateinit var penthouseBox: CheckBox
    @BindView(R.id.search_view_house_check) lateinit var houseBox: CheckBox
    @BindView(R.id.search_view_duplex_check) lateinit var duplexBox: CheckBox
    @BindView(R.id.search_view_photo) lateinit var withPictures: CheckBox
    @BindView(R.id.search_view_select_all_amenities) lateinit var selectAllAmenities: CheckBox
    @BindView(R.id.search_view_select_all_type) lateinit var selectAllType: CheckBox
    @BindView(R.id.search_view_price_title) lateinit var priceTitle: TextView
    @BindView(R.id.search_view_surface_title) lateinit var surfaceTitle: TextView

    private lateinit var viewModel: SearchPropertyViewModel

    private val agentsSelectedId = mutableListOf<Int>()
    private var allAgents: List<Int>? = null
    private var adapter: ListAgentSearchAdapter?= null
    private lateinit var callback: OnCurrencyChangedListener

    interface OnCurrencyChangedListener{
        fun onClickCurrency(currency: Currency)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_property_view, container, false)
        ButterKnife.bind(this, view)

        configureViewModel()
        configureCurrentCurrency()

        viewModel.actionFromIntent(SearchPropertyIntent.GetListAgentsIntent)
        return view
    }

    fun setOnCurrencyChangedListener(callback: OnCurrencyChangedListener){
        this.callback = callback
    }

    fun configureCurrentCurrency(){
        viewModel.actionFromIntent(SearchPropertyIntent.GetCurrentCurrencyIntent)
    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarClickListener(buttonId: Int?){
        when(buttonId){
            R.id.menu_validate_activity_currency -> viewModel.actionFromIntent(SearchPropertyIntent.ChangeCurrencyIntent)
            R.id.menu_validate_activity_check -> viewModel.actionFromIntent(SearchPropertyIntent.SearchPropertyFromInputIntent(
                    getTypePropertySelected(), minPrice.toDouble(), maxPrice.toDouble(),
                    minSurface.toDouble(), maxSurface.toDouble(), minRooms.toInt(),
                    minBedrooms.toInt(), minBathrooms.toInt(), neighborhood.text.toString(), isAvailable.isChecked,
                    agentsSelectedId, getAmenitiesSelected(), null, withPictures.isChecked
            ))
        }
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun configureViewModel() {
        val viewModelFactory = Injection.providesViewModelFactory(activity!!)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SearchPropertyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    override fun render(state: SeachPropertyViewState?) {
        if (state == null) return
        state.agents?.let{renderAgentsList(it)}

        renderChangeCurrency(state.currency)
    }

    private fun renderAgentsList(agents: List<Agent>){
        adapter = ListAgentSearchAdapter(agents, Glide.with(this), this)
        recyclerViewAgents.adapter = adapter
        recyclerViewAgents.layoutManager = LinearLayoutManager(activity)
        allAgents = agents.map{it.id!!}
        agentsSelectedId.clear()
        agentsSelectedId.addAll(allAgents!!)
    }

    private fun renderChangeCurrency(currency: Currency){
        callback.onClickCurrency(currency)

        when(currency){
            Currency.EURO -> {
                surfaceTitle.text = getString(R.string.surface_m2)
                priceTitle.text = getString(R.string.price_euros)
            }
            Currency.DOLLAR -> {
                surfaceTitle.text = getString(R.string.surface_ft2)
                priceTitle.text = getString(R.string.price_dollar)
            }
        }


    }

    override fun onClickCheckBox(id: Int, isChecked: Boolean) {
        if(isChecked){
            agentsSelectedId.add(id)
            if(agentsSelectedId.size == allAgents?.size) selectAllAgents.isChecked = true
        } else {
            agentsSelectedId.remove(id)
            selectAllAgents.isChecked = false
        }
    }

    @OnClick(R.id.search_select_all_agents)
    fun onClickSelectAllAgents(checkBox: CheckBox){
        if(checkBox.isChecked) {
            adapter?.selectAllAgents()
            agentsSelectedId.clear()
            allAgents?.let { agentsSelectedId.addAll(it) }
        }
    }

    @OnClick(R.id.search_view_select_all_amenities)
    fun onClickSelectAllAmenities(checkBox: CheckBox){
        if(checkBox.isChecked){
            schoolBox.isChecked = true
            parkBox.isChecked = true
            busesBox.isChecked = true
            subwayBox.isChecked = true
            shopBox.isChecked = true
            playgroundBox.isChecked = true
        }
    }

    @OnClick(R.id.search_view_select_all_type)
    fun onClickSelectAllTypes(checkBox: CheckBox){
        if(checkBox.isChecked){
            flatBox.isChecked = true
            penthouseBox.isChecked = true
            townhouseBox.isChecked = true
            houseBox.isChecked = true
            duplexBox.isChecked = true
        }
    }

    @OnClick(value = [
        R.id.checkbox_nearby_school, R.id.checkbox_nearby_buses, R.id.checkbox_nearby_park,
    R.id.checkbox_nearby_playground, R.id.checkbox_nearby_shop, R.id.checkbox_nearby_subway
    ])
    fun onClickOneAmenity(checkBox: CheckBox){
        if(!checkBox.isChecked && selectAllAmenities.isChecked){
            selectAllAmenities.isChecked = false
            return
        }

        if(
                busesBox.isChecked && schoolBox.isChecked && parkBox.isChecked
                && playgroundBox.isChecked && shopBox.isChecked && subwayBox.isChecked
        ){
           selectAllAmenities.isChecked = true
        }
    }

    @OnClick(value = [
    R.id.search_view_flat_check, R.id.search_view_penthouse_check, R.id.search_view_townhouse_check,
    R.id.search_view_house_check, R.id.search_view_duplex_check
    ])
    fun onClickOneType(checkBox: CheckBox){
        if(!checkBox.isChecked && selectAllType.isChecked){
            selectAllType.isChecked = false
            return
        }

        if(
                flatBox.isChecked && penthouseBox.isChecked && townhouseBox.isChecked
                && houseBox.isChecked && duplexBox.isChecked
        ){
            selectAllType.isChecked = true
        }
    }

    private fun getAmenitiesSelected(): List<TypeAmenity>{
        val listAmenities = mutableListOf<TypeAmenity>()
        if(schoolBox.isChecked) listAmenities.add(TypeAmenity.SCHOOL)
        if(parkBox.isChecked) listAmenities.add(TypeAmenity.PARK)
        if(busesBox.isChecked) listAmenities.add(TypeAmenity.BUSES)
        if(subwayBox.isChecked) listAmenities.add(TypeAmenity.SUBWAY)
        if(shopBox.isChecked) listAmenities.add(TypeAmenity.SHOP)
        if(playgroundBox.isChecked) listAmenities.add(TypeAmenity.PLAYGROUND)

        return listAmenities
    }

    private fun getTypePropertySelected(): List<TypeProperty>{
        val listType = mutableListOf<TypeProperty>()
        if(flatBox.isChecked) listType.add(TypeProperty.FLAT)
        if(penthouseBox.isChecked) listType.add(TypeProperty.PENTHOUSE)
        if(townhouseBox.isChecked) listType.add(TypeProperty.TOWNHOUSE)
        if(houseBox.isChecked) listType.add(TypeProperty.HOUSE)
        if(duplexBox.isChecked) listType.add(TypeProperty.DUPLEX)

        return listType
    }
}
