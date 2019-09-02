package com.openclassrooms.realestatemanager.mainActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.openclassrooms.realestatemanager.listProperties.ActionTypeList
import com.openclassrooms.realestatemanager.listProperties.ListPropertyView
import com.openclassrooms.realestatemanager.listProperties.MapPropertyView

/**
 * Created by galou on 2019-06-30
 */

class PageAdapterMainActivity(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> ListPropertyView.newInstance(ActionTypeList.ALL_PROPERTIES.actionName)
            1 -> MapPropertyView.newInstance(ActionTypeList.ALL_PROPERTIES.actionName)
            else -> throw Exception ("No page found")
        }
    }

    override fun getCount(): Int {
        return 2
    }

}