package com.moviedrinkers.moviedrinkers

import android.app.Application
import android.content.Context
import com.moviedrinkers.moviedrinkers.data.Api
import java.util.*

class MovieShotsApplication: Application() {

    companion object {
        const val PREFERENCES_FILE_KEY = "MOVIE_SHOTS_PREFERENCES"
        const val APPLICATION_KEY = "MOVIE_SHOTS_APPLICATION_KEY"
    }

    fun getApi(): Api {
        var applicationKey = this.getApplicatonKey()
        if (applicationKey == null) {
            // Application key has not yet been created, do it now
            applicationKey = this.generateApplicationKey()
            this.saveApplicationKey(applicationKey)
        }

        // Here, we have either read the application key or generated and saved a new one.
        // We can now create a new Api object with this key
        return Api(applicationKey)
    }

    private fun getApplicatonKey(): String? {
        val preferences = this.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return preferences.getString(APPLICATION_KEY, null)
    }

    private fun saveApplicationKey(key: String) {
        val preferences = this.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with (preferences.edit()) {
            putString(APPLICATION_KEY, key)
            commit()
        }
    }

    private fun generateApplicationKey(): String {
        return UUID.randomUUID().toString()
    }

}