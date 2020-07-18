package com.moviedrinkers.moviedrinkers.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.DrinkingGamePlayer
import kotlinx.android.synthetic.main.list_item_game_display.view.*
import kotlinx.android.synthetic.main.list_item_game_intro.view.*

class GameDisplayAdapter(private val game: DrinkingGame): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_INTRO = 1
        private const val TYPE_CUE = 2
    }

    class CueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val words = view.words
        private val player = view.player
        private val intoxication = view.intoxication
        private val colorStrip = view.color_strip

        fun bind(drinkingGamePlayer: DrinkingGamePlayer, position: Int, itemCount: Int) {
            words.text = drinkingGamePlayer.getWords()
            player.text = itemView.context.getString(R.string.player_number, position + 1)

            val occurrences = drinkingGamePlayer.getTotalOccurrences()
            intoxication.text = itemView.context.resources.getQuantityString(R.plurals.intoxication_level_shots, occurrences, occurrences)

            val hue = position * (360.0f / itemCount)
            val stripColor = Color.HSVToColor(floatArrayOf(hue, 0.6f, 1.0f))
            colorStrip.setBackgroundColor(stripColor)
        }
    }

    class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.drinking_game_title_text
        private val numberOfPlayers = view.game_players
        private val duration = view.movie_duration

        fun bind(game: DrinkingGame) {
            title.text = game.movie.title

            duration.text = game.movie.duration

            val players = game.players.size
            numberOfPlayers.text = itemView.context.resources.getQuantityString(R.plurals.players_number, players, players)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_INTRO)
            return IntroViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_game_intro,
                    parent,
                    false
                )
            )
        return CueViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_game_display,
                parent,
                false
            )
        )
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_INTRO) {
            (holder as IntroViewHolder).bind(this.game)
        } else {
            val player: DrinkingGamePlayer = game.players[position - 1]
            (holder as CueViewHolder).bind(player, position - 1, itemCount)
        }
    }

    override fun getItemCount() = game.players.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return TYPE_INTRO
        return TYPE_CUE
    }
}