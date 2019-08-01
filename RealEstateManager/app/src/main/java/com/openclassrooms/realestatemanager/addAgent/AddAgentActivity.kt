package com.openclassrooms.realestatemanager.addAgent

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R

class AddAgentActivity : AppCompatActivity() {

    @BindView(R.id.add_agent_activity_toolbar) lateinit var toolbar: Toolbar

    private var addAgentView: AddAgentView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_agent)
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
        menuInflater.inflate(R.menu.menu_toolbar_add_agent_activity, menu)
        return true
    }

    private fun configureAndShowView(){
        addAgentView = supportFragmentManager.findFragmentById(R.id.add_agent_activity_frame_layout) as AddAgentView?
        if(addAgentView == null){
            addAgentView = AddAgentView()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.add_agent_activity_frame_layout, addAgentView!!)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_agent_activity_check -> {
                Log.e("tag", "click toolbar detected Activity")
                addAgentView!!.clickListenerToolbar()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
