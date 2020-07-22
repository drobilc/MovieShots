package com.moviedrinkers.moviedrinkers.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class DrinkingCue(
    val word: String,
    val occurrences: Int
): Parcelable {
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