package com.openclassrooms.realestatemanager.addProperty

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.ACTION_TYPE
import com.openclassrooms.realestatemanager.utils.Currency

class AddPropertyActivity : AppCompatActivity(), AddPropertyView.OnCurrencyChangedListener {

    @BindView(R.id.add_property_activity_toolbar) lateinit var toolbar: Toolbar

    private var addPropertyView: AddPropertyView? = null

    private var menuToolbar: Menu? = null

    private lateinit var actionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)
        ButterKnife.bind(this)

        configureActionType()
        configureToolbar()
        configureAndShowView()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if(fragment is AddPropertyView){
            fragment.setOnCurrencyChangedListener(this)
        }
    }

    private fun configureActionType(){
        actionType = intent.getStringExtra(ACTION_TYPE)
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.close_icon)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_add_property_activity, menu)
        menuToolbar = menu
        addPropertyView?.configureCurrentCurrency()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) finish()
        addPropertyView!!.toolBarClickListener(item?.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun configureAndShowView(){
        addPropertyView = supportFragmentManager.findFragmentById(R.id.add_property_activity_frame_layout) as AddPropertyView?
        if(addPropertyView == null){
            addPropertyView = AddPropertyView.newInstance(actionType)
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.add_property_activity_frame_layout, addPropertyView!!)
                    .commit()
        }
    }

    override fun onClickCurrency(currency: Currency) {
        menuToolbar?.let{
            when(currency){
                Currency.EURO -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_add_property_activity_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.euro_icon)
                }
                Currency.DOLLAR -> {
                    val currencyItem = menuToolbar!!.findItem(R.id.menu_add_property_activity_currency)
                    currencyItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.dollar_icon)
                }
            }
        }

    }
}
