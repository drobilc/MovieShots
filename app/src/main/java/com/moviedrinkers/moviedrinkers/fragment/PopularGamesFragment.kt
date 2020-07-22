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
import com.moviedrinkers.moviedrinkers.adapter.PopularGamesDisplayAdapter
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import kotlinx.android.synthetic.main.fragment_popular_games.view.*


class PopularGamesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PopularGamesDisplayAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    // Current list of games
    private val games: ArrayList<DrinkingGame> = arrayListOf()

    private fun refreshPopularGamesList(newItems: List<DrinkingGame>) {
        this.viewAdapter.addItems(newItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_popular_games, container, false)

        this.viewManager = LinearLayoutManager(context)
        this.viewAdapter = PopularGamesDisplayAdapter(this.games)

        this.recyclerView = view.popular_games_list.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(viewManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadGames(page)
            }
        }

        this.recyclerView.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)

        return view
    }

    private fun loadGames(page: Int) {
        val queue = VolleySingleton.getInstance(this.context!!.applicationContext).requestQueue

        val api = (this.context!!.applicationContext as MovieShotsApplication).getApi()
        val jsonRequest = api.getPopularGames(page, Response.Listener {

            // Parse list of DrinkingGame objects
            val newGames: ArrayList<DrinkingGame> = arrayListOf()
            for (i in 0 until it.length()) {
                val drinkingGameJson = it.getJSONObject(i)
                newGames.add(DrinkingGame.fromJson(drinkingGameJson))
            }

            refreshPopularGamesList(newGames)

        }, Response.ErrorListener {
        })

        queue.add(jsonRequest)
    }

    companion object {

        const val TAG: String = "POPULAR_GAMES_FRAGMENT"

        @JvmStatic
        fun newInstance() =
            PopularGamesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
