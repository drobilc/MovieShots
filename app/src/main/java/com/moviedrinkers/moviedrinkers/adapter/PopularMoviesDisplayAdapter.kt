package com.moviedrinkers.moviedrinkers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.TrendingMovie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_trending_movie.view.*

class PopularMoviesDisplayAdapter(private val movies: ArrayList<TrendingMovie>, val itemClickListener: OnTrendingMovieClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnTrendingMovieClickListener {
        fun onTrendingMovieClicked(movie: TrendingMovie)
    }

    abstract class Item
    class Intro: Item()
    class Loading: Item()
    class MovieDisplay(val index: Int, val movie: TrendingMovie): Item()

    private var items: ArrayList<Item> = arrayListOf()
    private var loadingItem: Loading? = null

    fun addItems(newItems: List<TrendingMovie>) {
        // When appending new items to this adapter, first check if there is loading indicator in
        // the list. If there is, remove it and notify adapter that it has been removed.
        if (loadingItem != null) {
            val index = this.items.indexOf((loadingItem as Loading))
            if (index != -1) {
                this.items.removeAt(index)
                loadingItem = null
                this.notifyItemRemoved(index)
            }
        }

        // After loading indicator has been removed, add new items to list.
        val lastIndex = this.items.size

        for ((index, movie) in newItems.withIndex()) {
            this.items.add(MovieDisplay(lastIndex + index + 1, movie))
        }
        this.movies.addAll(newItems)

        this.notifyItemRangeInserted(lastIndex, newItems.size)
    }

    init {
        this.items.add(Intro())

        if (this.movies.size <= 0) {
            loadingItem = Loading()
            this.items.add(loadingItem as Loading)
        }

        for ((index, movie) in movies.withIndex())
            this.items.add(MovieDisplay(index + 1, movie))
    }

    class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val fullView = view
        val cover = view.movie_cover
        val movieTitle = view.movie_title
        val movieDescription = view.movie_description
        val movieAdditionalInfo = view.movie_additional_info

        fun bind(movie: TrendingMovie, clickListener: OnTrendingMovieClickListener) {
            // Load movie cover into image view
            if (movie.cover.isNotBlank()) {
                Picasso.get().load(movie.cover).into(cover)
            } else {
                cover.setImageDrawable(null)
            }

            // Set movie title
            movieTitle.text = movie.title

            // Set movie description
            movieDescription.text = movie.overview

            // Set additional info about movie
            movieAdditionalInfo.text = movieAdditionalInfo.context.getString(R.string.review_text, movie.rating, movie.numberOfReviews)

            fullView.setOnClickListener {
                clickListener.onTrendingMovieClicked(movie)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INTRO -> IntroViewHolder(inflater.inflate(R.layout.list_item_trending_movie_intro, parent, false))
            TYPE_MOVIE -> MovieViewHolder(inflater.inflate(R.layout.list_item_trending_movie, parent, false))
            TYPE_LOADING -> LoadingViewHolder(inflater.inflate(R.layout.list_item_trending_movie_loading, parent, false))
            else -> MovieViewHolder(inflater.inflate(R.layout.list_item_trending_movie, parent, false))
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = this.items[position]
        when (getItemViewType(position)) {
            TYPE_INTRO -> (holder as IntroViewHolder).bind()
            TYPE_MOVIE -> (holder as MovieViewHolder).bind((item as MovieDisplay).movie, itemClickListener)
            TYPE_LOADING -> (holder as LoadingViewHolder).bind()
        }
    }

    override fun getItemCount() = this.items.size

    override fun getItemViewType(position: Int): Int {
        val item = this.items[position]
        if (item is Intro)
            return TYPE_INTRO
        if (item is Loading)
            return TYPE_LOADING
        return TYPE_MOVIE
    }

    companion object {
        private const val TYPE_INTRO = 1
        private const val TYPE_MOVIE = 2
        private const val TYPE_LOADING = 4
    }

}