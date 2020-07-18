package com.moviedrinkers.moviedrinkers.data

import android.net.Uri
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

class Api {

    companion object {

        private const val MAIN_URL = "http://stvari.si:4567"
        private const val GENERATE_GAME_URL = "$MAIN_URL/game"
        private const val SUGGESTIONS_URL = "$MAIN_URL/suggestions"

        private fun getGenerateGameUrl(
            movieTitle: String,
            intoxication: Int,
            players: Int
        ): String {
            val generateGameUrl = Uri.parse(GENERATE_GAME_URL).buildUpon()
                .appendQueryParameter("movie", movieTitle)
                .appendQueryParameter("intoxication", intoxication.toString())
                .appendQueryParameter("players", players.toString())
                .build()
            return generateGameUrl.toString()
        }

        private fun getSuggestionsUrl(keywords: String): String {
            val generateGameUrl = Uri.parse(SUGGESTIONS_URL).buildUpon()
                .appendQueryParameter("keywords", keywords)
                .build()
            return generateGameUrl.toString()
        }

        fun generateGame(
            movieTitle: String,
            intoxication: Int,
            players: Int,
            listener: Response.Listener<JSONObject>,
            errorListener: Response.ErrorListener
        ): JsonObjectRequest {
            val url = this.getGenerateGameUrl(movieTitle, intoxication, players)
            val jsonRequest =
                JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener)
            jsonRequest.tag = VolleySingleton.GAME_GENERATION_TAG
            jsonRequest.retryPolicy =
                DefaultRetryPolicy(20000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            return jsonRequest
        }

        fun getSuggestions(
            keywords: String,
            listener: Response.Listener<JSONArray>,
            errorListener: Response.ErrorListener
        ): JsonArrayRequest {
            val url = this.getSuggestionsUrl(keywords)
            val jsonRequest =
                JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener)
            jsonRequest.tag = VolleySingleton.MOVIE_SUGGESTIONS_TAG
            return jsonRequest
        }
    }

}