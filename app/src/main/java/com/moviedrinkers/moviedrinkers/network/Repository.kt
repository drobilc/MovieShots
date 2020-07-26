package com.moviedrinkers.moviedrinkers.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(
    private val webservice: Webservice = TODO(),
    private val movieCache: Cache<String, LiveData<Movie>> = Cache(),
    private val gameCache: Cache<String, LiveData<DrinkingGame>> = Cache()
) {

    fun getMovie(movieId: String): LiveData<Movie> {
        val cachedMovie = this.movieCache.get(movieId)
        if (cachedMovie != null)
            return cachedMovie

        val movie = MutableLiveData<Movie>()
        webservice.getMovie(movieId).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                movie.value = response.body()
            }
            override fun onFailure(call: Call<Movie>, t: Throwable) {
                // There was an error while fetching data
            }
        })
        return movie
    }

    fun getGame(gameId: String): LiveData<DrinkingGame> {
        val cachedGame = this.gameCache.get(gameId)
        if (cachedGame != null)
            return cachedGame

        val game = MutableLiveData<DrinkingGame>()
        webservice.getGame(gameId).enqueue(object : Callback<DrinkingGame> {
            override fun onResponse(call: Call<DrinkingGame>, response: Response<DrinkingGame>) {
                game.value = response.body()
            }
            override fun onFailure(call: Call<DrinkingGame>, t: Throwable) {
                // There was an error while fetching data
            }
        })
        return game
    }

}