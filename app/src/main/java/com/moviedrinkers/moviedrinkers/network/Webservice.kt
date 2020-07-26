package com.moviedrinkers.moviedrinkers.network

import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Webservice {

    @GET("/game/generate")
    fun generateGameByMovieId(
        @Query("movie_id") movieId: String,
        @Query("intoxication") numberOfShots: Int,
        @Query("players") numberOfPlayers: Int
    ): Call<DrinkingGame>

    @GET("/game/generate")
    fun generateGameByMovieTitle(
        @Query("movie") movieTitle: String,
        @Query("intoxication") numberOfShots: Int,
        @Query("players") numberOfPlayers: Int
    ): Call<DrinkingGame>

    @GET("/movies/{movie}")
    fun getUser(@Path("movie") movieId: String): Call<Movie>

    @GET("/game/{game}")
    fun getGame(@Path("game") gameId: String): Call<DrinkingGame>

    @GET("/game/{game}/vote")
    fun voteGame(@Path("game") gameId: String): Call<DrinkingGame>

    @GET("/movies/trending")
    fun trendingMovies(@Query("page") page: Int): Call<List<Movie>>


}