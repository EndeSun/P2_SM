package es.ua.eps.filmoteca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import es.ua.eps.filmoteca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FilmListFragment.OnItemSelectedListener{

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()

        //dynamic fragment
        if (findViewById<View?>(R.id.fragment_container)  != null) {
            if (savedInstanceState != null) return
            // Create the fragment
            val fragment = FilmListFragment()
            fragment.arguments = intent.extras
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment).commit()
        }
    }


    override fun onItemSelected(position: Int, listView: ListView) {
        //DataFragment instance
        var detalleFragment = supportFragmentManager.findFragmentById(R.id.fragment_data) as FilmDataFragment?
        if (detalleFragment != null) {
            // Update the fragment as the static way
            detalleFragment.setDetalleItem(position, listView)
        } else {
            // Do the transition
            detalleFragment = FilmDataFragment()
            val args = Bundle()
            //Film position param
            args.putInt(PARAM_POSICION, position)
            detalleFragment.arguments = args
            val t = supportFragmentManager.beginTransaction()

            t.replace(R.id.fragment_container, detalleFragment)
            t.addToBackStack(null)
            t.commit()
        }
    }

    private fun initUI(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}