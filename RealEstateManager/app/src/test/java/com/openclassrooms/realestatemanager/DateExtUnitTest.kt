package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.extensions.isCorrectOnMarketDate
import com.openclassrooms.realestatemanager.utils.extensions.isCorrectSoldDate
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * Created by galou on 2019-07-27
 */
class DateExtUnitTest {

    @Test
    fun onMarketDateAfterToday(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val onMarketDate = calendar.time

        assertFalse(onMarketDate.isCorrectOnMarketDate())

    }

    @Test
    fun onMarketDateBeforeToday(){
        val calendar = Calendar.getInstance()
        val onMarketDate = calendar.time

        assertTrue(onMarketDate.isCorrectOnMarketDate())

    }

    @Test
    fun sellDateBeforeOnMarketDate(){
        val calendar = Calendar.getInstance()
        val sellDate = calendar.time
        calendar.add(Calendar.DATE, 1)
        val onMarketDate = calendar.time

        assertFalse(sellDate.isCorrectSoldDate(onMarketDate))

    }

    @Test
    fun sellDateAfterToday(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val sellDate = calendar.time
        val onMarketDate = calendar.time

        assertFalse(sellDate.isCorrectSoldDate(onMarketDate))

    }

    @Test
    fun sellDateBeforeTodayAndAfterOnMarketDate(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val sellDate = calendar.time
        calendar.add(Calendar.DATE, -1)
        val onMarketDate = calendar.time

        assertTrue(sellDate.isCorrectSoldDate(onMarketDate))

    }
}