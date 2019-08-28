package com.openclassrooms.realestatemanager.addProperty


import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.extensions.*
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.Currency
import java.net.URL
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class AddPropertyView : Fragment(), REMView<AddPropertyViewState>,
        PickDateDialogView.OnOkButtonListener, ListAgentsDialogView.OnAgentSelected{

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
    @BindView(R.id.add_property_view_dropdown_agent) lateinit var dropdowAgent: EditText
    @BindView(R.id.add_property_view_nearby_school) lateinit var schoolBox: CheckBox
    @BindView(R.id.add_property_view_nearby_buses) lateinit var busesBox: CheckBox
    @BindView(R.id.add_property_view_nearby_park) lateinit var parkBox: CheckBox
    @BindView(R.id.add_property_view_nearby_playground) lateinit var playgroundBox: CheckBox
    @BindView(R.id.add_property_view_nearby_subway) lateinit var subwayBox: CheckBox
    @BindView(R.id.add_property_view_nearby_shop) lateinit var shopBox: CheckBox
    @BindView(R.id.add_property_view_soldon_inputlayout) lateinit var soldOnLayout: TextInputLayout
    @BindView(R.id.add_property_view_surface_layout) lateinit var surfaceLayout: TextInputLayout
    @BindView(R.id.add_property_view_price_layout) lateinit var priceLayout: TextInputLayout
    @BindView(R.id.add_property_view_room_layout) lateinit var roomLayout: TextInputLayout
    @BindView(R.id.add_property_view_bedroom_layout) lateinit var bedroomLayout: TextInputLayout
    @BindView(R.id.add_property_view_bathroom_layout) lateinit var bathroomLayout: TextInputLayout
    @BindView(R.id.add_property_view_description_inputlayout) lateinit var descriptionLayout: TextInputLayout
    @BindView(R.id.add_property_view_address_inputlayout) lateinit var addressLayout: TextInputLayout
    @BindView(R.id.add_property_view_neighbourhood_inputlayout) lateinit var neighbourhoodLayout: TextInputLayout
    @BindView(R.id.add_property_view_since_inputlayout) lateinit var onMarketSinceLayout: TextInputLayout
    @BindView(R.id.add_property_view_dropdown_agent_inputlayout) lateinit var agentLayout: TextInputLayout
    @BindView(R.id.add_property_view_type_inputlayout) lateinit var typeLayout: TextInputLayout

    private lateinit var viewModel: AddPropertyViewModel
    private var agentSelectedId: Int? = null

    private lateinit var callback: OnCurrencyChangedListener

    interface OnCurrencyChangedListener{
        fun onClickCurrency(currency: Currency)
    }

    companion object {

        fun newInstance(actionType: String) = AddPropertyView().apply {
            arguments = bundleOf(ACTION_TYPE to actionType)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_property_view, container, false)
        ButterKnife.bind(this, view)
        configureViewModel()
        configureTypeDropdownOptions()
        configureActionType()

        return view

    }

    private fun configureActionType(){
        val argument = arguments?.getString(ACTION_TYPE, "")
        argument?.let{
            val actionType = ActionType.valueOf(it)
            viewModel.actionFromIntent(AddPropertyIntent.SetActionTypeIntent(actionType))
        }

    }

    fun setOnCurrencyChangedListener(callback: OnCurrencyChangedListener){
        this.callback = callback
    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarClickListener(buttonId: Int?){
        when(buttonId){
            R.id.menu_add_property_activity_currency -> viewModel.actionFromIntent(AddPropertyIntent.ChangeCurrencyIntent)
            R.id.menu_add_property_activity_check -> fetchInfoPropertyFromUI()
        }
    }

    override fun onOkButtonListener(calendar: Calendar, view: View) {
        val dateForDisplay = calendar.time.toStringForDisplay()
        when(view){
            onMarketSinceText -> onMarketSinceText.setText(dateForDisplay)
            soldOnText -> soldOnText.setText(dateForDisplay)
        }
    }

    @OnClick(value = [R.id.add_property_view_sold_on, R.id.add_property_view_since])
    fun onClickDate(view: View){
        val calendar: Calendar? = when(view){
            onMarketSinceText -> onMarketSinceText.text.toString().toDate()?.toCalendar()
            soldOnText -> soldOnText.text.toString().toDate()?.toCalendar()
            else -> null
        }

        val datePickerDialog = PickDateDialogView(view, calendar)
        datePickerDialog.setTargetFragment(this, PICK_DATE_DIALOG_CODE)
        datePickerDialog.show(fragmentManager!!.beginTransaction(), PICK_DATE_TAG)

    }

    @OnClick(R.id.add_property_view_dropdown_agent)
    fun onClickAgentDropdown(){
        viewModel.actionFromIntent(AddPropertyIntent.OpenListAgentsIntent)
    }

    override fun onAgentSelected(agent: Agent) {
        val displayNameAgent = "${agent.firstName} ${agent.lastName}"
        dropdowAgent.setText(displayNameAgent)
        agentSelectedId = agent.id
    }

    @OnClick(R.id.add_property_view_sold_switch)
    fun clickSoldButtonListener(){
        if(soldSwithch.isChecked){
            soldOnText.visibility = View.VISIBLE
            soldOnLayout.visibility = View.VISIBLE
        } else {
            soldOnText.visibility = View.INVISIBLE
            soldOnLayout.visibility = View.INVISIBLE
        }

    }

    //--------------------
    // CONFIGURE UI
    //--------------------

    private fun configureTypeDropdownOptions(){
        val propertyType = mutableListOf<String>()
        TypeProperty.values().forEach {
            propertyType.add(it.typeName)
        }

        ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, propertyType)
                .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dropdowPropertyType.setAdapter(adapter) }
    }

    fun configureCurrentCurrency(){
        viewModel.actionFromIntent(AddPropertyIntent.GetCurrentCurrencyIntent)
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(AddPropertyViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    //--------------------
    // STATE AND INTENT
    //--------------------

    override fun render(state: AddPropertyViewState?){
        if (state == null) return
        if(state.isSaved) {
            renderPropertyAddedToDB()
        }

        if(state.errors != null){
            renderErrors(state.errors)
        }
        changeCurrency(state.currency)

        if(state.openListAgents && state.listAgents != null){
            renderAgentDialog(state.listAgents)
        }

        if(state.isModifyProperty){
            renderDataFetchedProperty(
                    state.type, state.price!!, state.surface!!, state.rooms!!,
                    state.bedrooms, state.bathrooms, state.description, state.address,
                    state.neighborhood, state.onMarketSince, state.isSold, state.sellDate,
                    state.agentId!!, state.amenities!!, state.pictures, state.agentFirstName,
                    state.agentLastName, state.currency
            )
        }


    }

    private fun changeCurrency(currency: Currency){
        callback.onClickCurrency(currency)
        when(currency){
            Currency.EURO -> {
                surfaceLayout.hint = getString(R.string.surface_m2)
                priceLayout.hint = getString(R.string.price_euros)
            }
            Currency.DOLLAR -> {
                surfaceLayout.hint = getString(R.string.surface_ft2)
                priceLayout.hint = getString(R.string.price_dollar)
            }
        }
    }


    private fun renderErrors(errors: List<ErrorSourceAddProperty>){
        errors.forEach{
            when(it){
                ErrorSourceAddProperty.NO_TYPE_SELECTED -> typeLayout.error = getString(R.string.incorrect_type)
                ErrorSourceAddProperty.NO_PRICE -> priceLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_SURFACE -> surfaceLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_ROOMS -> roomLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_ADDRESS -> addressLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_NEIGHBORHOOD -> neighbourhoodLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_ON_MARKET_DATE -> onMarketSinceLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_SOLD_DATE -> soldOnLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.NO_AGENT -> agentLayout.error = getString(R.string.can_t_be_empty)
                ErrorSourceAddProperty.INCORRECT_SOLD_DATE -> soldOnLayout.error = getString(R.string.incorrect_date)
                ErrorSourceAddProperty.INCORRECT_ON_MARKET_DATE -> onMarketSinceLayout.error = getString(R.string.incorrect_date)
                ErrorSourceAddProperty.ERROR_FETCHING_AGENTS -> showSnackBarMessage(getString(R.string.error_finding_agents))
                ErrorSourceAddProperty.TOO_MANY_ADDRESS -> addressLayout.error = getString(R.string.incorrect_address)
                ErrorSourceAddProperty.INCORECT_ADDRESS -> addressLayout.error = getString(R.string.incorrect_address)
                ErrorSourceAddProperty.UNKNOW_ERROR -> showSnackBarMessage(getString(R.string.unknow_error))
            }
        }
    }

    private fun renderAgentDialog(agents: List<Agent>){
        val listAgentDialog = ListAgentsDialogView(agents)
        listAgentDialog.setTargetFragment(this, AGENT_LIST_DIALOG_CODE)
        listAgentDialog.show(fragmentManager!!.beginTransaction(), AGENT_LIST_TAG)

    }

    private fun renderPropertyAddedToDB(){
        activity!!.setResult(RESULT_OK)
        activity!!.finish()
    }

    private fun renderDataFetchedProperty(type: String, price: Double,
                                          surface: Double, rooms: Int,
                                          bedrooms: Int?, bathrooms: Int?,
                                          description: String?, address: String,
                                          neighborhood: String?, onMarketSince: String,
                                          isSold: Boolean, sellOn: String?,
                                          agentId: Int, amenities: List<TypeAmenity>,
                                          pictures: List<Picture>?, agentFirstName: String,
                                          agentLastName: String, currency: Currency){
        val priceToDisplay = when(currency){
            Currency.EURO -> price.toString()
            Currency.DOLLAR -> price.toDollar().toString()
        }
        val surfaceToDisplay = when(currency){
            Currency.EURO -> surface.toString()
            Currency.DOLLAR -> surface.toSqFt().toString()
        }
        priceText.setText(priceToDisplay)
        surfaceText.setText(surfaceToDisplay)
        roomText.setText(rooms.toString())
        bedrooms?.let { bedroomText.setText(it.toString()) }
        bathrooms?.let { bathroomText.setText(it.toString()) }
        description?.let{ descriptionText.setText(it)}
        addressText.setText(address)
        neighborhood?.let { neighbourhoodText.setText(it) }
        soldSwithch.isChecked = isSold
        sellOn?.let{ soldOnText.setText(sellOn)}
        onMarketSinceText.setText(onMarketSince)
        dropdowPropertyType.setText(type)
        agentSelectedId = agentId
        val displayNameAgent = "$agentFirstName $agentLastName"
        dropdowAgent.setText(displayNameAgent)
        amenities.forEach {
            when(it){
                TypeAmenity.SCHOOL -> schoolBox.isChecked = true
                TypeAmenity.PLAYGROUND -> playgroundBox.isChecked = true
                TypeAmenity.SHOP -> shopBox.isChecked = true
                TypeAmenity.BUSES -> busesBox.isChecked = true
                TypeAmenity.SUBWAY -> subwayBox.isChecked = true
                TypeAmenity.PARK -> parkBox.isChecked = true
            }
        }

    }

    private fun disableAllErrors(){
        priceLayout.isErrorEnabled = false
        surfaceLayout.isErrorEnabled = false
        roomLayout.isErrorEnabled = false
        addressLayout.isErrorEnabled = false
        neighbourhoodLayout.isErrorEnabled = false
        onMarketSinceLayout.isErrorEnabled = false
        soldOnLayout.isErrorEnabled = false
    }

    private fun fetchInfoPropertyFromUI(){
        disableAllErrors()
        val listAmenities = mutableListOf<TypeAmenity>()
        if(schoolBox.isChecked) listAmenities.add(TypeAmenity.SCHOOL)
        if(parkBox.isChecked) listAmenities.add(TypeAmenity.PARK)
        if(busesBox.isChecked) listAmenities.add(TypeAmenity.BUSES)
        if(subwayBox.isChecked) listAmenities.add(TypeAmenity.SUBWAY)
        if(shopBox.isChecked) listAmenities.add(TypeAmenity.SHOP)
        if(playgroundBox.isChecked) listAmenities.add(TypeAmenity.PLAYGROUND)

        val typeProperty = dropdowPropertyType.text.toString()

        viewModel.actionFromIntent(AddPropertyIntent.AddPropertyToDBIntent(
                typeProperty, priceText.text.toString(), surfaceText.text.toString(), roomText.text.toString(),
                bedroomText.text.toString(), bathroomText.text.toString(), descriptionText.text.toString(),
                addressText.text.toString(), neighbourhoodText.text.toString(), onMarketSinceText.text.toString(),
                soldSwithch.isChecked, soldOnText.text.toString(), agentSelectedId, listAmenities, null, null,
                activity!!.applicationContext)
        )
    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}
