package com.example.android.treeviewer

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Allows the user to select a pdf file to view, then launches `PdfRendererBasic` to display it.
 */
class PdfFileDisplayActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * and then we set our content view to our layout file R.layout.activity_pdf_file_display. We
     * initialize our `val linearLayout` to the [LinearLayout] with id R.id.pdf_choices, then loop
     * over `i` for all of the entries in our array [fileNames] calling our [addButton] method to
     * add a button to `linearLayout` whose label is the `i`'th entry in [titles]. The `i`'th entry
     * in [fileNames] will be passed to the [PdfRendererBasic] activity as an extra in the [Intent]
     * that will start that activity when the `i`'th button is clicked ([PdfRendererBasic] will read
     * and display that file).
     *
     * @param savedInstanceState we do not override `onSaveInstanceState` so do not use this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_file_display)
        val linearLayout: LinearLayout = findViewById(R.id.pdf_choices)
        for (i in fileNames.indices) {
            addButton(fileNames[i], titles[i], linearLayout)
        }
    }


    /**
     * Adds a [Button] to its parameter [parent]'s `ViewGroup` whose label is the string of
     * [description] and whose `OnClickListener` creates an [Intent] `intent` to launch the
     * activity [PdfRendererBasic], adds an extra to `intent` with our parameter [fileName] and
     * starts the activity.
     *
     * @param fileName  resource ID that our button's `OnClickListener` should call the method
     * `loadResourceTextFile` to load in the background.
     * @param description Label for our `Button`
     * @param parent      `ViewGroup` we should add our `Button` to.
     */
    private fun addButton(fileName: String, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            val intent = Intent(this@PdfFileDisplayActivity, PdfRendererBasic::class.java)
            intent.putExtra("com.example.android.treeviewer.FILE", fileName)
            startActivity(intent)
        }
        parent.addView(button)
    }

    /**
     * Our static constants.
     */
    companion object {

        /**
         * List of the resource ids for the text files we can display.
         */
        val fileNames: Array<String> = arrayOf(
            "sample.pdf",
            "everybody.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf"
        )

        /**
         * List of the titles for the text files we can display (used to label the selection buttons)
         */
        val titles: Array<String> = arrayOf(
            "sample.pdf from sample pdf renderer",
            "everybody.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf",
            "sample.pdf"
        )
    }
}
