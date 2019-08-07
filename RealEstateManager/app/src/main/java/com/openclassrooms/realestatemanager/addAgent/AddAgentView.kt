package com.openclassrooms.realestatemanager.addAgent


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.utils.PERMS_EXT_STORAGE
import com.openclassrooms.realestatemanager.utils.RC_CHOOSE_PHOTO
import com.openclassrooms.realestatemanager.utils.RC_IMAGE_PERMS
import com.openclassrooms.realestatemanager.utils.showSnackBar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

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
    @BindView(R.id.add_agent_view_picture_agent) lateinit var profilePicture: ImageView

    private lateinit var viewModel: AddAgentViewModel
    private var uriProfileImage: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_agent_view, container, false)
        ButterKnife.bind(this, view)

        toolbar = activity!!.findViewById(R.id.add_agent_activity_toolbar)

        configureViewModel()
        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_CHOOSE_PHOTO){
            if(resultCode == RESULT_OK){
                uriProfileImage = data?.data.toString()
                if(uriProfileImage != null){
                    Glide.with(context!!)
                            .load(uriProfileImage)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePicture)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_agent_activity_check -> {
                viewModel.actionFromIntent(AddAgentIntent.AddAgentToDBIntent(
                        uriProfileImage,
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
        disableAllErrors()
        viewModel.actionFromIntent(AddAgentIntent.AddAgentToDBIntent(
                null,
                firstName.text.toString(),
                lastName.text.toString(),
                email.text.toString(),
                phoneNumber.text.toString()
        ))
    }

    @OnClick(R.id.add_agent_view_picture_agent)
    fun clickProfilePictureListener(){
        chooseProfilePictureFromPhone()
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
        if(viewState.isSaved) {
            activity!!.finish()
        }
        if(viewState.errors != null){
            displayErrors(viewState.errors)
        }
    }

    private fun displayErrors(errors: List<ErrorSourceAddAgent>){
        errors.forEach{
            when(it){
                ErrorSourceAddAgent.FIRST_NAME_INCORRECT -> firstNameLayout.error = getString(R.string.error_message_first_name)
                ErrorSourceAddAgent.LAST_NAME_INCORRECT -> lastNameLayout.error = getString(R.string.error_message_last_name)
                ErrorSourceAddAgent.EMAIL_INCORRECT -> emailLayout.error = getString(R.string.error_message_email)
                ErrorSourceAddAgent.PHONE_INCORRECT -> phoneNumberLayout.error = getString(R.string.error_message_phone)
                ErrorSourceAddAgent.UNKNOW_ERROR -> showSnackBarMessage(getString(R.string.unknow_error))
            }
        }
    }

    private fun disableAllErrors(){
        firstNameLayout.isErrorEnabled = false
        lastNameLayout.isErrorEnabled = false
        emailLayout.isErrorEnabled = false
        phoneNumberLayout.isErrorEnabled = false
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    fun chooseProfilePictureFromPhone(){
        if(! EasyPermissions.hasPermissions(context!!, PERMS_EXT_STORAGE)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.storage_perm_request), RC_IMAGE_PERMS, PERMS_EXT_STORAGE)
            return
        }

        val photoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(photoIntent, RC_CHOOSE_PHOTO)

    }

    private fun showSnackBarMessage(message: String){
        val viewLayout = activity!!.findViewById<ContentFrameLayout>(android.R.id.content)
        showSnackBar(viewLayout, message)

    }
}
