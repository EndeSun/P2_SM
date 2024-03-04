package es.ua.eps.filmoteca

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import es.ua.eps.filmoteca.databinding.ActivityMapBinding


const val LOCATION_REQUEST_CODE = 111
const val GEOFENCE_LOCATION_REQUEST_CODE = 333
const val GEOFENCE_ID = "SOME_GEOFENCE_ID"
const val ACTION_GEOFENCE_EVENT = "com.tuapp.ACCION_GEOFENCE_EVENT"
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var title: String? = ""
    private var director: String? = ""
    private var year: Int = 0
    private lateinit var binding : ActivityMapBinding
    private lateinit var geofencingClient: GeofencingClient
    private val GEOFENCE_RADIUS = 500.00
    //-------------------------------------------------------
    //-------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        config()
        initUI()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkPermission()
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) { return }
        map.isMyLocationEnabled = true
        val location = LatLng(latitud, longitud)
        val marker = MarkerOptions().position(location).title(title)
        map.addMarker(marker)
        map.setInfoWindowAdapter(MyInfoWindowAdapter())
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15F))
        addCircle(location, GEOFENCE_RADIUS)
        createGeoFence(location, GEOFENCE_RADIUS)
    }
    //-------------------------------------------------------
    //Create the geofence
    private fun createGeoFence(location: LatLng, radius: Double) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(location.latitude, location.longitude, radius.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setLoiteringDelay(5000)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()

        //The information is saved at the pendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), GEOFENCE_LOCATION_REQUEST_CODE)
            } else {
                geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        Log.d("Añadido", "Añadido")
                    }
                    addOnFailureListener {
                        Log.d("Fallido", "Fallido")

                    }
                }
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.d("Añadido", "Añadido")
                }
                addOnFailureListener {
                    Log.d("Fallido", "Fallido")

                }
            }
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }










    //-------------------------------------------------------
    //Init UI
    private fun initUI(){
        //Receive the data from the filmData fragment
        latitud = intent.getDoubleExtra("latitud",0.0)
        longitud = intent.getDoubleExtra("longitud",0.0)
        title = intent?.getStringExtra("title")
        director = intent?.getStringExtra("director")
        year = intent.getIntExtra("year",0)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
    }
    //-------------------------------------------------------
    //Add Circle at the map.
    private fun addCircle(latLng: LatLng, radius: Double){
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(radius)
            .strokeColor(Color.argb(120, 255, 0, 0))
            .fillColor(Color.argb(120, 255, 0, 0))
            .strokeWidth(4F)
        map.addCircle(circleOptions)
    }
    //-------------------------------------------------------
    //Show the window information about the film.
    private inner class MyInfoWindowAdapter : InfoWindowAdapter {
        override fun getInfoContents(marker: Marker): View? {
            val infoView = LinearLayout(applicationContext)
            infoView.orientation = LinearLayout.VERTICAL

            val titleTextView = TextView(applicationContext)
            titleTextView.textSize = 16f
            titleTextView.text = marker.title
            infoView.addView(titleTextView)

            val directorTextView = TextView(applicationContext)
            directorTextView.textSize = 14f
            directorTextView.text = "Director: $director"
            infoView.addView(directorTextView)

            val yearTextView = TextView(applicationContext)
            yearTextView.textSize = 14f
            yearTextView.text = "Year: $year"
            infoView.addView(yearTextView)
            return infoView
        }

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }
    }
    //-------------------------------------------------------
    //Tabbar configuration.
    private fun config(){
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    //-------------------------------------------------------
    //Home button behaviour.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, Intent(this, FilmListFragment::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //-------------------------------------------------------
    //Check Permission
    private fun isLocationPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    //-------------------------------------------------------
    //Request permission
    private fun checkPermission(){
        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), LOCATION_REQUEST_CODE)
        } else {
            setupMap()
        }
    }
    //-------------------------------------------------------
    //Manage the permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap()
            }
        }
    }
    //-------------------------------------------------------
    //Geofence error
}