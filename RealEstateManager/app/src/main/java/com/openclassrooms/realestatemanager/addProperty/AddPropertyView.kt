package com.openclassrooms.realestatemanager.addProperty


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.addProperty.ErrorSourceAddProperty.*
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.BaseFragmentREM
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.Currency.DOLLAR
import com.openclassrooms.realestatemanager.utils.Currency.EURO
import com.openclassrooms.realestatemanager.utils.TypeAmenity.*
import com.openclassrooms.realestatemanager.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_photo_source.view.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class AddPropertyView : BaseFragmentREM(), REMView<AddPropertyViewState>,
        PickDateDialogView.OnOkButtonListener, ListAgentsDialogView.OnAgentSelected, ListPictureAdapter.Listener,
SnackBarListener{

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
    @BindView(R.id.add_property_view_picture_rv) lateinit var recyclerViewPictures: RecyclerView

    private lateinit var viewModel: AddPropertyViewModel
    private var currentCurrency: Currency? = null
    private lateinit var actionType: ActionType

    private var openAgentWindowHandled = true

    private var packageManager: PackageManager? = null
    private var lastPhotoTakenPath: String? = null

    private lateinit var adapter: ListPictureAdapter

    private val itemTouchHelper by lazy{
        val touchCallback = object : ItemTouchHelper.SimpleCallback(UP or DOWN, START or END){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val adapter = recyclerView.adapter as ListPictureAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                viewModel.actionFromIntent(AddPropertyIntent.MovePictureInListPositionIntent(from, to))
                adapter.notifyItemMoved(from, to)
                adapter.updateForegroundViewHolder()

                return true
            }
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if(actionState == ACTION_STATE_DRAG){
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val toDelete = (viewHolder as ListPictureViewHolder).picture
                viewModel.actionFromIntent(AddPropertyIntent.RemovePictureFromListIntent(toDelete))
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        }

        ItemTouchHelper(touchCallback)
    }

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
        configureTypeDropdownOptions()
        configureRecyclerViewPictures()
        configureSellDateVisibility()
        configureActionType()
        showWarningMessage()
        currencyObserver()

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
            RC_CODE_TAKE_PHOTO -> {
                if(resultCode == RESULT_OK){
                    addPictureTakenToList()
                }
            }
        }
    }

    private fun showWarningMessage(){
        if(!isInternetAvailable(activity!!)){
            showSnackBarMessage(getString(R.string.offline_mode_message))
        }
    }

    //--------------------
    // CLICK LISTENER
    //--------------------

    fun toolBarValidateClickListener(){
        disableAllErrors()

        if(loading) return

        if(isInternetAvailable(activity!!)){
            emitSaveToDBIntent()
        } else {
            emitSaveDraftIntent()
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
        openAgentWindowHandled = false
        viewModel.actionFromIntent(AddPropertyIntent.OpenListAgentsIntent)
    }

    override fun onAgentSelected(agent: Agent) {
        val displayNameAgent = "${agent.firstName} ${agent.lastName}"
        dropdowAgent.setText(displayNameAgent)
        viewModel.actionFromIntent(AddPropertyIntent.SelectAgentIntent(agent.id))
        openAgentWindowHandled = true
    }

    @OnClick(R.id.add_property_view_sold_switch)
    fun clickSoldButtonListener(){
        configureSellDateVisibility()

    }

    @OnClick(R.id.add_property_view_add_picture_button)
    fun onClickAddPicture(){
        requestPermissionStorage(this)
        if(requestPermissionStorage(this)){
            openChoosePhotoSourceDialog()
        }
    }

    override fun onClickDeleteButton(picture: Picture) {
        viewModel.actionFromIntent(AddPropertyIntent.RemovePictureFromListIntent(picture))
    }

    override fun onDragItemRV(viewHolder: ListPictureViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onSnackBarButtonClick(action: SnackBarAction) {
        when(action){
            SnackBarAction.SHOW_ORIGINAL -> viewModel.actionFromIntent(AddPropertyIntent.DisplayDataFromDB)
            SnackBarAction.SAVE_DRAFT -> emitSaveDraftIntent()
        }
    }

    //--------------------
    // CONFIGURE UI
    //--------------------

    private fun configureSellDateVisibility(){
        if(soldSwithch.isChecked){
            soldOnText.visibility = View.VISIBLE
            soldOnLayout.visibility = View.VISIBLE
        } else {
            soldOnText.visibility = View.INVISIBLE
            soldOnLayout.visibility = View.INVISIBLE
        }
    }

    private fun configureTypeDropdownOptions(){
        val propertyType = mutableListOf<String>()
        TypeProperty.values().forEach {
            propertyType.add(it.typeName)
        }

        ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, propertyType)
                .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dropdowPropertyType.setAdapter(adapter) }
    }

    private fun configureRecyclerViewPictures(){
        adapter = ListPictureAdapter(listOf<Picture>(), Glide.with(this), this, this)
        recyclerViewPictures.adapter = adapter
        recyclerViewPictures.layoutManager = LinearLayoutManager(activity)
        itemTouchHelper.attachToRecyclerView(recyclerViewPictures)

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
        viewModel.viewEffect.observe(this, Observer { takeActionOn(it) })
    }

    private fun currencyObserver(){
        viewModel.currency.observe(this, Observer {currency ->
            currentCurrency = currency
            renderChangeCurrency(currentCurrency!!)
            viewModel.actionFromIntent(AddPropertyIntent.InitialIntent(actionType))
        })
    }

    private fun configureActionType(){
        val argument = arguments?.getString(ACTION_TYPE_ADD_PROPERTY, "")
        argument?.let{
            actionType = ActionType.valueOf(it)
        }

    }

    private fun emitSaveToDBIntent(){
        val typeProperty = dropdowPropertyType.text.toString()
        val soldOn = if(soldSwithch.isChecked) soldOnText.text.toString() else null
        viewModel.actionFromIntent(AddPropertyIntent.AddPropertyToDBIntent(
                typeProperty, priceText.toDouble(), surfaceText.toDouble(), roomText.toInt(),
                bedroomText.toInt(), bathroomText.toInt(), descriptionText.text.toString(),
                addressText.text.toString(), neighbourhoodText.text.toString(), onMarketSinceText.text.toString(),
                soldSwithch.isChecked, soldOn, getAmenitiesSelected(), activity!!.applicationContext
        )
        )
    }

    private fun emitSaveDraftIntent(){
        val typeProperty = dropdowPropertyType.text.toString()
        val soldOn = if(soldSwithch.isChecked) soldOnText.text.toString() else null
        viewModel.actionFromIntent(AddPropertyIntent.SaveDraftIntent(
                typeProperty, priceText.toDouble(), surfaceText.toDouble(), roomText.toInt(),
                bedroomText.toInt(), bathroomText.toInt(), descriptionText.text.toString(),
                addressText.text.toString(), neighbourhoodText.text.toString(), onMarketSinceText.text.toString(),
                soldSwithch.isChecked, soldOn, getAmenitiesSelected()
        ))
    }

    private fun addPicturePickedToList(data: Intent){
        getPicturesPathFromData(data).forEach {
            viewModel.actionFromIntent(AddPropertyIntent.AddPictureToListIntent(it, null))
        }
    }

    private fun addPictureTakenToList(){
        lastPhotoTakenPath?.let {
            viewModel.actionFromIntent(AddPropertyIntent.AddPictureToListIntent(it, getThumbnailFromPicture(it, activity!!)))
            addPictureToGallery(activity!!, it)
        }
    }

    override fun onPictureDescriptionEntered(position: Int, description: String) {
        viewModel.actionFromIntent(AddPropertyIntent.AddDescriptionToPicture(position, description))
    }


    //--------------------
    // STATE AND INTENT
    //--------------------

    override fun render(state: AddPropertyViewState?){
        if (state == null) return
        if(state.isSavedToDB) {
            renderPropertyAddedToDB()
            return
        }

        if(state.isSavedToDraft){
            renderPropertyAddedToDraft()
            return
        }

        renderLoading(state.isLoading)

        if(state.errors != null){
            renderErrors(state.errors)
            return
        }

        state.listAgents?.let{
            renderAgentDialog(it)
        }

        state.pictures?.let { renderPictures(it) }


    }

    private fun renderChangeCurrency(currency: Currency){
        when(currency){
            EURO -> {
                surfaceLayout.hint = getString(R.string.surface_m2)
                priceLayout.hint = getString(R.string.price_euros)
            }
            DOLLAR -> {
                surfaceLayout.hint = getString(R.string.surface_ft2)
                priceLayout.hint = getString(R.string.price_dollar)
            }
        }
    }


    private fun renderErrors(errors: List<ErrorSourceAddProperty>){
        errors.forEach{
            when(it){
                NO_TYPE_SELECTED -> typeLayout.error = getString(R.string.incorrect_type)
                NO_PRICE -> priceLayout.error = getString(R.string.can_t_be_empty)
                NO_SURFACE -> surfaceLayout.error = getString(R.string.can_t_be_empty)
                NO_ROOMS -> roomLayout.error = getString(R.string.can_t_be_empty)
                NO_ADDRESS -> addressLayout.error = getString(R.string.can_t_be_empty)
                NO_NEIGHBORHOOD -> neighbourhoodLayout.error = getString(R.string.can_t_be_empty)
                NO_ON_MARKET_DATE -> onMarketSinceLayout.error = getString(R.string.can_t_be_empty)
                NO_SOLD_DATE -> soldOnLayout.error = getString(R.string.can_t_be_empty)
                NO_AGENT -> agentLayout.error = getString(R.string.can_t_be_empty)
                INCORRECT_SOLD_DATE -> soldOnLayout.error = getString(R.string.incorrect_date)
                INCORRECT_ON_MARKET_DATE -> onMarketSinceLayout.error = getString(R.string.incorrect_date)
                ERROR_FETCHING_AGENTS -> showSnackBarMessage(getString(R.string.error_finding_agents))
                TOO_MANY_ADDRESS -> addressLayout.error = getString(R.string.incorrect_address)
                INCORECT_ADDRESS -> addressLayout.error = getString(R.string.incorrect_address)
                UNKNOW_ERROR -> showSnackBarMessage(getString(R.string.unknow_error))
                ERROR_FETCHING_PROPERTY -> showSnackBarMessage(getString(R.string.unknow_error))
                MISSING_DESCRIPTION -> adapter.showErrorViewHolder(getString(R.string.no_description))
                ERROR_FETCHING_MAP -> showSnackBarMessage(getString(R.string.error_update_server))
                UPLOAD_PICTURE -> showSnackBarMessage(getString(R.string.error_update_server))
                UPLOAD_DATA -> showSnackBarMessage(getString(R.string.error_update_server))
            }
        }
        showSnackBarWithAction(getString(R.string.error_saving), SnackBarAction.SAVE_DRAFT)
    }

    private fun renderAgentDialog(agents: List<Agent>){
        if(!openAgentWindowHandled) {
            val listAgentDialog = ListAgentsDialogView(agents)
            listAgentDialog.setTargetFragment(this, AGENT_LIST_DIALOG_CODE)
            listAgentDialog.show(fragmentManager!!.beginTransaction(), AGENT_LIST_TAG)
        }

    }

    private fun renderPropertyAddedToDB(){
        activity!!.setResult(RESULT_SAVED_TO_DB)
        activity!!.finish()
    }

    private fun renderPropertyAddedToDraft(){
        activity!!.setResult(RESULT_SAVED_TO_DRAFT)
        activity!!.finish()
    }

    private fun renderPictures(pictures: List<Picture>){
        adapter.update(pictures)
        recyclerViewPictures.scrollToPosition(pictures.size - 1)
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

    //--------------------
    // VIEW EFFECT
    //--------------------
    private fun takeActionOn(viewEffect: AddPropertyViewEffect?){
        if (viewEffect == null) return
        if(currentCurrency == null) return
        when(viewEffect){
            is AddPropertyViewEffect.PropertyFromDBEffect -> {
                renderDataFetchedProperty(
                        viewEffect.type, viewEffect.price, viewEffect.surface, viewEffect.rooms,
                        viewEffect.bedrooms, viewEffect.bathrooms, viewEffect.description, viewEffect.address,
                        viewEffect.neighborhood, viewEffect.onMarketSince, viewEffect.isSold, viewEffect.sellDate,
                        viewEffect.amenities!!, viewEffect.agentFirstName, viewEffect.agentLastName
                )
            }
            is AddPropertyViewEffect.PropertyfromDraftEffect -> {
                renderDataFetchedProperty(
                        viewEffect.type, viewEffect.price, viewEffect.surface, viewEffect.rooms,
                        viewEffect.bedrooms, viewEffect.bathrooms, viewEffect.description, viewEffect.address,
                        viewEffect.neighborhood, viewEffect.onMarketSince, viewEffect.isSold, viewEffect.sellDate,
                        viewEffect.amenities!!, viewEffect.agentFirstName, viewEffect.agentLastName
                )
                renderShowingADraft(viewEffect.isOriginalAvailable)
            }
        }
    }

    private fun renderDataFetchedProperty(type: String, price: Double?,
                                          surface: Double?, rooms: Int?,
                                          bedrooms: Int?, bathrooms: Int?,
                                          description: String?, address: String,
                                          neighborhood: String?, onMarketSince: String,
                                          isSold: Boolean?, sellOn: String?,
                                          amenities: List<TypeAmenity>, agentFirstName: String,
                                          agentLastName: String
    ){
        val priceToDisplay = when(currentCurrency!!){
            EURO -> price?.toString()
            DOLLAR -> price?.toDollar().toString()
        }
        val surfaceToDisplay = when(currentCurrency!!){
            EURO -> surface?.toString()
            DOLLAR -> surface?.toSqFt().toString()
        }
        priceToDisplay?.let{ priceText.setText(it) }
        surfaceToDisplay?.let{ surfaceText.setText(it) }
        rooms?.let{ roomText.setText(it.toString()) }
        bedrooms?.let { bedroomText.setText(it.toString()) }
        bathrooms?.let { bathroomText.setText(it.toString()) }
        description?.let{ descriptionText.setText(it)}
        addressText.setText(address)
        neighborhood?.let { neighbourhoodText.setText(it) }
        soldSwithch.isChecked = isSold ?: false
        configureSellDateVisibility()
        sellOn?.let{
            soldOnText.setText(sellOn)
        }
        onMarketSinceText.setText(onMarketSince)
        dropdowPropertyType.setText(type)
        val displayNameAgent = "$agentFirstName $agentLastName"
        dropdowAgent.setText(displayNameAgent)
        amenities.forEach {
            when(it){
                SCHOOL -> schoolBox.isChecked = true
                PLAYGROUND -> playgroundBox.isChecked = true
                SHOP -> shopBox.isChecked = true
                BUSES -> busesBox.isChecked = true
                SUBWAY -> subwayBox.isChecked = true
                PARK -> parkBox.isChecked = true
            }
        }

    }

    private fun renderShowingADraft(originalAvailable: Boolean){
        if(originalAvailable){
            showSnackBarWithAction(getString(R.string.seeing_draft), SnackBarAction.SHOW_ORIGINAL)
        }
    }


    //--------------------
    // UTILS
    //--------------------
    private fun getAmenitiesSelected(): List<TypeAmenity>{
        val listAmenities = mutableListOf<TypeAmenity>()
        if(schoolBox.isChecked) listAmenities.add(SCHOOL)
        if(parkBox.isChecked) listAmenities.add(PARK)
        if(busesBox.isChecked) listAmenities.add(BUSES)
        if(subwayBox.isChecked) listAmenities.add(SUBWAY)
        if(shopBox.isChecked) listAmenities.add(SHOP)
        if(playgroundBox.isChecked) listAmenities.add(PLAYGROUND)

        return listAmenities
    }

    //--------------------
    // WARNING MESSAGE
    //--------------------

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<CoordinatorLayout>(R.id.base_activity_main_layout)
        showSnackBar(viewLayout, message)

    }

    private fun showSnackBarWithAction(message: String, action: SnackBarAction){
        val viewLayout = activity!!.findViewById<CoordinatorLayout>(R.id.base_activity_main_layout)
        showSnackBarWithAction(viewLayout, message, this, action)
    }

    //--------------------
    // PICK PICTURE
    //--------------------

    private fun openChoosePhotoSourceDialog(){
        packageManager = activity!!.packageManager
        val hasCamera = packageManager!!.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        if(hasCamera) {
            val bottomSheet = layoutInflater.inflate(R.layout.dialog_photo_source, null)
            val dialog = BottomSheetDialog(activity!!).apply {
                setContentView(bottomSheet)
                show()
            }
            bottomSheet.dialog_photo_pick_icon.setOnClickListener {
                pickPhotoFromLibrary()
                dialog.dismiss()
            }
            bottomSheet.dialog_photo_take_icon.setOnClickListener {
                takePictureWithCamera()
                dialog.dismiss()
            }
        } else {
            pickPhotoFromLibrary()
        }
    }

    private fun pickPhotoFromLibrary(){
        startActivityForResult(intentSeveralPicture(), RC_CHOOSE_PHOTO)
    }

    private fun takePictureWithCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager!!)?.also {
                val photoFile: File? = try {
                    createImageFileInExtStorage().apply {
                        lastPhotoTakenPath = absolutePath
                    }
                } catch (e: IOException){
                    Log.e("error", e.toString())
                    null
                }
                photoFile?.also {
                    val photoUri = FileProvider.getUriForFile(
                            activity!!, "${BuildConfig.APPLICATION_ID}.fileprovider", it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, RC_CODE_TAKE_PHOTO)
                }

            }
        }
    }

}
