package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.adapter.MovieDisplayAdapter
import com.moviedrinkers.moviedrinkers.data.TrendingMovie
import kotlinx.android.synthetic.main.fragment_movie_display.view.*

class MovieDisplayFragment : Fragment() {

    private var movie: TrendingMovie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getParcelable("movie")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie_display, container, false)

        val viewManager = LinearLayoutManager(context)
        val viewAdapter = MovieDisplayAdapter(this.movie!!)

        val recyclerView = view.movie_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return view
    }

    companion object {

        const val TAG: String = "MOVIE_FRAGMENT"

        @JvmStatic
        fun newInstance(movie: TrendingMovie) =
            MovieDisplayFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("movie", movie)
                }
            }
    }
}
