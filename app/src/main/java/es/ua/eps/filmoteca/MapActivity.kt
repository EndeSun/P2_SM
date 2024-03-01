package es.ua.eps.filmoteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NavUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import es.ua.eps.filmoteca.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var title: String? = ""
    private var director: String? = ""
    private var year: Int = 0



    private lateinit var binding : ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        config()

        latitud = intent.getDoubleExtra("latitud",0.0)
        longitud = intent.getDoubleExtra("longitud",0.0)

        title = intent?.getStringExtra("title")
        director = intent?.getStringExtra("director")
        year = intent.getIntExtra("year",0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val marker = MarkerOptions().position(LatLng(latitud, longitud)).title(title)
        map.addMarker(marker)
        map.setInfoWindowAdapter(MyInfoWindowAdapter())
        //Set the camera at the location
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitud, longitud)))
    }

    //Show the window information about the film
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

    private fun config(){
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, Intent(this, FilmListFragment::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}