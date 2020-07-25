package com.moviedrinkers.moviedrinkers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.TrendingMovie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_movie_display_game.view.*
import kotlinx.android.synthetic.main.list_item_movie_display_intro.view.*
import kotlinx.android.synthetic.main.list_item_trending_movie.view.movie_cover
import kotlinx.android.synthetic.main.list_item_trending_movie.view.movie_title

class MovieDisplayAdapter(private val movie: TrendingMovie, val itemClickListener: OnGameClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnGameClickListener {
        fun onGameClicked(game: DrinkingGame)
    }

    abstract class Item
    class Intro: Item()
    class GameDisplay(val index: Int, val game: DrinkingGame): Item()

    private var items: ArrayList<Item> = arrayListOf()

    init {
        this.items.add(Intro())
        for ((index, game) in movie.games.withIndex())
            this.items.add(GameDisplay(index + 1, game))
    }

    class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val movieCover = view.movie_cover
        val movieTitle = view.movie_title
        val movieOverview = view.movie_overview
        val movieYear = view.movie_year

        fun bind(movie: TrendingMovie) {

            if (movie.cover.isNotBlank()) {
                Picasso.get().load(movie.cover).into(movieCover)
            } else {
                movieCover.setImageDrawable(null)
            }

            movieTitle.text = movie.title
            movieOverview.text = movie.overview
            movieYear.text = movie.year.toString()

        }
    }

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val movieCover = view.movie_cover
        val movieTitle = view.movie_title
        val movieRating = view.movie_rating
        val movieNumberOfRatings = view.movie_number_of_ratings
        val movieNumberOfPlayers = view.movie_number_of_players
        val movieNumberOfShots = view.movie_number_of_shots
        val fullView = view

        fun bind(game: DrinkingGame, clickListener: OnGameClickListener) {

            if (game.movie.cover.isNotBlank()) {
                Picasso.get().load(game.movie.cover).into(movieCover)
            } else {
                movieCover.setImageDrawable(null)
            }

            movieTitle.text = game.movie.title

            movieRating.text = game.rating.toString()

            val numberOfPlayers: Int = game.players.size
            movieNumberOfPlayers.text = movieNumberOfPlayers.context.resources.getQuantityString(R.plurals.players_number, numberOfPlayers, numberOfPlayers)

            val numberOfShots = game.numberOfShots()
            movieNumberOfShots.text = movieNumberOfShots.context.resources.getQuantityString(R.plurals.intoxication_level_shots, numberOfShots, numberOfShots)

            movieNumberOfRatings.text = movieNumberOfRatings.context.resources.getQuantityString(R.plurals.number_of_ratings, game.numberOfReviews, game.numberOfReviews)

            fullView.setOnClickListener {
                clickListener.onGameClicked(game)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INTRO -> IntroViewHolder(inflater.inflate(R.layout.list_item_movie_display_intro, parent, false))
            TYPE_GAME -> GameViewHolder(inflater.inflate(R.layout.list_item_movie_display_game, parent, false))
            else -> GameViewHolder(inflater.inflate(R.layout.list_item_movie_display_game, parent, false))
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = this.items[position]
        when (getItemViewType(position)) {
            TYPE_INTRO -> (holder as IntroViewHolder).bind(this.movie)
            TYPE_GAME -> (holder as GameViewHolder).bind((item as GameDisplay).game, itemClickListener)
        }
    }

    override fun getItemCount() = this.items.size

    override fun getItemViewType(position: Int): Int {
        val item = this.items[position]
        if (item is Intro)
            return TYPE_INTRO
        return TYPE_GAME
    }

    companion object {
        private const val TYPE_INTRO = 1
        private const val TYPE_GAME = 2
    }

}