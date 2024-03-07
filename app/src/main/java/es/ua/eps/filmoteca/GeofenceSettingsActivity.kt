package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import es.ua.eps.filmoteca.databinding.ActivityGeofenceSettingsBinding

class GeofenceSettingsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGeofenceSettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGeofenceSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        config()
        initUI()
    }

    //-------------------------------------------------------
    private fun initUI(){
        val radiusGeofence = MapActivity.GEOFENCE_RADIUS
        binding.tvRadius.setText("$radiusGeofence")
        binding.buttonChangeRadius.setOnClickListener {
            val radiusSize = binding.tvRadius.text.toString()
            val intentInfoChange = Intent()
            intentInfoChange.putExtra("geofenceRadius", radiusSize.toDouble())
            setResult(Activity.RESULT_OK, intentInfoChange)
            finish()
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
}