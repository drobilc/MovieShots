package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moviedrinkers.moviedrinkers.adapter.GameDisplayAdapter
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.data.DrinkingGame
import kotlinx.android.synthetic.main.fragment_game_display.*


class GameDisplayFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val handler: Handler = Handler()

    private var loadingMessages: Array<String>? = null
    private var loadingMessageIndex: Int = 0
    private var isLoading: Boolean = true

    private var drinkingGame: DrinkingGame? = null

    private val run: Runnable = object : Runnable {
        override fun run() {
            loading_text?.text = loadingMessages!![loadingMessageIndex]
            loadingMessageIndex = (loadingMessageIndex + 1) % loadingMessages!!.size

            // Repeat until isLoading is set to true
            if (isLoading)
                handler.postDelayed(this, 2000)
        }
    }

    fun displayLoadingScreen(isLoading: Boolean) {
        this.isLoading = isLoading

        // Set the progress_bar visibility if it has been loaded
        if (progress_bar != null) {
            val visibility = if (isLoading) View.VISIBLE else View.GONE
            progress_bar.visibility = visibility
        }
    }

    fun displayGame(game: DrinkingGame) {
        viewManager = LinearLayoutManager(context)
        viewAdapter =
            GameDisplayAdapter(game)

        recyclerView = generated_game.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val slideUp = AnimationUtils.loadAnimation(context!!.applicationContext,
            R.anim.sliide_up
        )
        recyclerView.startAnimation(slideUp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_display, container, false)

        // Get the loading messages from strings xml file
        loadingMessages = activity!!.resources.getStringArray(R.array.loading_messages)

        // If no game data has been loaded yet, display loading spinner
        if (drinkingGame == null) {
            this.displayLoadingScreen(true)
            handler.removeCallbacksAndMessages(null)
            this.handler.post(run)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GameDisplayFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }
}
