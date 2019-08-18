package com.openclassrooms.realestatemanager.mainActivity


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.ContentFrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import com.openclassrooms.realestatemanager.utils.showSnackBar

/**
 * A simple [Fragment] subclass.
 *
 */
class ListPropertyView : Fragment(), MainActivity.OnClickChangeCurrencyListener {

    @BindView(R.id.list_property_view_rv) lateinit var recyclerView: RecyclerView
    @BindView(R.id.list_property_view_refresh) lateinit var refreshLayout: SwipeRefreshLayout
    @BindView(R.id.list_property_view_frameLayout) lateinit var frameLayout: FrameLayout

    private var adapter: ListPropertyAdapter? = null
    private var currentCurrency: Currency = Currency.EURO

    private lateinit var viewModel: ListPropertyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_list_property_view, container, false)
        ButterKnife.bind(this, view)
        configureRefreshLayout()
        configureForeground()
        configureViewModel()
        setupCurrencyListener()

        return view
    }

    private fun setupCurrencyListener(){
        if(activity is MainActivity){
            (activity as MainActivity).setOnClickChangeCurrencyList(this)
        }
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

        viewModel.actionFromIntent(PropertyListIntent.DisplayPropertiesIntent)
    }

    private fun render(viewState: PropertyListViewState?) {
        if (viewState == null) return

        viewState.listProperties?.let {
            renderListProperties(viewState.listProperties)
        }

        viewState.errorSource?.let { renderErrorFetchingProperty(it) }

        if(viewState.isLoading) renderIsLoading()

    }

    private fun renderListProperties(properties: List<PropertyForListDisplay>){
        turnOffLoading()
        configureRecyclerView(properties)
        configureClickRecyclerView()
    }

    private fun renderErrorFetchingProperty(errorSource: ErrorSourceListProperty){
        turnOffLoading()
        when(errorSource){
            ErrorSourceListProperty.NO_PROPERTY_IN_DB -> showSnackBarMessage(getString(R.string.no_properties))
            ErrorSourceListProperty.CAN_T_ACCESS_DB -> showSnackBarMessage(getString(R.string.unknow_error))
        }
    }

    private fun renderIsLoading(){
        frameLayout.foreground.alpha = 50
    }

    private fun configureRecyclerView(properties: List<PropertyForListDisplay>){
        adapter = ListPropertyAdapter(properties, currentCurrency, Glide.with(this))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun configureClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.list_agent_dialog_item)
                .setOnItemClickListener{ _, position, _ ->  setPropertySelected(adapter!!.getProperty(position))}
    }

    private fun setPropertySelected(id: Int){

    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }

    override fun onChangeCurrency(currency: Currency) {
        currentCurrency = currency
        adapter?.updateCurrency(currentCurrency)
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
