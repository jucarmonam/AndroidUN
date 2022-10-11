package com.unal.reto9

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.unal.reto9.PermissionUtils.isPermissionGranted
import com.unal.reto9.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

// Add an import statement for the client library.

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnPoiClickListener {

    private var permissionDenied = false
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var zoomNumber = 16

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onPoiClick(poi: PointOfInterest) {

        // Define a Place ID.
        val placeId = poi.placeId
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.BUSINESS_STATUS)

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        val placesClient: PlacesClient = Places.createClient(this)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                Toast.makeText(
                    this, """Clicked: ${poi.name}
                        Business:${place.businessStatus}
                        Latitude:${poi.latLng.latitude} Longitude:${poi.latLng.longitude}""",
                    Toast.LENGTH_SHORT
                ).show()

            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                }
            }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()

        /*
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-33.852, 151.211)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))
         */
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        val here = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(here))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 16f))
                    }
                }
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    private fun dialogPicker() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.picker, null)
        val numberPicker: NumberPicker = dialogView.findViewById(R.id.dialog_number_picker)
        numberPicker.minValue = 13
        numberPicker.maxValue = 18
        numberPicker.wrapSelectorWheel = false
        numberPicker.value = zoomNumber
        numberPicker.setOnValueChangedListener { numberPicker, _, newValue ->
            zoomNumber = newValue
        }
        with(builder)
        {
            setTitle("Zoom")
            setMessage("Selecciona un nivel de zoom")
            setView(dialogView)
            setPositiveButton("Cambiar", positiveButtonClick)
            show()
        }
    }

    private val positiveButtonClick = { dialog: DialogInterface, _: Int ->
        myLocationZoom()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add("Cambiar zoom")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dialogPicker()
        return true
    }

    private fun myLocationZoom() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val here = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(here))
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                here,
                                zoomNumber.toFloat()
                            )
                        )
                    }
                }
            return
        }
    }
}