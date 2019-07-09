package com.openclassrooms.realestatemanager.mainActivity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.internal.ListenerClass
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.addAgent.AddAgentActivity
import com.openclassrooms.realestatemanager.addProperty.AddPropertyActivity
import com.openclassrooms.realestatemanager.mviBase.MviView
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil
import dagger.android.DispatchingAndroidInjector
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class MainActivity : AppCompatActivity(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<RFACLabelItem<Int>>,
MviView<MainActivityIntent, MainActivityViewState> {

    @BindView(R.id.main_activity_toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.main_activity_tablayout_viewpager) lateinit var viewPager: ViewPager
    @BindView(R.id.main_activity_tablayout) lateinit var tabLayout: TabLayout
    @BindView(R.id.activity_main_rfal) lateinit var rfaLayout: RapidFloatingActionLayout
    @BindView(R.id.activity_main_rfab) lateinit var rfaButton: RapidFloatingActionButton
    private lateinit var rfabHelper: RapidFloatingActionHelper

    private val openAddPropertyIntentPublisher = PublishSubject.create<MainActivityIntent.OpenAddPropertyActivityIntent>()



    lateinit var viewModel: MainActivityViewModel by lazy(ListenerClass.NONE){
        ViewModelProviders.of(this)
    }

    private var currency = "euros"

    private val listDrawableIconTab = listOf(R.drawable.list_icon, R.drawable.map_icon)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        configureToolbar()
        configureViewPagerAndTablayout()
        configureRapidFloatingActionButton()
    }

    //--------------------
    // CONFIGURE UI
    //--------------------

    //------Toolbar---------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_main_activity_currency -> {
                when(currency){
                    "euros" -> {
                        item.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                        currency = "dollars"
                        return true
                    }
                    "dollars" -> {
                        item.icon = ContextCompat.getDrawable(applicationContext, R.drawable.euro_icon)
                        currency = "euros"
                        return true
                    }
                }
                item.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
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
            0 -> openAddPropertyIntentPublisher.onNext(MainActivityIntent.OpenAddPropertyActivityIntent)

            1 -> showAddAgentActivity()
        }
        rfabHelper.toggleContent()
    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        onRFACItemIconClick(position, item)
    }

    override fun intents(): Observable<MainActivityIntent> {
        return Observable.merge(initialIntent(),
                openAddPropertyIntent())
    }

    //--------------------
    // SATE AND INTENT
    //--------------------

    override fun render(state: MainActivityViewState) {
        when{
            state.isOpenAddProperty -> renderShowAddPropertyActivity()
            state.isError -> state.errorSource?.let { renderErrorOpeningActivity(it) }
        }
    }

    private fun initialIntent(): Observable<MainActivityIntent.InitialIntent>{
        return Observable.just(MainActivityIntent.InitialIntent)
    }

    private fun openAddPropertyIntent(): Observable<MainActivityIntent.OpenAddPropertyActivityIntent>{
        return openAddPropertyIntentPublisher
    }

    private fun renderShowAddPropertyActivity(){
        val intent = Intent(this, AddPropertyActivity::class.java)
        startActivity(intent)
    }

    private fun showAddAgentActivity(){
        val intent = Intent(this, AddAgentActivity::class.java)
        startActivity(intent)
    }

    private fun renderErrorOpeningActivity(errorSource: ErrorSource){
        when(errorSource){
            ErrorSource.NO_AGENT_IN_DB -> Log.e("message", "no agent in db")
            else -> Log.e("message", "unknow error")
        }

    }
}

