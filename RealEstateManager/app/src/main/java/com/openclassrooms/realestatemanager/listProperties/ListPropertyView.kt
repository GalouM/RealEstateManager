package com.openclassrooms.realestatemanager.listProperties


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import com.openclassrooms.realestatemanager.mainActivity.MainActivity
import com.openclassrooms.realestatemanager.utils.ACTION_TYPE_LIST_PROPERTY
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.ItemClickSupport

/**
 * A simple [Fragment] subclass.
 *
 */
class ListPropertyView : BaseViewListProperties(),
         MainActivity.OnListPropertiesChangeListener {

    @BindView(R.id.list_property_view_rv) lateinit var recyclerView: RecyclerView
    @BindView(R.id.list_property_view_refresh) lateinit var refreshLayout: SwipeRefreshLayout
    @BindView(R.id.list_property_view_frameLayout) lateinit var frameLayout: FrameLayout

    private var adapter: ListPropertyAdapter? = null

    companion object {

        fun newInstance(actionType: String) = ListPropertyView().apply {
            arguments = bundleOf(ACTION_TYPE_LIST_PROPERTY to actionType)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_list_property_view, container, false)
        ButterKnife.bind(this, view)
        configureRefreshLayout()
        configureForeground()
        configureRecyclerView()
        configureViewModel()
        configureActionType()
        currencyObserver()
        setupRefreshPropertiesListener()

        return view
    }

    private fun setupRefreshPropertiesListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setListPropertiesChangeList(this)
        }
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun renderListProperties(properties: List<PropertyWithAllData>){
        adapter!!.update(properties)
        if(activity is MainActivity){
            if(!(activity as MainActivity).isDoubleScreenMode){
                adapter!!.updateSelection(null)

            }
        }
    }

    override fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty){
        when(errorSource){
            ErrorSourceListProperty.NO_PROPERTY_IN_DB -> showSnackBarMessage(getString(R.string.no_properties))
            ErrorSourceListProperty.CAN_T_ACCESS_DB -> showSnackBarMessage(getString(R.string.unknow_error))
        }
    }

    override fun renderIsLoading(loading: Boolean){
        if(loading) {
            frameLayout.foreground.alpha = 50
        } else {
            frameLayout.foreground.alpha = 0
            refreshLayout.isRefreshing = false
        }
    }

    //--------------------
    // RECYCLERVIEW
    //--------------------

    private fun configureRecyclerView(){
        var isDoubleScreen = false
        if(activity is MainActivity){
            isDoubleScreen = (activity as MainActivity).isDoubleScreenMode
        }
        adapter = ListPropertyAdapter(listOf<PropertyWithAllData>(), currentCurrency, Glide.with(this), isDoubleScreen)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        configureClickRecyclerView()
    }

    private fun configureClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.list_agent_dialog_item)
                .setOnItemClickListener{ _, position, _ ->
                    setPropertyPicked(adapter!!.getProperty(position))
                    adapter!!.updateSelection(position)
                }
    }

    //--------------------
    // UPDATE UI FROM MAIN ACTIVITY ACTIONS
    //--------------------

    override fun renderChangeCurrency(currency: Currency) {
        adapter?.updateCurrency(currency)
    }

    override fun onListPropertiesChange() {
        refreshListProperties()
    }

    //--------------------
    // REFRESH VIEW
    //--------------------

    private fun configureRefreshLayout(){
        refreshLayout.setOnRefreshListener(this::refreshListProperties)
    }

    private fun configureForeground(){
        frameLayout.foreground = ColorDrawable(Color.BLACK)
        frameLayout.foreground.alpha = 0
    }

    private fun refreshListProperties(){
        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

}
