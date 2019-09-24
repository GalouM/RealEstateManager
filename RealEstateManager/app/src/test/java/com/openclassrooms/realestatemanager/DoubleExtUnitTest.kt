package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.extensions.toEuro
import com.openclassrooms.realestatemanager.utils.extensions.toSqMeter
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by galou on 2019-07-27
 */
class DoubleExtUnitTest {

    @Test
    fun convertDollarToEuro(){
        assertEquals(4060.00, 5000.00.toEuro(Currency.DOLLAR))
    }

    @Test
    fun convertSqFeetToSqMeter(){
        assertEquals(111.00, 1200.00.toSqMeter(Currency.DOLLAR))
    }
}