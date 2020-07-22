package com.moviedrinkers.moviedrinkers.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.jem.rubberpicker.RubberSeekBar
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.adapter.MovieSuggestionsAdapter
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import com.moviedrinkers.moviedrinkers.data.Movie
import com.moviedrinkers.moviedrinkers.data.TrendingMovie
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : Fragment() {

    interface OnSearch {
        fun onGameSearched(selectedMovie: Movie?, movieTitle: String, numberOfShots: Int, numberOfPlayers: Int)
        fun onMenuButtonClicked()
        fun onMovieSelected(movie: TrendingMovie)
        fun displayGame(game: DrinkingGame)
        fun onRetry()
    }

    private lateinit var callback: OnSearch

    fun setOnSearchListener(callback: OnSearch) {
        this.callback = callback
    }

    private var currentIntoxicationLevel: Int = 2
    private var currentNumberOfPlayers: Int = 1

    // If user clicks on the movie suggestion, this variable holds additional movie information
    // such as movie id (which is not possible if user simply types in the movie name)
    private var selectedMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    private fun getIntoxicationLevelText(value: Int): String {
        if (value <= 3)
            return getString(R.string.intoxication_chatty)
        else if (value <= 5)
            return getString(R.string.intoxication_tipsy)
        else if (value <= 8)
            return getString(R.string.intoxication_friendly)
        else if (value <= 16)
            return getString(R.string.intoxication_very_drunk)
        return getString(R.string.intoxication_wasted)
    }

    private fun getNumberOfPlayersText(value: Int): String {
        if (value <= 1)
            return getString(R.string.players_just_me)
        if (value <= 3)
            return getString(R.string.players_a_few)
        else if (value <= 6)
            return getString(R.string.players_quite_a_lot)
        else if (value <= 10)
            return getString(R.string.players_party)
        return getString(R.string.players_a_shitload)
    }

    fun refreshSliders() {
        val numberOfShotsText = resources.getQuantityString(R.plurals.intoxication_level_shots, currentIntoxicationLevel, currentIntoxicationLevel)
        intoxication_level?.text = getIntoxicationLevelText(currentIntoxicationLevel)
        number_of_shots?.text = numberOfShotsText

        val numberOfPlayersText = resources.getQuantityString(R.plurals.people_number, currentNumberOfPlayers, currentNumberOfPlayers)
        number_of_players?.text = getNumberOfPlayersText(currentNumberOfPlayers)
        number_of_players_number?.text = numberOfPlayersText
    }

    override fun onResume() {
        super.onResume()
        refreshSliders()
        intoxication_level_picker.setCurrentValue(currentIntoxicationLevel)
        number_of_players_picker.setCurrentValue(currentNumberOfPlayers)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // val view = inflater.inflate(R.layout.fragment_game_display, container, false)
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // When the activity is created, use the movie selection title from string resources, because it can contain HTML strings (which XML apparently can't)
        view.movie_intro.text = getText(R.string.movie_selection_title)

        refreshSliders()

        view.menu_button.setOnClickListener {
            this.callback.onMenuButtonClicked()
        }

        view.intoxication_level_picker.setOnRubberSeekBarChangeListener(object : RubberSeekBar.OnRubberSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RubberSeekBar, value: Int, fromUser: Boolean) {
                currentIntoxicationLevel = value
                refreshSliders()
            }
            override fun onStartTrackingTouch(seekBar: RubberSeekBar) {}
            override fun onStopTrackingTouch(seekBar: RubberSeekBar) {}
        })

        view.number_of_players_picker.setOnRubberSeekBarChangeListener(object : RubberSeekBar.OnRubberSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RubberSeekBar, value: Int, fromUser: Boolean) {
                currentNumberOfPlayers = value
                refreshSliders()
            }
            override fun onStartTrackingTouch(seekBar: RubberSeekBar) {}
            override fun onStopTrackingTouch(seekBar: RubberSeekBar) {}
        })

        view.generate_game_button.setOnClickListener {
            val movieTitle: String = view.movie_title_input.text.toString()
            val numberOfShots = view.intoxication_level_picker.getCurrentValue()
            val numberOfPlayers = view.number_of_players_picker.getCurrentValue()

            this.callback.onGameSearched(selectedMovie, movieTitle, numberOfShots, numberOfPlayers)
        }

        // Autocomplete from the server
        val adapter = MovieSuggestionsAdapter(context!!)
        view.movie_title_input.setAdapter(adapter)

        // Every time the text changes, the selected movie resets. This ensures that only if user
        // selects movie from dropdown, the movie is not null.
        view.movie_title_input.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                selectedMovie = null
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                selectedMovie = null
            }
            override fun afterTextChanged(s: Editable) {
                selectedMovie = null
            }
        })

        view.movie_title_input.onItemClickListener = AdapterView.OnItemClickListener{
                _, _, position, _ ->

            // UPDATE selected movie to match clicked movie
            selectedMovie = adapter.getMovie(position)

            // Hide the keyboard because user has selected an item
            val viewInFocus = activity?.currentFocus
            viewInFocus?.let { v ->
                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        return view
    }

    companion object {

        const val TAG: String = "SEARCH_FRAGMENT"

        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
