package es.ua.eps.filmoteca

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import androidx.fragment.app.ListFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class FilmListFragment : ListFragment(), MessageListener {
    //------------------------------
    private var callback: OnItemSelectedListener? = null
    private lateinit var adapter: FilmsArrayAdapter
    private val itemSeleccionados = mutableListOf<Int>()
    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient


    //------------------------------
    companion object {
        private const val add_film = Menu.FIRST
        private const val closeSession = Menu.FIRST + 1
        private const val discconectAccount = Menu.FIRST + 2
        private const val about = Menu.FIRST + 3

    }


    //handle the message we received from FCM
    //------------------------------
    override fun onMessageReceived(message: RemoteMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                when (message.notification?.title) {
                    "New Film" -> handleFilmMessage(message, isNewFilm = true)
                    "Film deleted" -> handleFilmMessage(message, isNewFilm = false)
                    else -> Log.e("Error", "Error receiving the message")
                }
            }
        }
    }

    //--------------------------------- check if film exist -- Yes -> update; No -> add
    private fun handleFilmMessage(message: RemoteMessage, isNewFilm: Boolean) {
        val messageBody = message.notification?.body
        val messagePhoto = message.notification?.imageUrl
        val messageDirector = message.data["Director"]
        val messageYear = message.data["Year"]
        val messageGenre = message.data["Genre"]
        val messageIMDB = message.data["IMDB"]
        val messageFormat = message.data["Format"]
        val messageComments = message.data["Comments"]


        val filmExists = FilmDataSource.films.any { it.title.toString() == messageBody }

        if (isNewFilm) {
            if (filmExists) {
                // Existing film found, update
                updateFilm(messageBody, messagePhoto, messageDirector, messageYear, messageGenre, messageIMDB, messageFormat,messageComments)
            } else {
                // Film doesn't exist, add
                addFilm(messageBody, messagePhoto, messageDirector, messageYear, messageGenre, messageIMDB, messageFormat,messageComments)
            }
        } else {
            // Handle film deletion
            if (filmExists) {
                deleteFilm(messageBody)
            }
        }
    }
    //---------------------------------
    private fun updateFilm(title: String?, imageUrl: Uri?, director: String?, year: String?, genre: String?, imdb: String?, format: String?, comments: String?) {
        val existingFilm = FilmDataSource.films.find { it.title.toString() == title }
        existingFilm?.apply {
            // Update film properties as needed
            this.imageResId = 0 //Set image as default
            this.director = director
            if (year != null) {
                this.year = year.toInt()
            }
            if (genre != null) {
                this.genre = genre.toInt()
            }
            this.imdbUrl = imdb
            if (format != null) {
                this.format = format.toInt()
            }
            this.comments = comments
            imageUrl?.let { this.imageUrl = it.toString() }
        }
        (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
    }
    //---------------------------------
    private fun addFilm(title: String?, imageUrl: Uri?, director: String?, year: String?, genre: String?, imdb: String?, format: String?, comments: String?) {
        //New film
        val f5 = Film().apply {
            this.title = title
            this.director = director
            if (year != null) {
                this.year = year.toInt()
            }
            if (genre != null) {
                this.genre = genre.toInt()
            }
            this.imdbUrl = imdb
            if (format != null) {
                this.format = format.toInt()
            }
            this.comments = comments
            imageUrl?.let {film -> this.imageUrl = film.toString() }
        }
        FilmDataSource.films.add(f5)
        (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
    }
    //---------------------------------
    private fun deleteFilm(title: String?) {
        val existingFilm = FilmDataSource.films.find { film -> film.title.toString() == title }
        existingFilm?.let { film->
            FilmDataSource.films.remove(film)
            (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
        }
    }

    //---------------------------------
    //---------------------------------
    //---------------------------------


    //Interface for filmData communication
    interface OnItemSelectedListener {
        fun onItemSelected(position: Int, listView: ListView)
    }

    //To pass the FilmData
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as OnItemSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar OnItemSelectedListener")
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(context, gso)
    }

    //Film List Fragment Creation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        adapter = FilmsArrayAdapter(activity, R.layout.item_film, FilmDataSource.films)
        listAdapter = adapter
        FirebaseMessagingService.registerListener(this)
    }

    //------------------------------
    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        callback?.onItemSelected(position, listView)
    }

    //------------------------------
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val groupId = Menu.NONE //Menu unique id
        val itemId = add_film
        val itemOrder = Menu.NONE // we don't care the item order so --> Menu.NONE

        // Menu option label
        val itemText = R.string.menuOptionAdd
        val groupId2 = Menu.NONE

        val itemId2 = about
        val itemOrder2 = Menu.NONE
        val itemText2 = R.string.menuOptionAbout

        val itemId3 = closeSession
        val itemOrder3 = Menu.NONE
        val itemText3 = "Close session"

        val itemId4 = discconectAccount
        val itemOrder4 = Menu.NONE
        val itemText4 = "Disconnect account"

        val itemAdd = menu.add(groupId, itemId, itemOrder, itemText)
        val itemCloseSession = menu.add(groupId2, itemId3, itemOrder3, itemText3)
        val itemDiscconect = menu.add(groupId2, itemId4, itemOrder4, itemText4)
        val itemAbout = menu.add(groupId2, itemId2, itemOrder2, itemText2)

        //------------------------------
        itemAbout.intent = Intent(context, AboutActivity::class.java)
        //------------------------------
        itemAdd.setOnMenuItemClickListener {
            val f5 = Film()
            f5.title = getString(R.string.film5)
            f5.director = "Kenneth Branagh"
            f5.imageResId = R.mipmap.ic_launcher
            f5.comments = ""
            f5.format = Film.FORMAT_DIGITAL
            f5.genre = Film.GENRE_ACTION
            f5.imdbUrl = "https://www.imdb.com/title/tt0800369/?ref_=fn_al_tt_1"
            f5.year = 2011
            FilmDataSource.films.add(f5)
            (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
            true
        }
        //------------------------------
        itemCloseSession.setOnMenuItemClickListener {
            gsc.signOut().addOnCompleteListener{
                val intent = Intent(context, FilmSignActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            true
        }
        //------------------------------
        itemDiscconect.setOnMenuItemClickListener {
            gsc.revokeAccess().addOnCompleteListener{
                val intent = Intent(context, FilmSignActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    //------------------------------
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        listView.setMultiChoiceModeListener(
            object : AbsListView.MultiChoiceModeListener {

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater = mode.menuInflater
                    inflater.inflate(R.menu.context_menu, menu)
                    itemSeleccionados.clear()
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode,
                                                 item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.menuDeleteSelected -> {
                            deleteSelectedItems()
                            mode.finish()
                            true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode) {}

                override fun onItemCheckedStateChanged(mode: ActionMode,
                                                       position: Int, id: Long, checked: Boolean) {
                    val count = listView.checkedItemCount
                    mode.title = "$count seleccionados"

                    if (checked) {
                        val selectedView = listView.getChildAt(position)
                        selectedView?.setBackgroundResource(R.drawable.list_selector)
                    } else {
                        val selectedView = listView.getChildAt(position)
                        selectedView?.setBackgroundResource(android.R.color.transparent)
                    }
                }
            }
        )
    }

    private fun deleteSelectedItems() {
        val selectedItems = ArrayList<Film>() //List for save the films
        for (i in 0 until listView.count) {
            if (listView.isItemChecked(i)) {
                val film = listView.getItemAtPosition(i) as Film
                selectedItems.add(film)
            }
        }
        for (item in selectedItems) {
            // We remove films from FilmDataSource
            FilmDataSource.films.remove(item)
        }

        // Notify to the adapter
        (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseMessagingService.unregisterListener(this)
    }

}