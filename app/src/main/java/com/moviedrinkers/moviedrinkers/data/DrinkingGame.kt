package com.moviedrinkers.moviedrinkers.data

import org.json.JSONObject

data class DrinkingGame(
    val id: String,
    val movie: Movie,
    val players: List<DrinkingGamePlayer>,
    val bonusWords: List<DrinkingCue>
) {
    companion object {
        fun fromJson(jsonObject: JSONObject): DrinkingGame {
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

            val movieJson = jsonObject.getJSONObject("movie")
            val movie = Movie.fromJson(
                movieJson
            )

            return DrinkingGame(gameId, movie, players, bonusWords)
        }
    }
}