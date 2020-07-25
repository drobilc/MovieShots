package com.moviedrinkers.moviedrinkers.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class DrinkingGame(
    val id: String,
    val movie: Movie,
    val players: List<DrinkingGamePlayer>,
    val bonusWords: List<DrinkingCue>,
    val rating: Double,
    val numberOfReviews: Int
): Parcelable {

    fun numberOfShots(): Int {
        if (this.players.isEmpty())
            return 0

        var totalNumberOfShots = 0
        for (player: DrinkingGamePlayer in this.players) {
            totalNumberOfShots += player.getTotalOccurrences()
        }
        return (totalNumberOfShots / this.players.size).toInt()
    }

    companion object {

        fun fromJson(jsonObject: JSONObject, movie: Movie): DrinkingGame {
            val gameId = jsonObject.optString("id", "")
            val playersArray = jsonObject.getJSONArray("words")
            val bonusWordsArray = jsonObject.getJSONArray("bonus_words")

            val players = arrayListOf<DrinkingGamePlayer>()
            val bonusWords = arrayListOf<DrinkingCue>()

            for (i in 0 until playersArray.length()) {
                val playerJson = playersArray.getJSONArray(i)
                val player =
                    DrinkingGamePlayer.fromJson(
                        playerJson
                    )
                players.add(player)
            }

            for (i in 0 until bonusWordsArray.length()) {
                val drinkingCueJson = bonusWordsArray.getJSONObject(i)
                val drinkingCue = DrinkingCue.fromJson(drinkingCueJson)
                bonusWords.add(drinkingCue)
            }

            val rating = jsonObject.optDouble("rating", 0.0)
            val numberOfReviews = jsonObject.optInt("number_of_reviews", 0)

            return DrinkingGame(gameId, movie, players, bonusWords, rating, numberOfReviews)
        }

        fun fromJson(jsonObject: JSONObject): DrinkingGame {
            val movieJson = jsonObject.getJSONObject("movie")
            val movie = Movie.fromJson(movieJson)
            return fromJson(jsonObject, movie)
        }
    }
}