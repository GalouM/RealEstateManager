package com.openclassrooms.realestatemanager;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by galou on 2019-06-21
 */
public class UtilsUnitTest {

    @Test
    public void checkConvertDollarToEuro_correct(){
        assertEquals(4060, Utils.convertDollarToEuro(5000));

    }

    @Test
    public void checkConvertEuroToDollar_correct(){
        assertEquals(5685, Utils.convertEuroToDollar(5000));

    }

    @Test
    public void getTodayDate_correct(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());

        assertEquals(todayDate, Utils.getTodayDate());
    }
}
