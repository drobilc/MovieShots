package com.moviedrinkers.moviedrinkers.data

import org.json.JSONObject

data class Movie(
    val title: String,
    val duration: String
) {
    companion object {
        fun fromJson(jsonObject: JSONObject): Movie {
            val title = jsonObject.optString("title", "")
            val duration = jsonObject.optString("duration", "")
            return Movie(title, duration)
        }
    }
}