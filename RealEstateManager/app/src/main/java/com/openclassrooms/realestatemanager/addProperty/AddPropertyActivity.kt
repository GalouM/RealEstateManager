package com.openclassrooms.realestatemanager.addProperty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R

class AddPropertyActivity : AppCompatActivity() {

    @BindView(R.id.add_property_activity_toolbar) lateinit var toolbar: Toolbar

    private var addPropertyView: AddPropertyView? = null

    private var currency = "euros"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)
        ButterKnife.bind(this)

        configureToolbar()
        configureAndShowView()
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.close_icon)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_add_property_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_add_property_activity_currency -> {
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

    private fun configureAndShowView(){
        addPropertyView = supportFragmentManager.findFragmentById(R.id.add_property_activity_frame_layout) as AddPropertyView?
        if(addPropertyView == null){
            addPropertyView = AddPropertyView()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.add_property_activity_frame_layout, addPropertyView!!)
                    .commit()
        }
    }
}
