package com.moviedrinkers.moviedrinkers.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class VolleySingleton constructor(context: Context) {

    companion object {

        // Tags that we can use to identify requests
        const val MOVIE_SUGGESTIONS_TAG: String = "MOVIE_SUGGESTIONS"
        const val GAME_GENERATION_TAG: String = "GAME_GENERATION"

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