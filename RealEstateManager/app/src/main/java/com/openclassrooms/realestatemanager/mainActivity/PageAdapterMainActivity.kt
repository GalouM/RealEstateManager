package com.openclassrooms.realestatemanager.mainActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by galou on 2019-06-30
 */

class PageAdapterMainActivity(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> ListPropertyView()
            1 -> MapPropertyView()
            else -> throw Exception ("No page found")
        }
    }

    override fun getCount(): Int {
        return 2
    }

}