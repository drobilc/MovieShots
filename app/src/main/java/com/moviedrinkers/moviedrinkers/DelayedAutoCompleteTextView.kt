package com.moviedrinkers.moviedrinkers

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView


class DelayedAutoCompleteTextView(context: Context, attributeSet: AttributeSet): AppCompatAutoCompleteTextView(context, attributeSet) {

    // Modified from http://makovkastar.github.io/blog/2014/04/12/android-autocompletetextview-with-suggestions-from-a-web-service/

    companion object {
        private val MESSAGE_TEXT_CHANGED = 666
        private val DEFAULT_AUTOCOMPLETE_DELAY = 200
    }

    // How much time to wait until sending a request to api
    private var autoCompleteDelay: Long = DEFAULT_AUTOCOMPLETE_DELAY.toLong()

    // Handler that will be used to execute filtering in the future
    private val messageHandler: Handler = CustomHandler()

    override fun performFiltering(text: CharSequence?, keyCode: Int) {
        messageHandler.removeMessages(MESSAGE_TEXT_CHANGED)

        // After autoCompleteDelay perform filtering
        // When constructing a message, a what parameter must be passed that identifies this message
        // type. In our case, the what argument is MESSAGE_TEXT_CHANGED
        // The second parameter is the text that we want to filter by (in the future)
        val message = messageHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text)
        messageHandler.sendMessageDelayed(message, autoCompleteDelay)
    }

    fun setAutoCompleteDelay(newDelay: Long) {
        this.autoCompleteDelay = newDelay
    }

    inner class CustomHandler : Handler() {
        override fun handleMessage(msg: Message) {
            // Get the text to filter by and use it to perform filtering
            super@DelayedAutoCompleteTextView.performFiltering(msg.obj as CharSequence, msg.arg1)
        }
    }

}