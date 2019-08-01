package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.extensions.dollarToEuro
import com.openclassrooms.realestatemanager.extensions.sqFeetToSqMeter
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by galou on 2019-07-27
 */
class DoubleExtUnitTest {

    @Test
    fun convertDollarToEuro(){
        assertEquals(4060.00, 5000.00.dollarToEuro())
    }

    @Test
    fun convertSqFeetToSqMeter(){
        assertEquals(111.00, 1200.00.sqFeetToSqMeter())
    }
}