package com.moviedrinkers.moviedrinkers.data

import org.json.JSONObject

data class DrinkingCue(
    val word: String,
    val occurrences: Int
) {
    companion object {
        fun fromJson(jsonObject: JSONObject): DrinkingCue {
            val word = jsonObject.optString("word", "")
            val occurrences = jsonObject.optInt("occurrences", 1)
            return DrinkingCue(
                word,
                occurrences
            )
        }
    }
}