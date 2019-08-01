package com.openclassrooms.realestatemanager.addAgent


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 *
 */
class AddAgentView : Fragment() {

    private lateinit var toolbar: Toolbar
    @BindView(R.id.add_agent_view_firstname) lateinit var firstName: EditText
    @BindView(R.id.add_agent_view_lastname) lateinit var lastName: EditText
    @BindView(R.id.add_agent_view_email) lateinit var email: EditText
    @BindView(R.id.add_agent_view_phonenb) lateinit var phoneNumber: EditText
    @BindView(R.id.add_agent_view_firstname_layout) lateinit var firstNameLayout: TextInputLayout
    @BindView(R.id.add_agent_view_lastname_layout) lateinit var lastNameLayout: TextInputLayout
    @BindView(R.id.add_agent_view_phonenb_layout) lateinit var phoneNumberLayout: TextInputLayout
    @BindView(R.id.add_agent_view_email_layout) lateinit var emailLayout: TextInputLayout

    private lateinit var viewModel: AddAgentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_agent_view, container, false)
        ButterKnife.bind(this, view)

        toolbar = activity!!.findViewById(R.id.add_agent_activity_toolbar)

        configureViewModel()
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_agent_activity_check -> {
                Log.e("tag", "click toolbar detected")
                viewModel.actionFromIntent(AddAgentIntent.AddAgentToDBIntent(
                        null,
                        firstName.text.toString(),
                        lastName.text.toString(),
                        email.text.toString(),
                        phoneNumber.text.toString()
                ))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun clickListenerToolbar(){
        Log.e("tag", "click toolbar detected Frag")
        disableAllErrors()
        viewModel.actionFromIntent(AddAgentIntent.AddAgentToDBIntent(
                null,
                firstName.text.toString(),
                lastName.text.toString(),
                email.text.toString(),
                phoneNumber.text.toString()
        ))
    }

    private fun configureViewModel(){
        val viewModelFactory = Injection.providesViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(AddAgentViewModel::class.java)

        viewModel.viewState.observe(this, Observer { render(it) })
    }

    private fun render(viewState: AddAgentViewState?){
        if (viewState == null) return
        Log.e("tag", viewState.toString())
        if(viewState.isSaved) {
            Log.e("tag", "result received view")
            activity!!.finish()
        }
        if(viewState.errors != null){
            displayErrors(viewState.errors)
            Log.e("erros", viewState.errors.toString())
        }
    }

    private fun displayErrors(errors: List<ErrorSourceAddAgent>){
        errors.forEach{
            when(it){
                ErrorSourceAddAgent.FIRST_NAME_INCORRECT -> firstNameLayout.error = "Incorrect First Name"
                ErrorSourceAddAgent.LAST_NAME_INCORRECT -> lastNameLayout.error = "Incorrect Last Name"
                ErrorSourceAddAgent.EMAIL_INCORRECT -> emailLayout.error = "Incorrect Email"
                ErrorSourceAddAgent.PHONE_INCORRECT -> phoneNumberLayout.error = "Incorrect Phone Number"
                ErrorSourceAddAgent.UNKNOW_ERROR -> Log.e("tag", "unknown error")
            }
        }
    }

    private fun disableAllErrors(){
        firstNameLayout.isErrorEnabled = false
        lastNameLayout.isErrorEnabled = false
        emailLayout.isErrorEnabled = false
        phoneNumberLayout.isErrorEnabled = false
    }
}
