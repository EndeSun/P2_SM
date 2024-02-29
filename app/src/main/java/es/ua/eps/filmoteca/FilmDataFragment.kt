package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


const val PARAM_POSICION = "param1"
private const val MOVIE = 123
private var positionFilm = 0

@Suppress("DEPRECATION")

class FilmDataFragment : Fragment() {
    private var filmList: ListView ?= null

    //------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    //------------------------------
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_film_data, container, false)
    }
    //------------------------------
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkScreenSize()

        if (arguments != null){
            positionFilm = arguments?.getInt(PARAM_POSICION, -1)!!
            Log.i("posicion","$positionFilm")
            if (positionFilm != -1) {
                printFilmData(positionFilm)
                val backToHome = activity?.findViewById<Button>(R.id.backToHome)
                backToHome?.setOnClickListener{
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
            val home = activity?.findViewById<Button>(R.id.backToHome)
            home?.visibility = View.INVISIBLE
        }else{
            mostrarBarra(true)
        }
    }

    //------------------------------
    fun setDetalleItem(position: Int, listView: ListView){
        printFilmData(position)
        positionFilm = position
        val home = activity?.findViewById<Button>(R.id.backToHome)
        home?.visibility = View.INVISIBLE
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
                Log.i("Prueba","$positionFilm")
                //Recibimos los datos
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
                //Actualizamos los datos de la lista de película
                film.genre = getGenreIndex(gender!!)
                film.format = getFormatIndex(format!!)
                film.title = title
                film.director = director
                film.imdbUrl = imdbUrl
                film.comments = comments

                val adapter = FilmsArrayAdapter(context, R.layout.item_film, FilmDataSource.films)
                filmList?.adapter = adapter

                printFilmData(positionFilm)
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(activity,"Edición cancelada", Toast.LENGTH_LONG).show()
            }
        }
    }

    //------------------------------
    private fun printFilmData(position: Int){
        // Obtenemos las referencias
        val film = FilmDataSource.films[position]
        val filmData = activity?.findViewById<TextView>(R.id.filmData)
        val filmDirectorName = activity?.findViewById<TextView>(R.id.nameDirectorBladeRunner)
        val filmYear = activity?.findViewById<TextView>(R.id.yearPublicationBladeRunner)
        val filmGender = activity?.findViewById<TextView>(R.id.filmGenderBladeRunner)
        val filmFormat = activity?.findViewById<TextView>(R.id.filmFormatBladeRunner)
        val filmComment = activity?.findViewById<TextView>(R.id.filmComment)

        //Print the references
        filmData!!.text = film.title
        filmDirectorName!!.text = film.director
        filmYear!!.text = film.year.toString()
        filmComment!!.text = film.comments
        val resources: Resources = resources
        val genderOptions = resources.getStringArray(R.array.genderOption)
        filmGender!!.text = genderOptions[film.genre]
        val formatOptions = resources.getStringArray(R.array.formatOption)
        filmFormat!!.text = formatOptions[film.format]
        val filmLink = activity?.findViewById<Button>(R.id.IMDBLink)
        filmLink!!.setOnClickListener {
            val linkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(film.imdbUrl))
            startActivity(linkIntent)
        }
        val filmImage = activity?.findViewById<ImageView>(R.id.bladeRunnerImage)

        if(film.imageResId != 0){
            filmImage!!.setImageResource(film.imageResId)
        }else{
            Glide.with(this).load(film.imageUrl).into(filmImage!!)
        }
        //Botón para ir a editar
        val buttonEdit = activity?.findViewById<Button>(R.id.filmEdit)
        buttonEdit?.setOnClickListener{
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
    }



    //------------------------------
    private fun getGenreIndex(genre: String): Int {
        val resources = resources
        val generos = resources.getStringArray(R.array.genderOption)
        for (i in generos.indices) {
            if (generos[i] == genre) {
                return i
            }
        }
        return -1 // Valor predeterminado si no se encuentra el género
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
        return -1 // Valor predeterminado si no se encuentra el formato
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