package com.openclassrooms.realestatemanager.listProperties


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.mainActivity.MainActivity
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.ItemClickSupport

/**
 * A simple [Fragment] subclass.
 *
 */
class ListPropertyView : BaseViewListProperties(),
        MainActivity.OnClickChangeCurrencyListener, MainActivity.OnListPropertiesChangeListener {

    @BindView(R.id.list_property_view_rv) lateinit var recyclerView: RecyclerView
    @BindView(R.id.list_property_view_refresh) lateinit var refreshLayout: SwipeRefreshLayout
    @BindView(R.id.list_property_view_frameLayout) lateinit var frameLayout: FrameLayout

    private var adapter: ListPropertyAdapter? = null
    private var currentCurrency: Currency = Currency.EURO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_list_property_view, container, false)
        ButterKnife.bind(this, view)
        configureRefreshLayout()
        configureForeground()
        configureViewModel()
        setupCurrencyListener()
        setupRefreshPropertiesListener()

        return view
    }

    private fun setupCurrencyListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setOnClickChangeCurrencyList(this)
        }
    }

    private fun setupRefreshPropertiesListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setListPropertiesChangeList(this)
        }
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun renderListProperties(properties: List<PropertyForListDisplay>){
        turnOffLoading()
        configureRecyclerView(properties)
        configureClickRecyclerView()
    }

    override fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty){
        turnOffLoading()
        when(errorSource){
            ErrorSourceListProperty.NO_PROPERTY_IN_DB -> showSnackBarMessage(getString(R.string.no_properties))
            ErrorSourceListProperty.CAN_T_ACCESS_DB -> showSnackBarMessage(getString(R.string.unknow_error))
        }
    }

    override fun renderIsLoading(){
        frameLayout.foreground.alpha = 50
    }

    private fun configureRecyclerView(properties: List<PropertyForListDisplay>){
        adapter = ListPropertyAdapter(properties, currentCurrency, Glide.with(this))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun configureClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.list_agent_dialog_item)
                .setOnItemClickListener{ _, position, _ ->  openPropertyDetails(adapter!!.getProperty(position))}
    }

    override fun onChangeCurrency(currency: Currency) {
        currentCurrency = currency
        adapter?.updateCurrency(currentCurrency)
    }

    override fun onListPropertiesChange() {
        onRefreshLayout()
    }

    private fun configureRefreshLayout(){
        refreshLayout.setOnRefreshListener(this::onRefreshLayout)
    }

    private fun configureForeground(){
        frameLayout.foreground = ColorDrawable(Color.BLACK)
        frameLayout.foreground.alpha = 0
    }

    private fun onRefreshLayout(){
        frameLayout.foreground.alpha = 50
        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    private fun turnOffLoading(){
        frameLayout.foreground.alpha = 0
        refreshLayout.isRefreshing = false
    }
}
