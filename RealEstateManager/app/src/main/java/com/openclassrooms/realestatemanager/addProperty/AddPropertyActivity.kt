package com.openclassrooms.realestatemanager.addProperty

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyActivity
import com.openclassrooms.realestatemanager.baseCurrency.BaseCurrencyIntent
import com.openclassrooms.realestatemanager.utils.ACTION_TYPE_ADD_PROPERTY
import com.openclassrooms.realestatemanager.utils.Currency

class AddPropertyActivity : BaseCurrencyActivity<AddPropertyView>() {

    private lateinit var actionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindLayout()
        configureActionType()
        configureViewModel()
        configureToolbar(R.drawable.close_icon)
        configureAndShowView()
    }

    private fun configureActionType(){
        actionType = intent.getStringExtra(ACTION_TYPE_ADD_PROPERTY)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_validate_button, menu)
        menuToolbar = menu
        viewModel.actionFromIntent(BaseCurrencyIntent.GetCurrentCurrencyIntent)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_toolbar_currency -> {
                viewModel.actionFromIntent(BaseCurrencyIntent.ChangeCurrencyIntent)
                return true
            }
            R.id.menu_validate_activity_check -> {
                view!!.toolBarValidateClickListener()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun createNewView(): AddPropertyView {
        return AddPropertyView.newInstance(actionType)
    }
}
