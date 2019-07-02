package com.openclassrooms.realestatemanager.addProperty


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import butterknife.BindView
import butterknife.ButterKnife
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.TypeProperty

/**
 * A simple [Fragment] subclass.
 *
 */
class AddPropertyView : Fragment() {

    @BindView(R.id.add_property_view_dropdown_type) lateinit var dropdown: AutoCompleteTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_property_view, container, false)
        ButterKnife.bind(this, view)


        ArrayAdapter(activity, android.R.layout.simple_spinner_item, TypeProperty.values())
                .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dropdown.setAdapter(adapter) }



        return view


    }




}
