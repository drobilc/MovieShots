package com.moviedrinkers.moviedrinkers.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.ApiException
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import com.moviedrinkers.moviedrinkers.data.TrendingMovie
import com.moviedrinkers.moviedrinkers.fragment.*


class MainActivity : AppCompatActivity(), MainActivityEventListener {

    private lateinit var searchFragment: SearchFragment
    private lateinit var trendingMoviesFragment: TrendingMoviesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If savedInstanceState is not null, the activity has been loaded from memory
        // (this mostly happens when user rotates the screen)
        if (savedInstanceState != null)
            return

        // Create new search and trending movies fragment so the data is persisted
        searchFragment = SearchFragment.newInstance()
        searchFragment.setOnSearchListener(this)

        trendingMoviesFragment = TrendingMoviesFragment.newInstance()
        trendingMoviesFragment.setOnSearchListener(this)

        // Add SearchFragment as the first screen the user sees
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, searchFragment)
        transaction.commit()
    }

    override fun onMenuButtonClicked() {
        // When menu button is clicked in the search fragment, a list of trending movies should
        // be displayed. The TrendingMoviesFragment is first created when activity is created
        // and is then reused each time user clicks on the menu button.

        // Swap the current fragment to the TrendingMoviesFragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_down,
            R.anim.slide_out_down,
            R.anim.slide_in_up,
            R.anim.slide_out_up
        )
        transaction.replace(R.id.fragment_container, trendingMoviesFragment, TrendingMoviesFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMovieSelected(movie: TrendingMovie) {
        // When user clicks a movie from TrendingMoviesFragment, additional data about movie should
        // be displayed. Here, a new MovieDisplayFragment is created from TrendingMovie object.
        val movieFragment: Fragment = MovieDisplayFragment.newInstance(movie)
        (movieFragment as MovieDisplayFragment).setOnSearchListener(this)

        // Swap the current fragment to the PopularGamesFragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_down,
            R.anim.slide_out_down,
            R.anim.slide_in_up,
            R.anim.slide_out_up
        )
        transaction.replace(R.id.fragment_container, movieFragment, MovieDisplayFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onException(exception: ApiException) {
        //To change body of created functions use File | Settings | File Templates.
        // If the server returns an exception when generating game, the exception is displayed
        // using the ErrorFragment.
        val exceptionFragment: ErrorFragment = ErrorFragment.newInstance(exception)
        exceptionFragment.setOnRetryListener(this)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
        transaction.replace(R.id.fragment_container, exceptionFragment, ErrorFragment.TAG)
        transaction.commit()
    }

    override fun displayGame(game: DrinkingGame) {
        // When user clicks on a popular game inside the MovieDisplayFragment, a game must be
        // displayed. Factory method GameDisplayFragment.fromGame(game) can be used to create
        // a new GameDisplayFragment that will not load data but simply display game information.
        val displayGameFragment = GameDisplayFragment.fromGame(game)
        displayGameFragment.setListener(this)
        // Swap the current fragment to the GameDisplayFragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_up,
            R.anim.slide_out_up,
            R.anim.slide_in_down,
            R.anim.slide_out_down
        )
        transaction.replace(R.id.fragment_container, displayGameFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onGameSearched(selectedMovie: Movie?, movieTitle: String, numberOfShots: Int, numberOfPlayers: Int) {
        // When user types in movie title, number of shots and number of players and clicks on the
        // "Generate game" button, a new game must be generated. The GameDisplayFragment.fromMovie
        // factory method can be used to tell the server that the game must be generated.

        // Use GameDisplayFragment.fromMovie factory method to construct a new GameDisplayFragment
        // that will actually download data from server.
        val displayGameFragment: GameDisplayFragment = GameDisplayFragment.fromMovie(selectedMovie, movieTitle, numberOfShots, numberOfPlayers)
        displayGameFragment.setListener(this)

        // Swap the current fragment to the GameDisplayFragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_up,
            R.anim.slide_out_up,
            R.anim.slide_in_down,
            R.anim.slide_out_down
        )
        transaction.replace(R.id.fragment_container, displayGameFragment, GameDisplayFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onRetryButtonClicked() {
        // When user clicks on "Retry" button in ErrorFragment, a SearchFragment should be displayed
        // to allow user to try searching for new movie.
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_out,
            R.anim.fade_in
        )
        transaction.replace(R.id.fragment_container, searchFragment)
        transaction.commit()
    }
}
