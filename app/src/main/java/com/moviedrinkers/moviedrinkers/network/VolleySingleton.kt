package com.moviedrinkers.moviedrinkers.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class VolleySingleton constructor(context: Context) {

    companion object {

        // Tags that we can use to identify requests
        const val MOVIE_SUGGESTIONS_TAG: String = "MOVIE_SUGGESTIONS"
        const val GAME_GENERATION_TAG: String = "GAME_GENERATION"
        const val GAME_RATING_TAG: String = "GAME_RATING"
        const val TRENDING_MOVIES_TAG: String = "POPULAR_GAMES"
        const val GAME_DATA_TAG: String = "GAME_DATA"

        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: VolleySingleton(
                        context
                    ).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

}