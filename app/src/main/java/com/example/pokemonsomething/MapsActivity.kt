package com.example.pokemonlocaldemo

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var pokemonList: LinkedList<Pokemon>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        loadPokemons();

    }

    fun loadPokemons() {
        pokemonList = LinkedList<Pokemon>()
        pokemonList.add(Pokemon("Char", R.drawable.charm, 20.5937, 78.9629, false))
        pokemonList.add(Pokemon("Pika", R.drawable.pika, 61.5240, 105.3188, false))
    }

    fun checkPermission() {

        if (Build.VERSION.SDK_INT > 23) {


            if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1000)
                return;
            }
        }
        getLocation()
    }

    fun getLocation() {
        Toast.makeText(this, "Location here", Toast.LENGTH_SHORT).show()

        val locationListener = MyListner();
        val locationRequester = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationRequester.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, locationListener)
        val thread = MyThread();
        thread.start()
    }

    lateinit var location: Location;

    inner class MyListner : LocationListener {

        constructor() {
            location = Location("")
            location.latitude = 0.0
            location.longitude = 0.0
        }

        override fun onLocationChanged(p0: Location?) {
            if (p0 != null) {
                location = p0
            };
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            //To change body of created functions use File | Settings | File Templates.
        }

    }

    inner class MyThread : Thread {
        var oldLocation: Location;

        constructor() : super() {
            oldLocation = Location("OldOne")
            oldLocation.latitude = 0.0
            oldLocation.longitude = 0.0;
        }

        override fun run() {
            super.run()
            while (true) {
                if (location != null) {
                    runOnUiThread {
                    if (oldLocation.distanceTo(location) > 0) {
                        oldLocation = location;

                        mMap.clear()
                        val myLocation = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(
                                MarkerOptions().position(myLocation).title("Me").snippet("My Location").icon(
                                        BitmapDescriptorFactory.fromResource(R.drawable.ash)
                                )
                        )

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 2.0f))

                    }


                    for (i in 0 until pokemonList.size) {
                        if (!pokemonList[i].isCaught!!) {

                            val pokemonLocation = LatLng(pokemonList[i].location!!.latitude, pokemonList[i].location!!.longitude)
                            mMap.addMarker(
                                    MarkerOptions().position(pokemonLocation).title(pokemonList[i].name!!)
                                            .snippet("Catch them").icon(
                                                    BitmapDescriptorFactory.fromResource(pokemonList[i].image!!)
                                            )
                            )
                            if (location.distanceTo(pokemonList[i].location) < 2) {
                                pokemonList[i].isCaught = true;
                                Toast.makeText(applicationContext, "You caught it..yayyy", Toast.LENGTH_LONG).show()
                            }

                        }

                    }
                }
                }
                sleep(1000)

            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(this, "Sorry No Permission", Toast.LENGTH_SHORT).show()
                }
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
        checkPermission()
        // Add a marker in Sydney and move the camera

    }
}
