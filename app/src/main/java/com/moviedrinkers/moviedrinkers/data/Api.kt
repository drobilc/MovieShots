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
        private const val MAIN_URL = "https://movieshots.stvari.si"
        private const val GENERATE_GAME_URL = "$MAIN_URL/game"
        private const val GET_GAME_DATA_URL = "$MAIN_URL/game/"
        private const val GET_SHARE_GAME_URL = "$MAIN_URL/share/"
        private const val TRENDING_MOVIES_URL = "$MAIN_URL/movie/trending"
        private const val RATE_GAME_URL = "$GENERATE_GAME_URL/rate"
        private const val SUGGESTIONS_URL = "$MAIN_URL/suggestions"
    }

    fun getShareUrl(gameId: String): String {
        val generatedSharingUrl = Uri.parse(GET_SHARE_GAME_URL).buildUpon()
            .appendEncodedPath(gameId)
            .build()
        return generatedSharingUrl.toString()
    }

    private fun getGenerateGameUrl(
        selectedMovie: Movie?,
        movieTitle: String,
        intoxication: Int,
        players: Int
    ): String {
        val generatedGameUrl = Uri.parse(GENERATE_GAME_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("movie", movieTitle)
            .appendQueryParameter("intoxication", intoxication.toString())
            .appendQueryParameter("players", players.toString())

        if (selectedMovie != null) {
            generatedGameUrl.appendQueryParameter("movie_id", selectedMovie.id)
        }

        return generatedGameUrl.build().toString()
    }

    private fun getRateGameUrl(gameId: String, rating: Float): String {
        val generatedRatingUrl = Uri.parse(RATE_GAME_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("game", gameId)
            .appendQueryParameter("rating", rating.toString())
            .build()
        return generatedRatingUrl.toString()
    }

    private fun getTrendingMoviesUrl(page: Int): String {
        val generatedTrendingMoviesUrl = Uri.parse(TRENDING_MOVIES_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("page", page.toString())
            .build()
        return generatedTrendingMoviesUrl.toString()
    }

    private fun getSuggestionsUrl(keywords: String): String {
        val generatedSuggestionsUrl = Uri.parse(SUGGESTIONS_URL).buildUpon()
            .appendQueryParameter("api_key", this.applicationKey)
            .appendQueryParameter("keywords", keywords)
            .build()
        return generatedSuggestionsUrl.toString()
    }

    private fun getGameDataUrl(gameId: String): String {
        val generatedGameDataUrl = Uri.parse(GET_GAME_DATA_URL).buildUpon()
            .appendEncodedPath(gameId)
            .appendQueryParameter("api_key", this.applicationKey)
            .build()
        return generatedGameDataUrl.toString()
    }

    fun generateGame(
        selectedMovie: Movie?,
        movieTitle: String,
        intoxication: Int,
        players: Int,
        listener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
    ): JsonObjectRequest {
        val url = this.getGenerateGameUrl(selectedMovie, movieTitle, intoxication, players)
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

    fun getTrendingMovies(page: Int, listener: Response.Listener<JSONArray>, errorListener: Response.ErrorListener): JsonArrayRequest {
        val url = this.getTrendingMoviesUrl(page)
        val jsonRequest = JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener)
        jsonRequest.tag = VolleySingleton.TRENDING_MOVIES_TAG
        return jsonRequest
    }

    fun getGame(gameId: String, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener): JsonObjectRequest {
        val url = this.getGameDataUrl(gameId)
        val jsonRequest =
            JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener)
        jsonRequest.tag = VolleySingleton.GAME_DATA_TAG
        jsonRequest.retryPolicy =
            DefaultRetryPolicy(20000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        return jsonRequest
    }

}