package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.TrendingMovie

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
