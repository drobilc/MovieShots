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

class Api(private val applicationKey: String) {

    companion object {
        private const val MAIN_URL = "http://stvari.si:4567"
        private const val GENERATE_GAME_URL = "$MAIN_URL/game"
        private const val RATE_GAME_URL = "$GENERATE_GAME_URL/rate"
        private const val SUGGESTIONS_URL = "$MAIN_URL/suggestions"
    }

    private fun getGenerateGameUrl(
        movieTitle: String,
        intoxication: Int,
        players: Int
    ): String {
        val generatedGameUrl = Uri.parse(GENERATE_GAME_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("movie", movieTitle)
            .appendQueryParameter("intoxication", intoxication.toString())
            .appendQueryParameter("players", players.toString())
            .build()
        return generatedGameUrl.toString()
    }

    private fun getRateGameUrl(gameId: String, rating: Float): String {
        val generatedRatingUrl = Uri.parse(RATE_GAME_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("game", gameId)
            .appendQueryParameter("rating", rating.toString())
            .build()
        return generatedRatingUrl.toString()
    }

    private fun getSuggestionsUrl(keywords: String): String {
        val generatedSuggestionsUrl = Uri.parse(SUGGESTIONS_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("keywords", keywords)
            .build()
        return generatedSuggestionsUrl.toString()
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

    fun rateGame(gameId: String, rating: Float, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener): JsonObjectRequest {
        val url = this.getRateGameUrl(gameId, rating)
        val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener)
        jsonRequest.tag = VolleySingleton.GAME_RATING_TAG
        return jsonRequest
    }

}