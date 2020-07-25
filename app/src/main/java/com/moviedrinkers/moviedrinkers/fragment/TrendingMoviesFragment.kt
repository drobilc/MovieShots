package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.moviedrinkers.moviedrinkers.EndlessRecyclerViewScrollListener
import com.moviedrinkers.moviedrinkers.MovieShotsApplication
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.activity.MainActivityEventListener
import com.moviedrinkers.moviedrinkers.adapter.PopularMoviesDisplayAdapter
import com.moviedrinkers.moviedrinkers.data.TrendingMovie
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import kotlinx.android.synthetic.main.fragment_popular_games.view.*


class TrendingMoviesFragment : Fragment(),
    PopularMoviesDisplayAdapter.OnTrendingMovieClickListener {

    private lateinit var callback: MainActivityEventListener

    fun setOnSearchListener(callback: MainActivityEventListener) {
        this.callback = callback
    }

    private var currentPage: Int = 1
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PopularMoviesDisplayAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    // Current list of games
    private val movies: ArrayList<TrendingMovie> = arrayListOf()

    override fun onTrendingMovieClicked(movie: TrendingMovie) {
        this.callback.onMovieSelected(movie)
    }

    private fun refreshPopularGamesList(newItems: List<TrendingMovie>) {
        this.viewAdapter.addItems(newItems)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("movies", this.movies)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If savedInstanceState is not null, the orientation probably changed.
        // Load a list of movies from previous state and display it.
        if (savedInstanceState != null) {
            val previousMovies: ArrayList<TrendingMovie>? = savedInstanceState.getParcelableArrayList("movies")
            if (previousMovies != null) {
                movies.clear()
                movies.addAll(previousMovies)
            }
        }

        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_popular_games, container, false)

        this.viewManager = LinearLayoutManager(context)
        this.viewAdapter = PopularMoviesDisplayAdapter(this.movies, this)

        this.recyclerView = view.popular_games_list.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(viewManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadGames(currentPage)
            }
        }

        this.recyclerView.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)

        return view
    }

    private fun loadGames(page: Int) {
        val queue = VolleySingleton.getInstance(this.context!!.applicationContext).requestQueue

        val api = (this.context!!.applicationContext as MovieShotsApplication).getApi()
        val jsonRequest = api.getTrendingMovies(page, Response.Listener {

            // Parse list of TrendingMovie objects
            val newMovies: ArrayList<TrendingMovie> = arrayListOf()
            for (i in 0 until it.length()) {
                val trendingMovieJson = it.getJSONObject(i)
                newMovies.add(TrendingMovie.fromJson(trendingMovieJson))
            }

            refreshPopularGamesList(newMovies)

            currentPage += 1

        }, Response.ErrorListener {
            currentPage += 1
        })

        queue.add(jsonRequest)
    }

    companion object {

        const val TAG: String = "TRENDING_MOVIES_FRAGMENT"

        @JvmStatic
        fun newInstance() =
            TrendingMoviesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}
