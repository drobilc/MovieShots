package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.moviedrinkers.moviedrinkers.MovieShotsApplication
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.activity.MainActivityEventListener
import com.moviedrinkers.moviedrinkers.adapter.GameDisplayAdapter
import com.moviedrinkers.moviedrinkers.data.ApiException
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import kotlinx.android.synthetic.main.fragment_game_display.*


class GameDisplayFragment : Fragment() {

    private val handler: Handler = Handler()

    private var loadingMessages: Array<String>? = null
    private var loadingMessageIndex: Int = 0
    private var isLoading: Boolean = false

    private lateinit var callback: MainActivityEventListener
    fun setListener(callback: MainActivityEventListener) {
        this.callback = callback
    }

    private val run: Runnable = object : Runnable {
        override fun run() {
            loading_text?.text = loadingMessages!![loadingMessageIndex]
            loadingMessageIndex = (loadingMessageIndex + 1) % loadingMessages!!.size

            // Repeat until isLoading is set to true
            if (isLoading)
                handler.postDelayed(this, 2000)
        }
    }

    private fun displayLoadingScreen(isLoading: Boolean) {
        this.isLoading = isLoading

        // Set the progress_bar visibility if it has been loaded
        if (progress_bar != null) {
            val visibility = if (isLoading) View.VISIBLE else View.GONE
            progress_bar.visibility = visibility
        }
    }

    private fun displayGame(game: DrinkingGame, animationOffset: Long = 0L) {
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = GameDisplayAdapter(game)

        val recyclerView = generated_game.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        this.displayLoadingScreen(false)

        val slideUp = AnimationUtils.loadAnimation(context!!.applicationContext,
            R.anim.slide_in_up
        )
        slideUp.startOffset = animationOffset
        recyclerView.startAnimation(slideUp)
    }

    private fun displayExceptionFragment(exception: ApiException) {
        this.callback.onException(exception)
    }

    private fun loadGameById(gameId: String) {
        Log.v("MovieShots", "Loading game by id: " + gameId)

    }

    private fun loadGameByMovie(selectedMovie: Movie?, movieTitle: String, numberOfShots: Int, numberOfPlayers: Int) {
        this.displayLoadingScreen(true)

        val applicationContext = context!!.applicationContext

        val queue = VolleySingleton.getInstance(applicationContext).requestQueue
        val api = (applicationContext as MovieShotsApplication).getApi()

        val jsonRequest = api.generateGame(selectedMovie, movieTitle, numberOfShots, numberOfPlayers, Response.Listener {
            if (it.has("error")) {
                val exception = ApiException.fromJson(it)
                displayExceptionFragment(exception)
            } else {
                // Construct a new drinking game object from received JSON
                val game: DrinkingGame = DrinkingGame.fromJson(it)

                // Notify the fragment that the game has been generated, so it gets displayed
                this.displayLoadingScreen(false)
                this.displayGame(game)
            }
        }, Response.ErrorListener {
            val exception = when (it) {
                    is NoConnectionError -> ApiException(getString(R.string.exception_no_connection), -1)
                    is NetworkError -> ApiException(getString(R.string.exception_network), -1)
                    is ParseError -> ApiException(getString(R.string.exception_parse), -1)
                    is ServerError -> ApiException(getString(R.string.exception_server), -1)
                    is TimeoutError -> ApiException(getString(R.string.exception_timeout), -1)
                    is AuthFailureError -> ApiException(getString(R.string.exception_authentication), -1)
                    else -> ApiException(getString(R.string.exception_default_message), -1)
                }
            displayExceptionFragment(exception)
        })
        queue.add(jsonRequest)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_game_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The GameDisplayFragment can be created using static factory methods
        //     * fromGameId
        //     * fromGame
        //     * fromMovie
        val gameObject: DrinkingGame? = arguments?.getParcelable("game")
        if (gameObject != null) {
            // Simply display game from DrinkingGame object
            return this.displayGame(gameObject, 800)
        }

        // If game_id is passed as argument, then load game by its id and exit onCreate method
        val gameId: String? = arguments?.getString("game_id")
        if (gameId != null)
            return this.loadGameById(gameId)

        // Otherwise, we are trying to generate a new game from movie title
        arguments?.let {
            val movie: Movie? = it.getParcelable("movie")
            val movieTitle: String? = it.getString("movie_title")

            // If no movie or movieTitle is present, exit
            if (movie == null && movieTitle == null) {
                return
            }

            val numberOfShots: Int = it.getInt("movie_number_of_shots")
            val numberOfPlayers: Int = it.getInt("movie_number_of_players")
            this.loadGameByMovie(movie, movieTitle!!, numberOfShots, numberOfPlayers)
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {

        const val TAG: String = "GAME_DISPLAY_FRAGMENT"

        @JvmStatic
        fun fromGameId(gameId: String) = GameDisplayFragment().apply {
            arguments = Bundle().apply {
                putString("game_id", gameId)
            }
        }

        @JvmStatic
        fun fromGame(game: DrinkingGame) = GameDisplayFragment().apply {
            arguments = Bundle().apply {
                putParcelable("game", game)
            }
        }

        @JvmStatic
        fun fromMovie(
            selectedMovie: Movie?,
            movieTitle: String,
            numberOfShots: Int,
            numberOfPlayers: Int
        ): GameDisplayFragment {
            return GameDisplayFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("movie", selectedMovie)
                    putString("movie_title", movieTitle)
                    putInt("movie_number_of_shots", numberOfShots)
                    putInt("movie_number_of_players", numberOfPlayers)
                }
            }
        }
    }

}
