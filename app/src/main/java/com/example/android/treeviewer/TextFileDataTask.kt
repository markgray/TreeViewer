package com.example.android.treeviewer

import android.content.Context
import com.example.android.treeviewer.util.CoroutinesAsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * An `AsyncTask` which loads the utf8 text file with the resource ID specified by the parameter
 * passed to the method `doInBackground` in the background and returns a list of strings that
 * correspond to each of the paragraphs of the text file to the caller.
 */
open class TextFileDataTask
/*
 * Our constructor, we just save our parameter `Context context` in our field `mContext`.
 *
 * @param context `Context` to use to access resources of this application
 */
internal constructor(
    /**
     * `Context` to use to access resources from our application (in our case this is the
     * "context of the single, global Application object of the current process" obtained from the
     * `getApplicationContext` method of the `TextFileDisplayActivity` activity and then passed
     * to our constructor).
     */
    private var mContext: Context
) : CoroutinesAsyncTask<Int, String, List<String>>() {

    /**
     * We override this method to perform a computation on a background thread. The specified
     * parameters are the parameters passed to `execute` by the caller of this task. We initialize
     * our variable `StringBuilder builder` with a new instance, declare `String line`, and
     * initialize our variable `List<String> results` with a new instance of `ArrayList`.
     * We use our field `Context mContext` to fetch a `Resources` instance for the application's
     * package which we then use to open a data stream for reading the raw resource with resource id
     * `resourceId[0]` for our variable `InputStream inputStream`. Then we initialize our
     * variable `BufferedReader reader` with a new instance constructed to use a new instance of
     * `InputStreamReader` created from `inputStream` using the default charset. Then wrapped
     * in a try block intended to catch and log [IOException] initialize `line` to the first line of
     * text in `reader`, and then we loop while `line` is not *null* branching on whether `line` is
     * empty:
     *  - If `line` is empty and `builder` is not empty we append a newline character to `builder`,
     *  add the the string value of `builder` to our list `results`, and set the length of `builder`
     *  to 0, and if `builder` was empty we just add a string with only a newline character to our
     *  list `results`.
     *  - If `line` is not empty we append `line` to `builder` followed by a space character and if
     *  the first character of `line` is a space character (an indented line in the file) we append
     *  a newline character to `builder` as well (so that text wrapping will not be applied to this
     *  line).
     *
     * When done with the loop (`readLine` returned *null* indicating EOF) we close `reader`
     * and return `results` so that the [onPostExecute] override will be called with it
     * as its parameter.
     *
     * @param params Resource ID of the utf8 text file we are to read in
     * @return a `List<String>` of the file read with each paragraph in a seperate string.
     */
    override fun doInBackground(vararg params: Int?): List<String> {
        val builder = StringBuilder()
        var line: String?
        val results = ArrayList<String>()
        val resourceID = params[0]!!
        val inputStream = mContext
            .resources
            .openRawResource(resourceID)

        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            line = reader.readLine()
            while (line != null) {
                if (line.isEmpty()) {
                    if (builder.isNotEmpty()) {
                        builder.append("\n")
                        results.add(builder.toString())
                        builder.setLength(0)
                    } else {
                        results.add("\n")
                    }
                } else {
                    builder.append(line).append(" ")
                    if (line.startsWith(" ")) {
                        builder.append("\n")
                    }
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return results
    }

    /**
     * Our static constant.
     */
    companion object {
        /**
         * TAG used for logging
         */
        @Suppress("unused")
        internal const val TAG = "TextFileDataTask"
    }
}
