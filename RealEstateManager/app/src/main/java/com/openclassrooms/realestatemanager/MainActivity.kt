package com.openclassrooms.realestatemanager

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil


class MainActivity : AppCompatActivity(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<RFACLabelItem<Int>> {

    @BindView(R.id.main_activity_toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.main_activity_tablayout_viewpager) lateinit var viewPager: ViewPager
    @BindView(R.id.main_activity_tablayout) lateinit var tabLayout: TabLayout
    @BindView(R.id.activity_main_rfal) lateinit var rfaLayout: RapidFloatingActionLayout
    @BindView(R.id.activity_main_rfab) lateinit var rfaButton: RapidFloatingActionButton
    private lateinit var rfabHelper: RapidFloatingActionHelper

    private val listDrawableIconTab = listOf(R.drawable.list_icon, R.drawable.map_icon)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        configureToolbar()
        configureViewPagerAndToolbar()
        configureRapidFloatingActionButton()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_main_activity_currency -> {
                item.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureToolbar(){
        setSupportActionBar(toolbar)
    }

    private fun configureViewPagerAndToolbar(){
        fun setupIconTabLayout(){
            tabLayout.getTabAt(0)?.setIcon(listDrawableIconTab[0])
            tabLayout.getTabAt(1)?.setIcon(listDrawableIconTab[1])
        }

        viewPager.adapter = PageAdapterMainActivity(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_FIXED
        setupIconTabLayout()
    }

    private fun configureRapidFloatingActionButton() {
        val rfaContent = RapidFloatingActionContentLabelList(applicationContext)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)
        val items = mutableListOf<RFACLabelItem<Int>>()
        items.add(RFACLabelItem<Int>()
                .setLabel("Add Property")
                .setResId(R.drawable.home_icon)
                .setIconNormalColor(ContextCompat.getColor(applicationContext, R.color.colorTextPrimary))
                .setIconPressedColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                .setWrapper(0)
        )
        items.add(RFACLabelItem<Int>()

                .setLabel("Add agent")
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

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        Toast.makeText(applicationContext, "clicked label: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        Toast.makeText(applicationContext, "clicked label: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }
}

