package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import es.ua.eps.filmoteca.databinding.FragmentFilmDataBinding


const val PARAM_POSICION = "param1"
private const val MOVIE = 123
private var positionFilm = 0


@Suppress("DEPRECATION")

class FilmDataFragment : Fragment() {
    private var filmList: ListView ?= null
    private lateinit var binding: FragmentFilmDataBinding
    //------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    //------------------------------
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilmDataBinding.inflate(layoutInflater)
        return binding.root
    }
    //------------------------------
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkScreenSize()

        if (arguments != null){
            positionFilm = arguments?.getInt(PARAM_POSICION, -1)!!

            if (positionFilm != -1) {
                printFilmData(positionFilm)
                binding.backToHome.setOnClickListener{
                    activity?.supportFragmentManager?.popBackStack()
                    mostrarBarra(false)
                }
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) { // ID especial para botón "home"
            activity?.supportFragmentManager?.popBackStack()
            mostrarBarra(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //large screen --> no fragment_container --> without back button
    private fun checkScreenSize(){
        if(activity?.findViewById<View>(R.id.fragment_container) == null){
            printFilmData(0)
            binding.backToHome.visibility = View.INVISIBLE
        }else{
            mostrarBarra(true)
        }
    }

    //------------------------------
    fun setDetalleItem(position: Int, listView: ListView){
        printFilmData(position)
        positionFilm = position
        binding.backToHome.visibility = View.INVISIBLE
        filmList = listView
    }

    //------------------------------
    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            onActivityResult(MOVIE, result.resultCode, result.data)
        }
    @Deprecated("Deprecated in Java")
    //------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MOVIE){
            if (resultCode == Activity.RESULT_OK){
                val film = FilmDataSource.films[positionFilm]

                //Receive the film information
                val title = data?.getStringExtra("inputFilmTitle")
                val director = data?.getStringExtra("inputDirectorName")
                val year = data?.getIntExtra("inputYear", R.string.yearPublicationBladeRunner)
                if (year != null) {
                    film.year = year
                }
                val imdbUrl = data?.getStringExtra("inputLink")
                val gender = data?.getStringExtra("inputGender")
                val format = data?.getStringExtra("inputFormat")
                val comments = data?.getStringExtra("inputComment")

                //update film data
                film.genre = getGenreIndex(gender!!)
                film.format = getFormatIndex(format!!)
                film.title = title
                film.director = director
                film.imdbUrl = imdbUrl
                film.comments = comments

                val adapter = FilmsArrayAdapter(context, R.layout.item_film, FilmDataSource.films)
                adapter.notifyDataSetChanged()

                printFilmData(positionFilm)
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(activity,"Edición cancelada", Toast.LENGTH_LONG).show()
            }
        }
    }

    //------------------------------
    private fun printFilmData(position: Int){
        // Get the film with the position argument
        val film = FilmDataSource.films[position]

        //Get the image
        if(film.imageResId != 0){
            binding.bladeRunnerImage.setImageResource(film.imageResId)
        }else{
            Glide.with(this).load(film.imageUrl).into(binding.bladeRunnerImage)
        }

        with(binding){
            //Print the references
            filmData.text = film.title
            nameDirectorBladeRunner.text = film.director
            yearPublicationBladeRunner.text = film.year.toString()
            filmComment.text = film.comments
            latitudFilm.text = "${latitudFilm.text} ${film.latitud}"
            longitudFilm.text = "${longitudFilm.text} ${film.longitud}"


            val resources: Resources = resources
            val genderOptions = resources.getStringArray(R.array.genderOption)
            filmGenderBladeRunner.text = genderOptions[film.genre]
            val formatOptions = resources.getStringArray(R.array.formatOption)
            filmFormatBladeRunner.text = formatOptions[film.format]
            IMDBLink.setOnClickListener {
                val linkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(film.imdbUrl))
                startActivity(linkIntent)
            }

            //------------------------------ EDIT BUTTON
            filmEdit.setOnClickListener{
                val filmEditIntent = Intent(activity, FilmEditActivity::class.java)
                filmEditIntent.putExtra("position", position)
                if(Build.VERSION.SDK_INT >= 30) {
                    startForResult.launch(filmEditIntent)
                }
                else {
                    @Suppress("DEPRECATION")
                    startActivityForResult(filmEditIntent, MOVIE)
                }
            }

            showMapButton.setOnClickListener {
                val intentMap = Intent(context, MapActivity::class.java)

                intentMap.putExtra("latitud", film.latitud)
                intentMap.putExtra("longitud", film.longitud)
                intentMap.putExtra("title", film.title)
                intentMap.putExtra("director", film.director)
                intentMap.putExtra("year", film.year)


                startActivity(intentMap)
            }
        }
    }



    //------------------------------
    //------------------------------
    //------------------------------
    private fun getGenreIndex(genre: String): Int {
        val resources = resources
        val generos = resources.getStringArray(R.array.genderOption)
        for (i in generos.indices) {
            if (generos[i] == genre) {
                return i
            }
        }
        return -1 //Default value
    }
    //------------------------------
    private fun getFormatIndex(format: String): Int {
        val resources = resources
        val formatos = resources.getStringArray(R.array.formatOption)
        for (i in formatos.indices) {
            if (formatos[i] == format) {
                return i
            }
        }
        return -1 //Default value
    }

    //------------------------------
    //----------CONFIG--------------
    //------------------------------
    private fun mostrarBarra(show: Boolean){
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setHomeButtonEnabled(show)
        actionBar?.setDisplayHomeAsUpEnabled(show)
    }
}