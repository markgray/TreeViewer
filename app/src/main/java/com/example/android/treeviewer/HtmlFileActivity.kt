package com.example.android.treeviewer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * This activity displays html files using
 */
@Suppress("MemberVisibilityCanBePrivate") // I like to use kdoc [] references
class HtmlFileActivity : AppCompatActivity() {
    /**
     * `TextView` used to display our html files
     */
    internal lateinit var htmlTestView: TextView

    /**
     * `TextView` used to display "Waiting for data to load…" message while waiting
     */
    internal lateinit var htmlWaiting: TextView

    /**
     * `LinearLayout` that we add our html files selection `Button`s to.
     */

    internal lateinit var htmlChapter: LinearLayout

    /**
     * `ScrollView` that holds the `LinearLayout htmlChapter`
     */
    internal lateinit var htmlChapterScrollView: ScrollView

    /**
     * List of the resource ids for the html files in our raw resources
     */
    val resourceIDS: IntArray = intArrayOf(
        R.raw.chapter1,
        R.raw.chapter2,
        R.raw.chapter3,
        R.raw.chapter4,
        R.raw.chapter5,
        R.raw.chapter6,
        R.raw.chapter7,
        R.raw.chapter8,
        R.raw.chapter9,
        R.raw.chapter10,
        R.raw.chapter11,
        R.raw.chapter12,
        R.raw.chapter13,
        R.raw.chapter14,
        R.raw.chapter15
    )

    /**
     * List of the titles for the html files in our raw resources (used to label the selection buttons)
     */
    val titles: Array<String> = arrayOf(
        "Chapter 1: What is Man?",
        "Chapter 2: The Death of Jean",
        "Chapter 3: The Turning-Point of My Life",
        "Chapter 4: How to Make History Dates Stick",
        "Chapter 5: The Memorable Assassination",
        "Chapter 6: A Scrap of Curious History",
        "Chapter 7: Switzerland, the Cradle of Liberty",
        "Chapter 8: At the Shrine of St. Wagner",
        "Chapter 9: William Dean Howells",
        "Chapter 10: English as she is Taught",
        "Chapter 11: A Simplified Alphabet",
        "Chapter 12: As Concerns Interpreting the Deity",
        "Chapter 13: Concerning Tobacco",
        "Chapter 14: The Bee",
        "Chapter 15: Taming the Bicycle"
    )

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file R.layout.activity_html_file. We initialize our
     * field `LinearLayout htmlChapter` by finding the view with id R.id.html_chapter (the chapter
     * selection buttons are placed here), initialize our field `ScrollView htmlChapterScrollView`
     * by finding the view with id R.id.html_chapter_scrollView (holds the `LinearLayout htmlChapter`
     * that holds our chapter selection buttons), initialize our field `TextView htmlTestView` by
     * finding the view with id R.id.html_textView (the selected chapter will be displayed here), and
     * initialize our field `TextView htmlWaiting` by finding the view with id R.id.html_waiting
     * (this will be displayed while our `HtmlDataTask` loads the chapter selected from our resources).
     * Finally we loop over `int i` for all of the resource ids in `int[] resourceIDS` calling
     * our method `addButton` to add a `Button` to `htmlChapter` whose title is given by
     * the `i`th string in `titles` and whose `OnClickListener` sets the visibility of `htmlChapterScrollView`
     * to GONE, and calls our method `loadResourceHtml` to have a `HtmlDataTask` instance load
     * the html file with resource id `i` in `resourceIDS` in the background into `htmlTestView`
     * (its `onPostExecute` override also changes the visibility of `htmlWaiting` to GONE).
     *
     * @param savedInstanceState we do not override `onSaveInstanceState` so do not use
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html_file)
        val rootView = findViewById<FrameLayout>(R.id.root_view_html)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
                topMargin = insets.top
                bottomMargin = insets.bottom
            }
            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        htmlChapter = findViewById(R.id.html_chapter)
        htmlChapterScrollView = findViewById(R.id.html_chapter_scrollView)
        htmlTestView = findViewById(R.id.html_textView)
        htmlWaiting = findViewById(R.id.html_waiting)
        for (i in resourceIDS.indices) {
            addButton(resourceIDS[i], titles[i], htmlChapter)
        }
    }

    /**
     * Adds a `Button` to its parameter `ViewGroup parent` whose label is given by its
     * parameter `String description` and whose `OnClickListener` sets the visibility of
     * the `ScrollView htmlChapterScrollView` that holds our chapter selection UI to GONE and
     * calls our method `loadResourceHtml` to have it load and display the Html resource file
     * with id `int resourceID` in the background.
     *
     * @param resourceID  resource ID that our button's `OnClickListener` should call the method
     * `loadResourceHtml` to load in the background.
     * @param description Label for our `Button`
     * @param parent      `ViewGroup` we should add our `Button` to.
     */
    fun addButton(resourceID: Int, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            htmlChapterScrollView.visibility = View.GONE
            htmlWaiting.visibility = View.VISIBLE
            loadResourceHtml(resourceID)
        }
        parent.addView(button)
    }

    /**
     * Loads the Html file with the resource ID of our parameter `int resourceID` in the background
     * then displays it in our field `TextView htmlTestView` when it is done loading. We initialize
     * our variable `HtmlDataTask mHtmlDataTask` with a new instance, whose `onPostExecute`
     * override sets the text of `htmlTestView` to the `Spanned` returned from the method
     * `doInBackground` of `mHtmlDataTask`, sets the visibility of `TextView htmlWaiting`
     * to GONE, and sets the visibility of `htmlTestView` to VISIBLE. Having done this we call the
     * `execute` method of `mHtmlDataTask` (which in turn calls the `doInBackground`
     * method in a separate thread) to have it load the file whose resource ID is our parameter
     * `resourceID`.
     *
     * @param resourceID resource ID of Html file located in our raw resources.
     */
    private fun loadResourceHtml(resourceID: Int) {
        @SuppressLint("StaticFieldLeak") // TODO: Fix static field leak
        val mHtmlDataTask = object : HtmlDataTask(applicationContext) {
            /**
             * Runs on the UI thread after [.doInBackground]. The parameter `Spanned s`
             * is the value returned by [.doInBackground]. We set the text of our field
             * `TextView htmlTestView` to our parameter `s`, set the visibility of our
             * field `TextView htmlWaiting` to GONE, then set the visibility of `htmlTestView`
             * to VISIBLE.
             *
             * @param result The result of the operation computed by [doInBackground].
             */
            override fun onPostExecute(result: Spanned?) {
                htmlTestView.text = (result ?: return)
                htmlWaiting.visibility = View.GONE
                htmlTestView.visibility = View.VISIBLE
            }
        }
        mHtmlDataTask.execute(resourceID)
    }

}
