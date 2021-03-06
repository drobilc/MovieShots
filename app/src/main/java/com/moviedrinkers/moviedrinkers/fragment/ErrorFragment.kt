package com.moviedrinkers.moviedrinkers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moviedrinkers.moviedrinkers.R
import com.moviedrinkers.moviedrinkers.activity.MainActivityEventListener
import com.moviedrinkers.moviedrinkers.data.ApiException
import kotlinx.android.synthetic.main.fragment_error.view.*

class ErrorFragment : Fragment() {

    private var exception: ApiException? = null

    private lateinit var callback: MainActivityEventListener
    fun setOnRetryListener(callback: MainActivityEventListener) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            exception = it.getParcelable("exception")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_error, container, false)
        view.exception_message.text = exception?.message

        view.try_again_button.setOnClickListener {
            this.callback.onRetryButtonClicked()
        }

        return view
    }

    companion object {

        const val TAG: String = "ERROR_FRAGMENT"

        @JvmStatic
        fun newInstance(exception: ApiException) =
            ErrorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("exception", exception)
                }
            }
    }
}
