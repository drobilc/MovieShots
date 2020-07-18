package com.moviedrinkers.moviedrinkers.data

import org.json.JSONArray

data class DrinkingGamePlayer(
    val drinkingCues: List<DrinkingCue>
) {

    fun getWords(): String {
        return drinkingCues.joinToString {
            it.word
        }
    }

    fun getTotalOccurrences(): Int {
        var totalOccurrences = 0
        for (drinkingCue in drinkingCues)
            totalOccurrences += drinkingCue.occurrences
        return totalOccurrences
    }

    companion object {
        fun fromJson(jsonArray: JSONArray): DrinkingGamePlayer {
            val cues = arrayListOf<DrinkingCue>()

            for (i in 0 until jsonArray.length()) {
                val drinkingCueJson = jsonArray.getJSONObject(i)
                val drinkingCue = DrinkingCue.fromJson(drinkingCueJson)
                cues.add(drinkingCue)
            }

            return DrinkingGamePlayer(cues)
        }
    }
}