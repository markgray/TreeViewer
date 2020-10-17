package com.example.android.treeviewer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * This activity allows the user to choose a html file and have it displayed by Chrome.
 */
class ChromeFileActivity : AppCompatActivity() {

    /**
     * `TextView` used to display "Waiting for data to load…" message while waiting
     */
    internal lateinit var htmlWaiting: TextView
    /**
     * This is the [LinearLayout] we place our [Button]'s in.
     */
    private lateinit var htmlFileButtonHolder: LinearLayout
    /**
     * `ScrollView` that holds the `LinearLayout` [htmlFileButtonHolder]
     */
    internal lateinit var htmlButtonsScrollView: ScrollView

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file R.layout.activity_chrome_file. We initialize
     * our field [htmlWaiting] by finding the [TextView] with the ID R.id.chrome_waiting,
     * initialize our field [htmlButtonsScrollView] by finding the [ScrollView] with the ID
     * R.id.chrome_file_scrollView, and initialize our field [htmlFileButtonHolder] by finding the
     * [LinearLayout] with the ID R.id.chrome_file_buttons. Then we loop over `i` for all of the
     * resource ID's in our array [resourceIDS] calling our method [addButton] to construct, configure,
     * and add a [Button] to [htmlFileButtonHolder] which will use the `i`'th entry in our [titles]
     * array as the label, and when clicked will call our method [sendResourceFileToChrome] to start
     * an instance of [ChromeDataTask] to send the html resource file whose ID is the `i`'th entry in
     * our [resourceIDS] array to Chrome to display.
     *
     * @param savedInstanceState we do not override `onSaveInstanceState` so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrome_file)

        htmlWaiting = findViewById(R.id.chrome_waiting)
        htmlButtonsScrollView = findViewById(R.id.chrome_file_scrollView)
        htmlFileButtonHolder = findViewById(R.id.chrome_file_buttons)
        for (i in resourceIDS.indices) {
            addButton(resourceIDS[i], titles[i], htmlFileButtonHolder)
        }
    }

    /**
     * This method constructs, configures, and adds a [Button] to the [ViewGroup] parameter [parent].
     * If uses [description] as the label of the [Button], and adds a lambda `OnClickListener` to the
     * [Button] which calls our method [sendResourceFileToChrome] with [resourceID] as the parameter.
     * First we initalize our `val button` to a new instance of [Button]. We set the text of `button`
     * to our parameter [description] then set the `OnClickListener` of `button` to a lambda which
     * toasts the label of the button clicked as a debugging aid, sets the visibility of the
     * [ScrollView] field [htmlButtonsScrollView] to GONE, sets the visibility of the [TextView]
     * field [htmlWaiting] to VISIBLE, and then calls our [sendResourceFileToChrome] method with our
     * parameter [resourceID] to have it launch Chrome with the file with that resource ID. Having
     * configured `button` we add it to our [ViewGroup] parameter [parent].
     *
     * @param resourceID the resource ID of the html file that is to be sent to Chrome for display.
     * @param description the label for the [Button]
     * @param parent the [ViewGroup] we are to add the [Button] to.
     */
    private fun addButton(resourceID: Int, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            Toast.makeText(it.context, "$description was clicked", Toast.LENGTH_LONG).show()
            htmlButtonsScrollView.visibility = View.GONE
            htmlWaiting.visibility = View.VISIBLE
            sendResourceFileToChrome(resourceID)
        }
        parent.addView(button)
    }

    /**
     * This method constructs a new instance of [ChromeDataTask] whose `onPostExecute` override uses
     * the content URI returned from its `doInBackground` method as the data of an [Intent] that
     * launches the Chrome browser to view that URI. Having constructed the [ChromeDataTask] it then
     * calls its `execute` method to have it create a content URI for the file with resource ID
     * [resourceID].
     *
     * @param resourceID the resource ID of the file we want Chrome to display.
     */
    private fun sendResourceFileToChrome(resourceID: Int) {
        val mHtmlDataTask = object : ChromeDataTask(applicationContext) {
            override fun onPostExecute(result: Uri?) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setClassName(
                    "com.android.chrome",
                    "com.google.android.apps.chrome.Main"
                )
                intent.setDataAndType(result, "text/html")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                @SuppressLint("QueryPermissionsNeeded")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
                htmlButtonsScrollView.visibility = View.VISIBLE
                htmlWaiting.visibility = View.GONE
            }
        }
        mHtmlDataTask.execute(resourceID)
    }

    /**
     * Our static constants.
     */
    companion object {

        /**
         * The resource ID's of the html files we can send to Chrome.
         */
        val resourceIDS = intArrayOf(
            R.raw.graytree,
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
         * The titles to display in our button labels for the html files we can send to Chrome.
         */
        val titles = arrayOf(
            "Gray family tree",
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
    }
}
