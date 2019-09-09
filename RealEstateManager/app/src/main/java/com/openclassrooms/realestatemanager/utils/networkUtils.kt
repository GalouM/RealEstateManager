package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by galou on 2019-09-09
 */

fun isWifiAvailable(context: Context): Boolean{
    val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    return  wifi.isWifiEnabled

}

fun isInternetAvailable(context: Context): Boolean{
    val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}


fun isGPSAvailable(context: Context): Boolean{
    val service = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

@AfterPermissionGranted(RC_LOCATION_PERMS)
fun requestPermissionLocation(fragment: Fragment): Boolean{
    if(! EasyPermissions.hasPermissions(fragment.activity!!, PERMS_LOCALISATION)) {
        EasyPermissions.requestPermissions(
                fragment, fragment.activity!!.getString(R.string.storage_perm_request), RC_LOCATION_PERMS, PERMS_LOCALISATION)
        return false
    }
    return true
}