package com.waytojava.mapmovementrapido

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.*


/**
 * Created by Manish Kumar on 1/5/2019
 */

class Utils {
    companion object {
        fun latLngToAddress(context: Context, latLng: LatLng): String? {
            val latitude = latLng.latitude
            val longitude = latLng.longitude
            val geocoder = Geocoder(context, Locale.getDefault())
            var address: String? = null
            try {
                val addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
                if (addresses != null) {
                    val locality = addresses[0].locality
//            val state = addresses[0].adminArea
//            val country = addresses[0].countryName
//            val postalCode = addresses[0].postalCode
                    val knownName = addresses[0].featureName // Only if available else return NULL
//            val premises = addresses[0].premises
//            val subAdminArea = addresses[0].subAdminArea
                    val subLocality = addresses[0].subLocality
                    address = "$knownName, $subLocality, $locality"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return address
        }
    }
}