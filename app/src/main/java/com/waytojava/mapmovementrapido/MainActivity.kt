package com.waytojava.mapmovementrapido

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MainActivity::class.java.simpleName
    private var googleMap: GoogleMap? = null
    private var currentLatLng: LatLng? = null
    private var imageMarker: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageMarker = findViewById(R.id.imageMarker)

        // Map Setup
        val supportFragmentManager = supportFragmentManager
        val fragment = supportFragmentManager.findFragmentById(R.id.map)
        val supportMapFragment: SupportMapFragment = fragment as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        val currentLocationFinder = CurrentLocationFinder(this, object : OnLocationChangeListener {
            override fun locationChange(location: Location?) {
                Log.d(TAG, "locationChange : $location")
                this@MainActivity.currentLatLng = LatLng(location!!.latitude, location!!.longitude)
            }
        })

        if (currentLatLng == null) {
            currentLatLng = LatLng(
                currentLocationFinder.getCurrentLocation().latitude,
                currentLocationFinder.getCurrentLocation().longitude
            )
            setCurrentAddressToTextField(currentLatLng!!)
        }


        btn_current_location.setOnClickListener {
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
            //zoomMapToCurrentLocation(currentLatLng)
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        googleMap!!.uiSettings.isRotateGesturesEnabled = false
        googleMap!!.uiSettings.isMyLocationButtonEnabled = true
        // Add Marker
//        val markerOptions = MarkerOptions()
//        markerOptions.position(currentLatLng!!)
//        markerOptions.title("Your location")
//        googleMap!!.addMarker(markerOptions)
        // Zoom Map
        zoomMapToCurrentLocation(currentLatLng)

        googleMap.setOnCameraMoveListener(object : GoogleMap.OnCameraMoveListener {
            override fun onCameraMove() {
                googleMap.setOnCameraIdleListener {
                    // IMPORTANT
                    val selectedLatLng = googleMap.cameraPosition.target
                    setCurrentAddressToTextField(selectedLatLng)
                }
            }
        })
    }

    private fun zoomMapToCurrentLocation(currentLatLng: LatLng?) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.7f)
        googleMap!!.moveCamera(cameraUpdate)
    }

    fun setCurrentAddressToTextField(latLng: LatLng) {
        val address = runBlocking {
            async(Dispatchers.Default) {
                Utils.latLngToAddress(this@MainActivity, latLng)
            }.await()
        }
        tvPickup.text = address
    }
}