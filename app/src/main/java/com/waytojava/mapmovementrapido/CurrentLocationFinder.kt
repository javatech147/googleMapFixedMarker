package com.waytojava.mapmovementrapido

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

class CurrentLocationFinder(private val mContext: Context, private val onLocationListener: OnLocationChangeListener?) :
    LocationListener {
    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 1 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (5000).toLong() // 5 sec
    }

    val TAG = CurrentLocationFinder::class.java.simpleName
    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null


    init {
        initializeCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun initializeCurrentLocation() {
        try {
            if (locationManager == null) {
                locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }

            val isGpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGpsEnabled) {
                if (locationManager != null) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    currentLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
            }

            if (isNetworkEnabled) {
                if (locationManager != null) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    currentLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            }

            if (currentLocation == null) {
                currentLocation = Location(LocationManager.GPS_PROVIDER)
                currentLocation!!.latitude = 0.00000
                currentLocation!!.longitude = 0.00000
            }

            Log.d(TAG, "Current Location CurrentLocationFinder : $currentLocation")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun removeLocationUpdate() {
        try {
            locationManager?.let {
                Log.d(TAG, "Removed location update")
                locationManager!!.removeUpdates(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentLocation(): Location {
        return currentLocation!!
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
        Log.d(TAG, "onProviderEnabled : $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d(TAG, "onProviderDisabled : $provider")
    }

    var isGPSActivated: Boolean = false

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location: Location?) {
        Log.d(TAG, "onLocationChanged : $location")
        onLocationListener!!.locationChange(location)
        this.currentLocation = location!!

        try {
            if (location!!.provider.equals("gps")) {
                if (!isGPSActivated) {
                    locationManager!!.removeUpdates(this)
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    isGPSActivated = true
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception : onLocationChanged ${e.message}")
        }
    }
}