package com.moviedrinkers.moviedrinkers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.android.volley.Response
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.Api
import com.moviedrinkers.moviedrinkers.data.Movie
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_suggestion.view.*


class MovieSuggestionsAdapter(private val context: Context) : BaseAdapter(), Filterable {

    private val suggestions: ArrayList<Movie> = arrayListOf()
    private val filter: CustomFilter = CustomFilter()

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val newView = convertView ?: inflater.inflate(R.layout.list_item_suggestion, parent, false)
        val suggestion = this.suggestions[position]

        newView.movie_title.text = suggestion.title

        if (suggestion.year != 0)
            newView.movie_year.text = suggestion.year.toString()
        else
            newView.movie_year.text = ""

        if (suggestion.cover.isNotBlank()) {
            Picasso.get().load(suggestion.cover).into(newView.movie_cover)
        } else {
            newView.movie_cover.setImageDrawable(null)
        }

        return newView
    }

    override fun getItem(position: Int): Any {
        return this.suggestions[position].title
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.suggestions.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    inner class CustomFilter: Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            // If user has not typed enough characters, return an empty filter results
            if (constraint == null)
                return FilterResults()

            val keywords = constraint.toString()

            // Use Volley to get suggestions from the server
            val queue = VolleySingleton.getInstance(context.applicationContext).requestQueue

            // First, cancel all previous requests with MOVIE_SUGGESTIONS_TAG
            queue.cancelAll {
                it.tag == VolleySingleton.MOVIE_SUGGESTIONS_TAG
            }

            val jsonRequest = Api.getSuggestions(keywords, Response.Listener {
                // The server returned movie suggestions, display them
                val newSuggestions = arrayListOf<Movie>()

                for (i in 0 until it.length()) {
                    val suggestion = it.getJSONObject(i)
                    val movie = Movie.fromJson(suggestion)
                    newSuggestions.add(movie)
                }

                suggestions.clear()
                suggestions.addAll(newSuggestions)
                notifyDataSetChanged()
            }, Response.ErrorListener {
                // There was an error, ignore it
            })
            queue.add(jsonRequest)

            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            // Do nothing, the results will be handled in the perform filtering
        }

    }

}