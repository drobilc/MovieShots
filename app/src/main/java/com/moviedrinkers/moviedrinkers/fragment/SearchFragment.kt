package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.jem.rubberpicker.RubberSeekBar
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.adapter.MovieSuggestionsAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    interface OnSearch {
        fun onGameSearched(movieTitle: String, numberOfShots: Int, numberOfPlayers: Int)
        fun onRetry()
    }

    internal lateinit var callback: OnSearch

    fun setOnSearchListener(callback: OnSearch) {
        this.callback = callback
    }

    private var currentIntoxicationLevel: Int = 2
    private var currentNumberOfPlayers: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    fun getIntoxicationLevelText(value: Int): String {
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

    fun getNumberOfPlayersText(value: Int): String {
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

            this.callback.onGameSearched(movieTitle, numberOfShots, numberOfPlayers)
        }

        // Autocomplete from the server
        val adapter = MovieSuggestionsAdapter(context!!)
        view.movie_title_input.setAdapter(adapter)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
