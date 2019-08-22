package com.openclassrooms.realestatemanager.mainActivity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.mapbox.mapboxsdk.Mapbox
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.addAgent.AddAgentActivity
import com.openclassrooms.realestatemanager.addProperty.AddPropertyActivity
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.mviBase.REMView
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.RC_CODE_ADD_AGENT
import com.openclassrooms.realestatemanager.utils.RC_CODE_ADD_PROPERTY
import com.openclassrooms.realestatemanager.utils.showSnackBar
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil


class MainActivity : AppCompatActivity(), REMView<MainActivityViewState>,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<RFACLabelItem<Int>>{

    interface OnClickChangeCurrencyListener{
        fun onChangeCurrency(currency: Currency)
    }

    interface OnListPropertiesChangeListener{
        fun onListPropertiesChange()
    }



    private lateinit var callbackListPropertiesCurrency: OnClickChangeCurrencyListener
    private lateinit var callbackMapPropertiesCurrency: OnClickChangeCurrencyListener
    private lateinit var callbackListPropertiesRefresh: OnListPropertiesChangeListener
    private lateinit var callbackMapPropertiesRefresh: OnListPropertiesChangeListener

    private lateinit var viewModel: MainActivityViewModel

    @BindView(R.id.main_activity_toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.main_activity_tablayout_viewpager) lateinit var viewPager: MainActivityViewPager
    @BindView(R.id.main_activity_tablayout) lateinit var tabLayout: TabLayout
    @BindView(R.id.activity_main_rfal) lateinit var rfaLayout: RapidFloatingActionLayout
    @BindView(R.id.activity_main_rfab) lateinit var rfaButton: RapidFloatingActionButton
    private lateinit var rfabHelper: RapidFloatingActionHelper

    private var menuToolbar: Menu? = null

    private val listDrawableIconTab = listOf(R.drawable.list_icon, R.drawable.map_icon)

    fun setOnClickChangeCurrencyList(callback: OnClickChangeCurrencyListener){
        this.callbackListPropertiesCurrency = callback
    }

    fun setOnClickChangeCurrencyMap(callback: OnClickChangeCurrencyListener){
        this.callbackMapPropertiesCurrency = callback
    }

    fun setListPropertiesChangeList(callback: OnListPropertiesChangeListener){
        this.callbackListPropertiesRefresh = callback
    }

    fun setListPropertiesChangeMap(callback: OnListPropertiesChangeListener){
        this.callbackMapPropertiesRefresh = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, BuildConfig.MapBoxToken)

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        configureViewModel()

