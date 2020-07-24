package com.moviedrinkers.moviedrinkers.activity

import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import com.moviedrinkers.moviedrinkers.data.TrendingMovie

// Interface that allows fragments to communicate with this activity
interface MainActivityEventListener {
    // Callback for when user clicks on "Generate game" button in SearchFragment
    fun onGameSearched(selectedMovie: Movie?, movieTitle: String, numberOfShots: Int, numberOfPlayers: Int)

    // Callback for when game needs to be displayed in GameDisplayFragment
    fun displayGame(game: DrinkingGame)

    // Callback for when user clicks the menu button in SearchFragment
    fun onMenuButtonClicked()

    // Function that will get called when user selects a movie in the trending movie section
    fun onMovieSelected(movie: TrendingMovie)

    // Callback for when user click "Retry" button in error fragment
    fun onRetryButtonClicked()
}