package com.example.android.treeviewer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * This `Activity` loads text files from the raw resources of the app in the background, and
 * displays them in a `RecyclerView`.
 */
class TextFileDisplayActivity : Activity() {
    /**
     * `RecyclerView` used to display our text files
     */
    internal lateinit var textFileRecylerView: RecyclerView
    /**
     * `StringListAdapter` we use for our `RecyclerView textFileRecylerView`
     */
    internal lateinit var textFileAdapter: StringListAdapter
    /**
     * `LayoutManager` for our `RecyclerView` (a `LinearLayout` instance)
     */
    internal lateinit var mLayoutManager: RecyclerView.LayoutManager
    /**
     * `TextView` used to display "Waiting for data to load…" message while waiting
     */
    internal lateinit var textFileWaiting: TextView
    /**
     * `LinearLayout` that we add our text file selection `Button`s to.
     */
    private lateinit var textFileBooks: LinearLayout
    /**
     * `ScrollView` that holds the `LinearLayout textFileBooks`
     */
    private lateinit var textFileBooksScrollView: ScrollView

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file R.layout.activity_text_file. We initialize our
     * field `RecyclerView.LayoutManager mLayoutManager` with a new `LinearLayoutManager`
     * instance, initialize our field `LinearLayout textFileBooks` by finding the view with
     * id R.id.text_file_books, initialize our field `ScrollView textFileBooksScrollView` by
     * finding the view with id R.id.text_file_books_scrollView, initialize our field
     * `RecyclerView textFileRecylerView` by finding the view with id R.id.text_file_recycle_view,
     * and initialize our field `TextView textFileWaiting` by finding the view with id
     * R.id.text_file_waiting. Then we loop over `int i` for all the resource id's in the array
     * `int[] resourceIDS` calling our method `addButton` to add a button to our field
     * `textFileBooks` with the label `titles[ i ]` which will load and display the raw
     * text file whose resource id is `resourceIDS[ i ]` when the button is clicked.
     *
     * @param savedInstanceState we do not override `onSaveInstanceState` so do not use
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_file)

        mLayoutManager = LinearLayoutManager(applicationContext)
        textFileBooks = findViewById(R.id.text_file_books)
        textFileBooksScrollView = findViewById(R.id.text_file_books_scrollView)
        textFileRecylerView = findViewById(R.id.text_file_recycle_view)
        textFileWaiting = findViewById(R.id.text_file_waiting)
        for (i in resourceIDS.indices) {
            addButton(resourceIDS[i], titles[i], textFileBooks)
        }
    }

    /**
     * Adds a [Button] to its parameter `ViewGroup` [parent] whose label is given by its
     * parameter `String` [description] and whose `OnClickListener` sets the visibility of
     * the `LinearLayout` [textFileBooks] which holds our text file selection buttons to GONE,
     * and the `ScrollView` [textFileBooksScrollView] that holds that selection `LinearLayout`
     * to GONE and then sets the visibility of [textFileWaiting] (our waiting text view) to
     * VISIBLE, and finally calls our method [loadResourceTextFile] to have it load and display
     * the resource file with id `int` [resourceID] in the background.
     *
     * @param resourceID  resource ID that our button's `OnClickListener` should call the method
     * `loadResourceTextFile` to load in the background.
     * @param description Label for our `Button`
     * @param parent      `ViewGroup` we should add our `Button` to.
     */
    private fun addButton(resourceID: Int, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            textFileBooks.visibility = View.GONE
            textFileBooksScrollView.visibility = View.GONE
            textFileWaiting.visibility = View.VISIBLE
            loadResourceTextFile(resourceID)
        }
        parent.addView(button)
    }

    /**
     * Causes the utf8 text file with resource ID `int` [resourceID] to be read in by a background
     * task, and then displays the `List<String> results` the task returns in our field
     * `RecyclerView textFileRecylerView`.
     *
     * @param resourceID resource ID of the raw file we are to read in the background and then display
     * in `RecyclerView textFileRecylerView` once the background task is done.
     */
    private fun loadResourceTextFile(resourceID: Int) {
        val mTextFileDataTask = object : TextFileDataTask(applicationContext) {
            /**
             * Runs on the UI thread after [doInBackground]. The parameter `List<String>` [result]
             * is the value returned by [doInBackground].
             *
             * We initialize our field `StringListAdapter` [textFileAdapter] with a new instance
             * which will use our parameter `List<String>` [result] as its data set, and our field
             * `RecyclerView.LayoutManager` [mLayoutManager] as its `LayoutManager`, set the
             * adapter of `RecyclerView` [textFileRecylerView] to [textFileAdapter] and set
             * the `LayoutManager` that [textFileRecylerView] will use to be our field
             * [mLayoutManager]. Finally we set the visibility of our field `TextView` [textFileWaiting]
             * to GONE, and set the visibility of [textFileRecylerView] to VISIBLE.
             *
             * @param result The result of the operation computed by [.doInBackground].
             */
            override fun onPostExecute(result: List<String>?) {
                textFileAdapter = StringListAdapter(result!!, mLayoutManager)
                textFileRecylerView.adapter = textFileAdapter
                textFileRecylerView.layoutManager = mLayoutManager
                textFileWaiting.visibility = View.GONE
                textFileRecylerView.visibility = View.VISIBLE
            }
        }
        mTextFileDataTask.execute(resourceID)
    }

    /**
     * Our static constants.
     */
    companion object {

        /**
         * List of the resource ids for the text files we can display.
         */
        val resourceIDS = intArrayOf(
            R.raw.all_relatives,
            R.raw.emerson_conduct_of_life,
            R.raw.emerson_essays_second_series,
            R.raw.emerson_poems,
            R.raw.emerson_representative_men,
            R.raw.thoreau_excursions,
            R.raw.bulfinch,
            R.raw.nietzshe
        )

        /**
         * List of the titles for the text files we can display (used to label the selection buttons)
         */
        val titles = arrayOf(
            "List of all relatives",
            "Emerson The Conduct of Life",
            "Emerson Essays, Second Series",
            "Emerson Poems",
            "Emerson Representative Men",
            "Thoreau Excursions",
            "Bulfinch’s Mythology",
            "Nietzshe's Philosophy"
        )
    }

}
