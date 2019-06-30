package com.openclassrooms.realestatemanager

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    @BindView(R.id.main_activity_toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.main_activity_tablayout_viewpager) lateinit var viewPager: ViewPager
    @BindView(R.id.main_activity_tablayout) lateinit var tabLayout: TabLayout

    private val listDrawableIconTab = listOf(R.drawable.list_icon, R.drawable.map_icon)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        configureToolbar()
        configureViewPagerAndToolbar()
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
}

