package com.moviedrinkers.moviedrinkers.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moviedrinkers.moviedrinkers.data.Movie

class Repository(
    private val repository: Repository = TODO(),
    private val movieCache: Cache<String, LiveData<Movie>> = Cache()
) {


    fun getMovie(movieId: String): LiveData<Movie> {
        val cachedMovie = this.movieCache.get(movieId)
        if (cachedMovie != null)
            return cachedMovie

        val movie = MutableLiveData<Movie>()
        // TODO: Get movie from REST API
        return movie
    }

}