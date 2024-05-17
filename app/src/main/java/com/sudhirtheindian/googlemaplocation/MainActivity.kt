
package com.sudhirtheindian.googlemaplocation

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTextView: TextView
    private lateinit var currentLocationTextView: TextView
    private val PERMISSIONS_REQUEST_LOCATION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationTextView = findViewById(R.id.locationTextView)
        currentLocationTextView = findViewById(R.id.currentLocationTextView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
            return
        }

        // Once permissions are granted, request location updates
        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations, this can be null.
                location?.let {
                    // Handle location here
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // Get location name
                    val locationName = getLocationName(latitude, longitude)
                    // Update TextView with location name
                    locationTextView.text = locationName

                    // Display current location
                    val currentLocation = "Latitude: $latitude, Longitude: $longitude"
                    currentLocationTextView.text = currentLocation

                    Log.d("LocationUpdate", "Latitude: $latitude, Longitude: $longitude")
                } ?: Log.e("LocationUpdate", "Last known location is null")
            }
            .addOnFailureListener { e ->
                Log.e("LocationUpdate", "Error getting last known location: ${e.message}")
            }
    }


    private fun getLocationName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                address.getAddressLine(0) // You can customize how you want to display the address here
            } else {
                "Unknown location"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error retrieving location"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates()
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }
}
