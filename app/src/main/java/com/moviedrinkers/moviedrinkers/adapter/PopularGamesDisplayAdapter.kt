package com.moviedrinkers.moviedrinkers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_popular_game.view.*

class PopularGamesDisplayAdapter(private val games: ArrayList<DrinkingGame>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract class Item
    class Intro: Item()
    class GameDisplay(val index: Int, val game: DrinkingGame): Item()

    private var items: ArrayList<Item> = arrayListOf()

    fun addItems(newItems: List<DrinkingGame>) {
        val lastIndex = this.items.size

        for ((index, game) in newItems.withIndex()) {
            this.items.add(GameDisplay(lastIndex + index + 1, game))
        }
        this.games.addAll(newItems)

        this.notifyItemRangeInserted(lastIndex, newItems.size)
    }

    init {
        this.items.add(Intro())
        for ((index, game) in games.withIndex())
            this.items.add(GameDisplay(index + 1, game))
    }

    class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cover = view.movie_cover
        val movieTitle = view.movie_title
        val movieDescription = view.movie_description
        val movieAdditionalInfo = view.movie_additional_info

        fun bind(game: DrinkingGame) {
            // Load movie cover into image view
            if (game.movie.cover.isNotBlank()) {
                Picasso.get().load(game.movie.cover).into(cover)
            } else {
                cover.setImageDrawable(null)
            }

            // Set movie title
            movieTitle.text = game.movie.title

            // Set movie description
            movieDescription.text = game.movie.overview

            // Set additional info about movie
            movieAdditionalInfo.text = movieAdditionalInfo.context.getString(R.string.review_text, game.rating, game.numberOfReviews)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INTRO -> IntroViewHolder(inflater.inflate(R.layout.list_item_popular_games_intro, parent, false))
            TYPE_GAME -> GameViewHolder(inflater.inflate(R.layout.list_item_popular_game, parent, false))
            else -> GameViewHolder(inflater.inflate(R.layout.list_item_popular_game, parent, false))
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = this.items[position]
        when (getItemViewType(position)) {
            TYPE_INTRO -> (holder as IntroViewHolder).bind()
            TYPE_GAME -> (holder as GameViewHolder).bind((item as GameDisplay).game)
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