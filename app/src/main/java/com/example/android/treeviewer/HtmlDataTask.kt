package com.example.android.treeviewer

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.util.Log
import com.example.android.treeviewer.util.CoroutinesAsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * An `AsyncTask` which loads the Html file with the resource ID specified by the parameter
 * passed to the method `doInBackground` in the background and returns a `Spanned` string
 * object to the caller.
 */
open class HtmlDataTask
/**
 * Our constructor, we just save our parameter `Context context` in our field `mContext`.
 */
internal constructor(
    /**
     * `Context` to use to access resources from our application (in our case this is the
     * "context of the single, global Application object of the current process" obtained from the
     * `getApplicationContext` method of the `HtmlFileActivity` activity and then passed to
     * our constructor).
     */
    private var mContext: Context
) : CoroutinesAsyncTask<Int, String, Spanned>() {

    /**
     * Loads a Html file from our resources on a background thread and returns a `Spanned` string
     * created from the contents of the file we load. The parameter is the resource ID of the file
     * passed to `execute` by the caller of this task. First we initialize our variable
     * `StringBuilder builder` to null, declare our variable `String line`, and initialize
     * our variable `int sizeOfInputStream` to 0. We initialize `InputStream inputStream`
     * by fetching a `Resources` instance for the application's package as returned by the
     * `getResources` method of our field `mContext`, and using that `Resources`
     * instance open a data stream for reading the raw resource with resource ID `resourceId[0]`.
     * Next we initialize `BufferedReader reader` with a buffering character-input stream that
     * uses a default-sized input buffer to read from an `InputStreamReader` constructed to read
     * bytes from `inputStream` and decode them into characters using  the platform's default charset.
     *
     * Having set everything up, wrapped in a try block intended to catch and log [IOException], we
     * set our variable `sizeOfInputStream` to an estimate of the number of bytes that can be read
     * from `inputStream` and allocate an initial capacity of 80 more than that value for
     * `builder`. We then loop setting `line` to the `String` returned by the
     * `readLine` method of `reader` until it returns null appending each `line`
     * to `builder`. When done reading the entire file into `builder` we close `reader`.
     *
     * Upon exiting from the try block we return the `Spanned` string created by the `fromHtml`
     * method of `Html` from the string value of `builder`.
     *
     * @param params the resource ID of the Html file we are to load.
     * @return A `Spanned` string created from the contents of the file we load.
     */
    override fun doInBackground(vararg params: Int?): Spanned {
        val resourceId = params[0]!!
        var builder: StringBuilder? = null
        var line: String?
        var sizeOfInputStream = 0
        val inputStream = mContext
            .resources
            .openRawResource(resourceId)

        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            sizeOfInputStream = inputStream.available() // Get the size of the stream
            builder = StringBuilder(sizeOfInputStream + 80)
            line = reader.readLine()
            while (line != null) {
                builder.append(line)
                line = reader.readLine()
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (BuildConfig.DEBUG && builder == null) {
            error("Assertion failed")
        }
        Log.i(
            TAG,
            "sizeOfInputStream: " + sizeOfInputStream + " Size of builder: " + builder!!.capacity()
        )
        @Suppress("DEPRECATION")
        return Html.fromHtml(builder.toString())
    }

    companion object {
        /**
         * TAG used for logging
         */
        internal const val TAG = "HtmlDataTask"
    }
}
