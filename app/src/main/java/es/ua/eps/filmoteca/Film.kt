package es.ua.eps.filmoteca

class Film {

    var imageUrl: String ?= null
    var imageResId = 0 // Propiedades de la clase
    var title: String? = null
    var director: String? = null
    var year = 0
    var genre = 0
    var format = 0
    var imdbUrl: String? = null
    var comments: String? = null

    override fun toString(): String {
        return title?:"<Sin titulo>" // Al convertir a cadena mostramos su título
    }

    companion object {
        const val FORMAT_DVD = 0 // Formatos
        const val FORMAT_BLURAY = 1
        const val FORMAT_DIGITAL = 2
        const val GENRE_ACTION = 0 // Géneros
        const val GENRE_COMEDY = 1
        const val GENRE_DRAMA = 2
        const val GENRE_SCIFI = 3
        const val GENRE_HORROR = 4
    }
}


object FilmDataSource {
    val films: MutableList<Film> = mutableListOf<Film>()
    init {
        val f1 = Film()
        f1.title = "Blade runner"
        f1.director = "Ridley Scott"
        f1.imageResId = R.drawable.anchor
        f1.comments = ""
        f1.format = Film.FORMAT_DIGITAL
        f1.genre = Film.GENRE_SCIFI
        f1.imdbUrl = "https://www.imdb.com/title/tt0083658/?ref_=fn_al_tt_1"
        f1.year = 1982
        val f2 = Film()
        f2.title = "Regreso al futuro II"
        f2.director = "Robert Zemeckis"
        f2.imageResId = R.drawable.coche
        f2.comments = ""
        f2.format = Film.FORMAT_DVD
        f2.genre = Film.GENRE_SCIFI
        f2.imdbUrl = "https://www.imdb.com/title/tt0096874/?ref_=fn_al_tt_3"
        f2.year = 1989
        val f3 = Film()
        f3.title = "Regreso al futuro III"
        f3.director = "Robert Zemeckis"
        f3.imageResId = R.drawable.bug
        f3.comments = ""
        f3.format = Film.FORMAT_BLURAY
        f3.genre = Film.GENRE_ACTION
        f3.imdbUrl = "https://www.imdb.com/title/tt0099088/?ref_=fn_al_tt_4"
        f3.year = 1990
        val f4 = Film()
        f4.title = "Regreso al futuro"
        f4.director = "Ivan Reitman"
        f4.imageResId = R.drawable.good
        f4.comments = ""
        f4.format = Film.FORMAT_DIGITAL
        f4.genre = Film.GENRE_SCIFI
        f4.imdbUrl = "https://www.imdb.com/title/tt4513678/"
        f4.year = 1984

        films.add(f1)
        films.add(f2)
        films.add(f3)
        films.add(f4)
    }
}