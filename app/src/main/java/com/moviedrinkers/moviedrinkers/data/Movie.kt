package com.moviedrinkers.moviedrinkers.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class Movie(
    val id: String,
    val title: String,
    val overview: String,
    val year: Int,
    val duration: String,
    val cover: String
): Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject): Movie {
            val id = jsonObject.optString("id", "")
            val title = jsonObject.optString("title", "")
            val overview = jsonObject.optString("overview", "")
            val duration = jsonObject.optString("duration", "")
            val year = jsonObject.optInt("year", 0)
            val cover = jsonObject.optString("cover", "")
            return Movie(id, title, overview, year, duration, cover)
        }

        fun fromTrendingMovie(movie: TrendingMovie): Movie {
            return Movie(movie.id, movie.title, movie.overview, movie.year, movie.duration, movie.cover)
        }

    }

    override fun toString(): String {
        return "Movie(id='$id', title='$title', year=$year, duration='$duration', cover='$cover')"
    }
}

@Parcelize
data class TrendingMovie(
    val id: String,
    val title: String,
    val overview: String,
    val year: Int,
    val duration: String,
    val cover: String,
    val rating: Double,
    val numberOfReviews: Int,
    val games: List<DrinkingGame>
): Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject): TrendingMovie {
            val id = jsonObject.optString("id", "")
            val title = jsonObject.optString("title", "")
            val overview = jsonObject.optString("overview", "")
            val duration = jsonObject.optString("duration", "")
            val year = jsonObject.optInt("year", 0)
            val cover = jsonObject.optString("cover", "")

            val rating = jsonObject.optDouble("rating", 0.0)
            val numberOfReviews = jsonObject.optInt("number_of_reviews", 0)

            val games = arrayListOf<DrinkingGame>()

            val movie = TrendingMovie(id, title, overview, year, duration, cover, rating, numberOfReviews, games)

            val normalMovie = Movie.fromTrendingMovie(movie)

            val drinkingGames = jsonObject.optJSONArray("games")
            for (i in 0 until drinkingGames.length()) {
                val drinkingGameJson = drinkingGames.getJSONObject(i)
                val drinkingGame = DrinkingGame.fromJson(drinkingGameJson, normalMovie)
                games.add(drinkingGame)
            }

            return movie
        }
    }

    override fun toString(): String {
        return "TrendingMovie(id='$id', title='$title', year=$year, duration='$duration', cover='$cover')"
    }
}