package com.openclassrooms.realestatemanager.listProperties


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.openclassrooms.realestatemanager.utils.displayData
import java.lang.ref.WeakReference

/**
 * A simple [Fragment] subclass.
 *
 */
class ListPropertyView : BaseViewListProperties(),
         MainActivity.OnListPropertiesChangeListener{

    @BindView(R.id.list_property_view_rv) lateinit var recyclerView: RecyclerView
    @BindView(R.id.list_property_view_refresh) lateinit var refreshLayout: SwipeRefreshLayout
    @BindView(R.id.list_property_view_frameLayout) lateinit var frameLayout: FrameLayout

    private var adapter: ListPropertyAdapter? = null

    private var isDoubleScreenMode = false

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
        checkScreenMode()

        return view
    }

    private fun setupRefreshPropertiesListener(){
        if(activity is MainActivity){
            (activity as MainActivity).callbackListPropertiesRefresh = WeakReference(this)
        }
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun renderListProperties(properties: List<PropertyWithAllData>){
        adapter!!.update(properties)
        if(isDoubleScreenMode){
            adapter!!.updateSelection(null)
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
        adapter = ListPropertyAdapter(listOf<PropertyWithAllData>(), currentCurrency, Glide.with(this), isDoubleScreenMode)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        configureClickRecyclerView()
    }

    private fun configureClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.list_agent_dialog_item)
                .setOnItemClickListener{ _, position, _ ->
                    setPropertyPicked(adapter!!.getProperty(position), position)
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

    fun refreshListProperties(){
        updatePropertiesDisplay()
    }

    private fun checkScreenMode(){
        if(activity is MainActivity){
            isDoubleScreenMode = (activity as MainActivity).isDoubleScreenMode
        }
    }

    override fun renderDisplaySelection(itemSelected: Int) {
        displayData("render selectecion $itemSelected")
        adapter!!.updateSelection(itemSelected)
    }
}
