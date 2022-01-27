package pt.ipp.estg.sensorsapp.src.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class LocationHelper {
    var LOCATION_REFRESH_TIME = 2000 // 3 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_DISTANCE = 0.5 // 1 meters. The Minimum Distance to be changed to get location update

    lateinit var mLocationManager :LocationManager
    lateinit var locationListener: LocationListener

    @SuppressLint("MissingPermission")
    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                myListener.onLocationChanged(location)
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        }

        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME.toLong(),
            LOCATION_REFRESH_DISTANCE.toFloat(),
            locationListener
        )
    }

    fun stopUpdates(){
        mLocationManager.removeUpdates(locationListener)
    }
}


interface MyLocationListener {
    fun onLocationChanged(location: Location?)
}
