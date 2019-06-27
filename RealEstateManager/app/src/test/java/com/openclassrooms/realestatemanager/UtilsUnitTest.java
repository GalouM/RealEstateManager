package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by galou on 2019-06-21
 */
@RunWith(AndroidJUnit4.class)
public class UtilsUnitTest {

    private ConnectivityManager connectivityManager;
    private ShadowNetworkInfo shadowOfActiveNetworkInfo;
    private Context context;

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

    @Test
    public void checkIsInternetAvailableFalse_NoInternet() throws Exception{
        this.setUpContextInternet();
        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.DISCONNECTED);
        assertFalse(Utils.isInternetAvailable(context));
    }

    @Test
    public void checkIsInternetAvailableTrue_InternetOn() throws Exception{
        this.setUpContextInternet();
        shadowOfActiveNetworkInfo.setConnectionStatus(NetworkInfo.State.CONNECTED);
        assertTrue(Utils.isInternetAvailable(context));
    }

    private void setUpContextInternet(){
        context = getApplicationContext();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        shadowOfActiveNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());
    }
}
