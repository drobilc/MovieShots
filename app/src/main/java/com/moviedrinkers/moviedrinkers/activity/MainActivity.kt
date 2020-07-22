package com.moviedrinkers.moviedrinkers.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.android.volley.*
import com.moviedrinkers.moviedrinkers.MovieShotsApplication
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.ApiException
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import com.moviedrinkers.moviedrinkers.fragment.ErrorFragment
import com.moviedrinkers.moviedrinkers.fragment.GameDisplayFragment
import com.moviedrinkers.moviedrinkers.fragment.TrendingMoviesFragment
import com.moviedrinkers.moviedrinkers.fragment.SearchFragment
import com.moviedrinkers.moviedrinkers.network.VolleySingleton


class MainActivity : AppCompatActivity(), SearchFragment.OnSearch {

    private lateinit var searchFragment: SearchFragment
    private lateinit var displayGameFragment: GameDisplayFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create both fragments, pass the on search listener to search fragment
        searchFragment = SearchFragment.newInstance()
        searchFragment.setOnSearchListener(this)

        displayGameFragment = GameDisplayFragment.newInstance()

        // Add the search fragment to screen
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, searchFragment)
        transaction.commit()

    }

    override fun onMenuButtonClicked() {
        displayPopularGamesList()
    }

    private fun displayPopularGamesList() {
        // If popular games list fragment already exists, use it, otherwise create a new instance
        var popularGamesFragment: Fragment? = supportFragmentManager.findFragmentByTag(TrendingMoviesFragment.TAG)
        if (popularGamesFragment == null) {
            popularGamesFragment = TrendingMoviesFragment.newInstance()
        }

        // Swap the current fragment to the PopularGamesFragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_down,
            R.anim.slide_out_down,
            R.anim.slide_in_up,
            R.anim.slide_out_up
        )
        transaction.replace(R.id.fragment_container, popularGamesFragment, TrendingMoviesFragment.TAG)
        transaction.addToBackStack(TrendingMoviesFragment.TAG)
        transaction.commit()
    }

    private fun displayExceptionFragment(exception: ApiException) {
        // Swap the current fragment to the ExceptionFragment
        val exceptionFragment: ErrorFragment =
            ErrorFragment.newInstance(
                exception
            )
        exceptionFragment.setOnRetryListener(this)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
        transaction.replace(R.id.fragment_container, exceptionFragment)
        transaction.commit()
    }

    private fun generateGame(selectedMovie: Movie?, movieTitle: String, numberOfShots: Int, numberOfPlayers: Int) {
        displayGameFragment.displayLoadingScreen(true)

        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue

        val api = (this.applicationContext as MovieShotsApplication).getApi()
        val jsonRequest = api.generateGame(selectedMovie, movieTitle, numberOfShots, numberOfPlayers, Response.Listener {
            if (it.has("error")) {
                val exception =
                    ApiException.fromJson(
                        it
                    )
                displayExceptionFragment(exception)
            } else {
                // Construct a new drinking game object from received JSON
                val game: DrinkingGame =
                    DrinkingGame.fromJson(
                        it
                    )

                // Notify the fragment that the game has been generated, so it gets displayed
                displayGameFragment.displayLoadingScreen(false)
                displayGameFragment.displayGame(game)
            }
        }, Response.ErrorListener {
            val exception =
                when (it) {
                    is NoConnectionError -> ApiException(
                        getString(R.string.exception_no_connection),
                        -1
                    )
                    is NetworkError -> ApiException(
                        getString(R.string.exception_network),
                        -1
                    )
                    is ParseError -> ApiException(
                        getString(R.string.exception_parse),
                        -1
                    )
                    is ServerError -> ApiException(
                        getString(R.string.exception_server),
                        -1
                    )
                    is TimeoutError -> ApiException(
                        getString(R.string.exception_timeout),
                        -1
                    )
                    is AuthFailureError -> ApiException(
                        getString(R.string.exception_authentication),
                        -1
                    )
                    else -> ApiException(
                        getString(R.string.exception_default_message),
                        -1
                    )
                }
            displayExceptionFragment(exception)
        })
        queue.add(jsonRequest)
    }

    override fun onGameSearched(selectedMovie: Movie?, movieTitle: String, numberOfShots: Int, numberOfPlayers: Int) {
        // This function is called when the search fragment button is clicked
        generateGame(selectedMovie, movieTitle, numberOfShots, numberOfPlayers)

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

    override fun onRetry() {
        // Swap the current fragment to the SearchFrgment
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