        configureToolbar()
        configureViewPagerAndTablayout()
        configureRapidFloatingActionButton()
    }

    override fun onResume() {
        super.onResume()
        viewModel.actionFromIntent(MainActivityIntent.GetCurrentCurrencyIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_CODE_ADD_AGENT){
            if(resultCode == Activity.RESULT_OK){
                showSnackBarMessage(getString(R.string.agent_added))
            }
        }
        if(requestCode == RC_CODE_ADD_PROPERTY){
            if(resultCode == Activity.RESULT_OK){
                showSnackBarMessage(getString(R.string.property_added))
                callbackListPropertiesRefresh.onListPropertiesChange()
                callbackMapPropertiesRefresh.onListPropertiesChange()
            }
        }
    }

    //--------------------
    // CONFIGURE UI
    //--------------------

    //------Toolbar---------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main_activity, menu)
        menuToolbar = menu
        viewModel.actionFromIntent(MainActivityIntent.GetCurrentCurrencyIntent)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_main_activity_currency -> {
                viewModel.actionFromIntent(MainActivityIntent.ChangeCurrencyIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureToolbar(){
        setSupportActionBar(toolbar)
    }

    //------View Pager and tablayout---------

    private fun configureViewPagerAndTablayout(){
        fun setupIconTabLayout(){
            tabLayout.getTabAt(0)?.setIcon(listDrawableIconTab[0])
            tabLayout.getTabAt(1)?.setIcon(listDrawableIconTab[1])
        }

        fun setupTabLayoutListener(){
            tabLayout.addOnTabSelectedListener(object :TabLayout.ViewPagerOnTabSelectedListener(viewPager){
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val tabIconColor = ContextCompat.getColor(applicationContext, R.color.colorTextPrimaryAlpha)
                    tab?.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val tabIconColor = ContextCompat.getColor(applicationContext, R.color.colorTextPrimary)
                    tab?.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                }
            })
            viewPager.currentItem = 1
            viewPager.currentItem = 0
        }

        viewPager.adapter = PageAdapterMainActivity(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_FIXED
        setupIconTabLayout()
        setupTabLayoutListener()
    }

    //------Floating button---------

    private fun configureRapidFloatingActionButton() {
        val rfaContent = RapidFloatingActionContentLabelList(applicationContext)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)
        val items = mutableListOf<RFACLabelItem<Int>>()
        items.add(RFACLabelItem<Int>()
                .setLabel(getString(R.string.add_property_menu))
                .setResId(R.drawable.home_icon)
                .setIconNormalColor(ContextCompat.getColor(applicationContext, R.color.colorTextPrimary))
                .setIconPressedColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                .setWrapper(0)
        )
        items.add(RFACLabelItem<Int>()

                .setLabel(getString(R.string.add_agent_menu))
                .setResId(R.drawable.person_icon)
                .setIconNormalColor(ContextCompat.getColor(applicationContext, R.color.colorTextPrimary))
                .setIconPressedColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                .setWrapper(1)
        )
        rfaContent
                .setItems(items as List<RFACLabelItem<Any>>?)
                .setIconShadowRadius(RFABTextUtil.dip2px(applicationContext, 3F))
                .setIconShadowColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setIconShadowDy(RFABTextUtil.dip2px(applicationContext, 3F))
        rfabHelper = RapidFloatingActionHelper(
                applicationContext,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build()
    }

    //------FAB click listener---------

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        when(position){
            0 -> {
                viewModel.actionFromIntent(MainActivityIntent.OpenAddPropertyActivityIntent)}

            1 -> showAddAgentActivity()
        }
        rfabHelper.toggleContent()
    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        onRFACItemIconClick(position, item)
    }

    //--------------------
    // VIEW MODEL CONNECTION
    //--------------------

    override fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(this)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(MainActivityViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    //--------------------
    // STATE AND INTENT
    //--------------------

    override fun render(viewState: MainActivityViewState?) {
        if (viewState == null) return
        if(viewState.isOpenAddProperty){
            renderShowAddPropertyActivity()
        } else{
            viewState.errorSource?.let { renderErrorOpeningActivity(it) }
        }
        renderChangeCurrency(viewState.currency)

    }

    private fun renderShowAddPropertyActivity(){
        val intent = Intent(this, AddPropertyActivity::class.java)
        startActivityForResult(intent, RC_CODE_ADD_PROPERTY)
    }

    private fun showAddAgentActivity(){
        val intent = Intent(this, AddAgentActivity::class.java)
        startActivityForResult(intent, RC_CODE_ADD_AGENT)
    }

    private fun renderErrorOpeningActivity(errorSource: ErrorSourceMainActivity){
        when(errorSource){
            ErrorSourceMainActivity.NO_AGENT_IN_DB -> showSnackBarMessage(getString(R.string.create_agent_first))
        }

    }

    private fun renderChangeCurrency(currency: Currency){
        menuToolbar?.let {
            when(currency){
                Currency.EURO -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_main_activity_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.euro_icon)
                }
                Currency.DOLLAR -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_main_activity_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                }
            }
            callbackListPropertiesCurrency.onChangeCurrency(currency)
            callbackMapPropertiesCurrency.onChangeCurrency(currency)
        }

    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}

