package com.moviedrinkers.moviedrinkers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.network.VolleySingleton
import kotlinx.android.synthetic.main.list_item_suggestion.view.*


class MovieSuggestionsAdapter(private val context: Context) : BaseAdapter(), Filterable {

    private val suggestions: ArrayList<String> = arrayListOf()
    private val filter: CustomFilter = CustomFilter()

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.list_item_suggestion, parent, false)
        view.movie_title.text = this.suggestions[position]
        return view
    }

    override fun getItem(position: Int): Any {
        return this.suggestions[position]
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
            val results = FilterResults()

            // If user has not typed enough characters, return an empty filter results
            if (constraint == null)
                return results

            val keywords = constraint.toString()

            // Use Volley to get suggestions from the server
            val queue = VolleySingleton.getInstance(context.applicationContext).requestQueue

            // First, cancel all previous requests with MOVIE_SUGGESTIONS_TAG
            queue.cancelAll {
                it.tag == VolleySingleton.MOVIE_SUGGESTIONS_TAG
            }

            val url = "http://alcohol.stvari.si/suggestions?keywords=$keywords"
            val jsonRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener {
                    // The server returned movie suggestions, display them
                    val newSuggestions = arrayListOf<String>()

                    val suggestionsArray = it.getJSONArray(1)
                    for (i in 0 until suggestionsArray.length()) {
                        val suggestion = suggestionsArray.getString(i)
                        newSuggestions.add(suggestion)
                    }

                    suggestions.clear()
                    suggestions.addAll(newSuggestions)
                    notifyDataSetChanged()
                }, Response.ErrorListener {
                    // There was an error, ignore it
                }
            )
            jsonRequest.tag = VolleySingleton.MOVIE_SUGGESTIONS_TAG
            queue.add(jsonRequest)


            val suggestions = arrayListOf<String>("Harry Potter 1", "Harry Potter 2")
            results.count = suggestions.size
            results.values = suggestions

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            // Do nothing, the results will be handled in the perform filtering
        }

    }

}