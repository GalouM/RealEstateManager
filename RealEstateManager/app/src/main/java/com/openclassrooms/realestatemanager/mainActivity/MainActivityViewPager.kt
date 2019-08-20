package com.openclassrooms.realestatemanager.mainActivity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by galou on 2019-08-20
 */

class MainActivityViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs){

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}