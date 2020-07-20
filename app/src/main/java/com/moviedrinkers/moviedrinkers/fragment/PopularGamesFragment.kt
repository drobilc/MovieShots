package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.adapter.PopularGamesDisplayAdapter
import com.moviedrinkers.moviedrinkers.data.DrinkingCue
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.DrinkingGamePlayer
import com.moviedrinkers.moviedrinkers.data.Movie
import kotlinx.android.synthetic.main.fragment_popular_games.*
import kotlinx.android.synthetic.main.fragment_popular_games.view.*


class PopularGamesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    fun displayGames(view: View, games: List<DrinkingGame>) {
        viewManager = LinearLayoutManager(context)
        viewAdapter = PopularGamesDisplayAdapter(games)

        recyclerView = view.popular_games_list.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_popular_games, container, false)

        val inception: Movie = Movie("test", "Inception", 2010, "1h 20min", "https://image.tmdb.org/t/p/w92/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg")
        val cues: List<DrinkingCue> = arrayListOf<DrinkingCue>(
            DrinkingCue("real", 8)
        )
        val player: DrinkingGamePlayer = DrinkingGamePlayer(cues)

        val game: DrinkingGame = DrinkingGame("game", inception, arrayListOf<DrinkingGamePlayer>(player), arrayListOf<DrinkingCue>())

        val games = arrayListOf<DrinkingGame>(game, game, game)
        displayGames(view, games)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PopularGamesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
