package com.cbruinsm.tailwindtwo.ui.main.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.cbruinsm.tailwindtwo.R
import com.cbruinsm.tailwindtwo.TailWindApp.Companion.CURRENT_LOCATION
import com.cbruinsm.tailwindtwo.TailWindApp.Companion.NIGHT_MODE
import com.cbruinsm.tailwindtwo.TailWindApp.Companion.PATH_POINTS
import com.cbruinsm.tailwindtwo.TailWindApp.Companion.YOUR_NAME
import com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels.trackViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.JointType.BEVEL
import com.google.android.gms.maps.model.JointType.ROUND
import java.lang.StringBuilder
import kotlin.properties.Delegates

/**
 * Maps Fragment : Plots points on a Google Map and Retrieve current location.
 */
class MapsFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient //For location.
    private lateinit var ROUTE : trackViewModel
    private var longitude = 0.0
    private var latitude = 0.0
    private var name by Delegates.notNull<String>()
    private lateinit var mapFragment : SupportMapFragment

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    //Map Callback that plots for flight path : important checkpoints.
     private val flightPath = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        if(prefs.getBoolean(NIGHT_MODE,true)) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.style_json))
        }
        ROUTE.route.observe(viewLifecycleOwner) {
            // it is the route from the trackViewModel.
            val routes = ArrayList<LatLng>()
            if(prefs.getBoolean(PATH_POINTS,true)) {
                for(i in 0 until it.size) {
                    latitude = it[i].latitude
                    longitude = it[i].longitude
                    name = it[i].name
                    val type = it[i].type
                    name = StringBuilder("$name $type").toString()
                    /**
                     * Change the color of the destination airport's tag.
                     */
                    when (i) {
                        //Set Destination Marker to Green
                        it.size-1 -> {
                            googleMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .position(LatLng(latitude, longitude)).title(name)
                            )
                        }
                        else -> {
                            googleMap.addMarker(MarkerOptions()
                                .position(LatLng(latitude, longitude)).title(name)
                            )
                        }
                    }

                }
            }
            else {
                googleMap.clear()
                for(i in 0 until it.size) {
                    routes.add(LatLng(it[i].latitude, it[i].longitude))
                }
                var type = it[0].type
                name = it[0].name
                name = StringBuilder("$name $type").toString()
                googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(LatLng(routes[0].latitude,routes[0].longitude))
                    .title(name))
                type = it[routes.size-1].type
                name = it[routes.size-1].name
                name = StringBuilder("$name $type").toString()
                googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(name)
                    .position(LatLng(routes[routes.size-1].latitude,routes[routes.size-1].longitude)))
                googleMap.addPolyline(PolylineOptions()
                    .addAll(routes)
                    .geodesic(true)
                    .color(R.color.Peach))
            }
        }
    }

    //Map Callback that plots for user's current location
    @SuppressLint("MissingPermission")
    private val currentLocation = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = prefs.getBoolean(CURRENT_LOCATION,true)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
        name = getString(R.string.Go)
        ROUTE = ViewModelProvider(requireActivity()).get(trackViewModel::class.java)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(flightPath)
        val Checker : LocationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        if(!Checker.isProviderEnabled(GPS_PROVIDER))  {
            Permissions()
        }
        else {
            current()
            mapFragment.getMapAsync(currentLocation)

        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    fun settings() {
        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
    }


    //Get's the phone's location ENSURE APP LOCATION SERVICES ON
    public fun current() {
        if (ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener{ location : Location->
                latitude = location.latitude
                longitude = location.longitude
                mapFragment.getMapAsync(currentLocation)
            }
    }
    //Allows user to enable location services
fun Permissions() {
        var msg = resources.getString(R.string.Location2)
        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setTitle(R.string.Location1)
            msg = StringBuilder(msg).toString()
            setMessage(msg)
            setPositiveButton(R.string.Enable) { _, _ ->
                settings()
            }
            setNegativeButton(R.string.O_K_,null)
            show()
        }
    }

    /**
     * Unused...
     */
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}