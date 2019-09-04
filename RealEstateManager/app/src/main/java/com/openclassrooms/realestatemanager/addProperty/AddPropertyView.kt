package com.openclassrooms.realestatemanager.addProperty


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.utils.extensions.*
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.Currency
import kotlinx.android.synthetic.main.dialog_photo_source.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
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
    @BindView(R.id.checkbox_nearby_school) lateinit var schoolBox: CheckBox
    @BindView(R.id.checkbox_nearby_buses) lateinit var busesBox: CheckBox
    @BindView(R.id.checkbox_nearby_park) lateinit var parkBox: CheckBox
    @BindView(R.id.checkbox_nearby_playground) lateinit var playgroundBox: CheckBox
    @BindView(R.id.checkbox_nearby_subway) lateinit var subwayBox: CheckBox
    @BindView(R.id.checkbox_nearby_shop) lateinit var shopBox: CheckBox
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
    private var currentCurrency: Currency? = null

    private var openAgentWindowHandled = true

    private val listUrlPictures = mutableListOf<String>()

    companion object {

        fun newInstance(actionType: String) = AddPropertyView().apply {
            arguments = bundleOf(ACTION_TYPE_ADD_PROPERTY to actionType)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_property_view, container, false)
        ButterKnife.bind(this, view)
        configureViewModel()
        currencyObserver()
        configureTypeDropdownOptions()
        configureActionType()

        return view

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_CHOOSE_PHOTO -> {
                if(resultCode == RESULT_OK){
                    data?.let{addPicturePickedToList(it)}
                }
            }
        }
    }

    private fun configureActionType(){
        val argument = arguments?.getString(ACTION_TYPE_ADD_PROPERTY, "")
        argument?.let{
            val actionType = ActionType.valueOf(it)
            viewModel.actionFromIntent(AddPropertyIntent.SetActionTypeIntent(actionType))
        }

    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarValidateClickListener(){
        fetchInfoPropertyFromUI()
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
        openAgentWindowHandled = false
        viewModel.actionFromIntent(AddPropertyIntent.OpenListAgentsIntent)
    }

    override fun onAgentSelected(agent: Agent) {
        val displayNameAgent = "${agent.firstName} ${agent.lastName}"
        dropdowAgent.setText(displayNameAgent)
        agentSelectedId = agent.id
        openAgentWindowHandled = true
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

    @OnClick(value = [R.id.add_property_view_add_picture_button, R.id.add_property_view_add_picture_text])
    fun onClickAddPicture(){
        openChoosePhotoSourceDialog()
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

    private fun currencyObserver(){
        viewModel.currency.observe(this, Observer {currency ->
            currentCurrency = currency
            renderChangeCurrency(currentCurrency!!)
        })
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

        state.listAgents?.let{
            renderAgentDialog(it)
        }

        if(state.isModifyProperty && currentCurrency != null){
            renderDataFetchedProperty(
                    state.type, state.price!!, state.surface!!, state.rooms!!,
                    state.bedrooms, state.bathrooms, state.description, state.address,
                    state.neighborhood, state.onMarketSince, state.isSold, state.sellDate,
                    state.agentId!!, state.amenities!!, state.pictures, state.agentFirstName,
                    state.agentLastName
            )
        }


    }

    private fun renderChangeCurrency(currency: Currency){
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
                ErrorSourceAddProperty.ERROR_FETCHING_PROPERTY -> showSnackBarMessage(getString(R.string.unknow_error))
            }
        }
    }

    private fun renderAgentDialog(agents: List<Agent>){
        if(!openAgentWindowHandled) {
            val listAgentDialog = ListAgentsDialogView(agents)
            listAgentDialog.setTargetFragment(this, AGENT_LIST_DIALOG_CODE)
            listAgentDialog.show(fragmentManager!!.beginTransaction(), AGENT_LIST_TAG)
        }

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
                                          agentLastName: String){
        val priceToDisplay = when(currentCurrency!!){
            Currency.EURO -> price.toString()
            Currency.DOLLAR -> price.toDollar().toString()
        }
        val surfaceToDisplay = when(currentCurrency!!){
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

        val typeProperty = dropdowPropertyType.text.toString()
        val pictures = listOf<String>()

        viewModel.actionFromIntent(AddPropertyIntent.AddPropertyToDBIntent(
                typeProperty, priceText.toDouble(), surfaceText.toDouble(), roomText.toInt(),
                bedroomText.toInt(), bathroomText.toInt(), descriptionText.text.toString(),
                addressText.text.toString(), neighbourhoodText.text.toString(), onMarketSinceText.text.toString(),
                soldSwithch.isChecked, soldOnText.text.toString(), agentSelectedId, getAmenitiesSelected(), pictures, null,
                activity!!.applicationContext)
        )
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

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }

    //--------------------
    // PICK PICTURE
    //--------------------

    private fun openChoosePhotoSourceDialog(){
        val dialog = BottomSheetDialog(activity!!)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_photo_source, null)
        dialog.setContentView(bottomSheet)
        dialog.show()
        bottomSheet.dialog_photo_pick_icon.setOnClickListener {
            requestPermissionStorage()
            dialog.dismiss()
        }
        bottomSheet.dialog_photo_take_icon.setOnClickListener {
            Log.e("click", "take")
            dialog.dismiss()
        }
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    fun requestPermissionStorage() {
        if (!EasyPermissions.hasPermissions(context!!, PERMS_EXT_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this, getString(R.string.storage_perm_request), RC_IMAGE_PERMS, PERMS_EXT_STORAGE)
            return
        }

        pickPhotoFromLibrary()
    }

    private fun pickPhotoFromLibrary(){
        val intent = Intent().apply {
            action = Intent.ACTION_PICK
            type = IMAGE_ONLY_TYPE
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            /*
            type = IMAGE_ONLY_TYPE
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

             */
        }
        startActivityForResult(intent, RC_CHOOSE_PHOTO)
    }

    private fun addPicturePickedToList(data: Intent){
        val uris = data.clipData
        if(uris != null) {
            for(i in 0 until uris.itemCount){
                val uri = uris.getItemAt(i).uri
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                val uriInternal = bitmap.saveToInternalStorage(
                        activity!!.applicationContext, generateName(), TypeImage.PROPERTY
                )
                listUrlPictures.add(uriInternal.toString())

            }
        } else {
            val uri = data.data
            uri?.let{
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, it)
                val uriInternal = bitmap.saveToInternalStorage(
                        activity!!.applicationContext, generateName(), TypeImage.PROPERTY
                )
                listUrlPictures.add(uriInternal.toString())
            }
        }

        Log.e("list uri", listUrlPictures.toString())

    }



}
