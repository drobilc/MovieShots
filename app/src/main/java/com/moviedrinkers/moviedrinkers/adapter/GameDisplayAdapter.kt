package com.moviedrinkers.moviedrinkers.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.Api
import com.moviedrinkers.moviedrinkers.data.DrinkingCue
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.DrinkingGamePlayer
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import kotlinx.android.synthetic.main.list_item_bonus_word.view.*
import kotlinx.android.synthetic.main.list_item_game_display.view.*
import kotlinx.android.synthetic.main.list_item_game_display.view.words
import kotlinx.android.synthetic.main.list_item_game_intro.view.*
import kotlinx.android.synthetic.main.list_item_game_rating.view.*

abstract class Item
class Intro: Item()
class PlayerCue(val position: Int, val player: DrinkingGamePlayer): Item()
class BonusIntro: Item()
class BonusCue(val position: Int, var drinkingCue: DrinkingCue): Item()
class Spacer: Item()
class GameRating(val game: DrinkingGame): Item()

class GameDisplayAdapter(private val game: DrinkingGame): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<Item> = arrayListOf()

    init {
        this.items.add(Intro())
        for ((index, player) in game.players.withIndex())
            this.items.add(PlayerCue(index + 1, player))

        if (game.bonusWords.size > 0) {
            this.items.add(BonusIntro())
            for ((index, bonusWord) in game.bonusWords.withIndex())
                this.items.add(BonusCue(index, bonusWord))
        }

        this.items.add(GameRating(game))

        this.items.add(Spacer())
    }

    class CueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val words = view.words
        private val player = view.player
        private val intoxication = view.intoxication
        private val colorStrip = view.color_strip

        fun bind(drinkingGamePlayer: DrinkingGamePlayer, position: Int, itemCount: Int) {
            words.text = drinkingGamePlayer.getWords()
            player.text = itemView.context.getString(R.string.player_number, position)

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

    class BonusIntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(game: DrinkingGame) {

        }
    }

    class BonusCueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val leftColorStrip = view.color_strip_left
        private val word = view.word

        fun bind(cue: BonusCue, itemCount: Int) {
            word.text = cue.drinkingCue.word

            val hue = cue.position * (360.0f / itemCount)
            val stripColor = Color.HSVToColor(floatArrayOf(hue, 0.6f, 1.0f))
            leftColorStrip.setBackgroundColor(stripColor)
        }
    }

    class SpacerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    class GameRatingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val ratingBar = view.game_rating
        private val thanksText = view.rating_thanks
        fun bind(game: DrinkingGame) {
            ratingBar.setOnRatingBarChangeListener { _, rating: Float, _ ->
                // The rating has changed, send data to api
                val queue = VolleySingleton.getInstance(view.context.applicationContext).requestQueue
                val jsonRequest = Api.rateGame(game.id, rating, Response.Listener {
                    thanksText.visibility = View.VISIBLE
                })
                queue.add(jsonRequest)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INTRO -> IntroViewHolder(inflater.inflate(R.layout.list_item_game_intro, parent, false))
            TYPE_CUE -> CueViewHolder(inflater.inflate(R.layout.list_item_game_display, parent, false))
            TYPE_BONUS_WORDS_INTRO -> BonusIntroViewHolder(inflater.inflate(R.layout.list_item_bonus_intro, parent, false))
            TYPE_BONUS_WORD -> BonusCueViewHolder(inflater.inflate(R.layout.list_item_bonus_word, parent, false))
            TYPE_SPACER -> SpacerViewHolder(inflater.inflate(R.layout.list_item_spacer, parent, false))
            TYPE_RATE_GAME -> GameRatingViewHolder(inflater.inflate(R.layout.list_item_game_rating, parent, false))
            else -> CueViewHolder(inflater.inflate(R.layout.list_item_game_display, parent, false))
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = this.items[position]
        when (getItemViewType(position)) {
            TYPE_INTRO -> (holder as IntroViewHolder).bind(this.game)
            TYPE_CUE -> (holder as CueViewHolder).bind((item as PlayerCue).player, item.position, game.players.size)
            TYPE_BONUS_WORDS_INTRO -> (holder as BonusIntroViewHolder).bind(this.game)
            TYPE_BONUS_WORD -> (holder as BonusCueViewHolder).bind((item as BonusCue), game.bonusWords.size)
            TYPE_SPACER -> (holder as SpacerViewHolder).bind()
            TYPE_RATE_GAME -> (holder as GameRatingViewHolder).bind(game)
        }
    }

    override fun getItemCount() = this.items.size

    override fun getItemViewType(position: Int): Int {
        val item = this.items[position]
        if (item is Intro)
            return TYPE_INTRO
        if (item is PlayerCue)
            return TYPE_CUE
        if (item is BonusIntro)
            return TYPE_BONUS_WORDS_INTRO
        if (item is BonusCue)
            return TYPE_BONUS_WORD
        if (item is GameRating)
            return TYPE_RATE_GAME
        return TYPE_SPACER
    }

    companion object {
        private const val TYPE_INTRO = 1
        private const val TYPE_CUE = 2
        private const val TYPE_BONUS_WORDS_INTRO = 4
        private const val TYPE_BONUS_WORD = 8
        private const val TYPE_SPACER = 16
        private const val TYPE_RATE_GAME = 32
    }

}