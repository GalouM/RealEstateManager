package com.openclassrooms.realestatemanager

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife

class MainActivity : AppCompatActivity() {

    @BindView(R.id.main_activity_toolbar) lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        configureToolbar()
    }

    private fun configureToolbar(){
        setSupportActionBar(toolbar)
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
}

