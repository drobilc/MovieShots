package com.moviedrinkers.moviedrinkers.data

import org.json.JSONObject

data class Movie(
    val title: String,
    val year: Int,
    val duration: String,
    val cover: String
) {
    companion object {
        fun fromJson(jsonObject: JSONObject): Movie {
            val title = jsonObject.optString("title", "")
            val duration = jsonObject.optString("duration", "")
            val year = jsonObject.optInt("year", 0)
            val cover = jsonObject.optString("cover", "")
            return Movie(title, year, duration, cover)
        }
    }
}