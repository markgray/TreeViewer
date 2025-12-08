package com.example.android.treeviewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * Allows the user to select a pdf file to view, then launches `PdfRendererBasic` to display it.
 */
class PdfFileDisplayActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to edge
     * display, then we call our super's implementation of `onCreate`, and set our content view to
     * our layout file `R.layout.activity_pdf_file_display`.
     *
     * We initialize our [FrameLayout] variable `rootView` to the view with ID
     * `R.id.root_view_pdf` then call [ViewCompat.setOnApplyWindowInsetsListener]
     * to take over the policy for applying window insets to `rootView`, with the
     * `listener` argument a lambda that accepts the [View] passed the lambda
     * in variable `v` and the [WindowInsetsCompat] passed the lambda
     * in variable `windowInsets`. It initializes its [Insets] variable
     * `systemBars` to the [WindowInsetsCompat.getInsets] of `windowInsets` with
     * [WindowInsetsCompat.Type.systemBars] as the argument. It then gets the insets for the
     * IME (keyboard) using [WindowInsetsCompat.Type.ime]. It then updates
     * the layout parameters of `v` to be a [ViewGroup.MarginLayoutParams]
     * with the left margin set to `systemBars.left`, the right margin set to
     * `systemBars.right`, the top margin set to `systemBars.top`, and the bottom margin
     * set to the maximum of the system bars bottom inset and the IME bottom inset.
     * Finally it returns [WindowInsetsCompat.CONSUMED]
     * to the caller (so that the window insets will not keep passing down to
     * descendant views).
     *
     * We initialize our variable `val linearLayout` to the [LinearLayout] with id
     * `R.id.pdf_choices`, then loop over `i` for all of the entries in our array [fileNames]
     * calling our [addButton] method to add a button to `linearLayout` whose label is the
     * `i`'th entry in [titles]. The `i`'th entry in [fileNames] will be passed to the
     * [PdfRendererBasic] activity as an extra in the [Intent] that will start that activity
     * when the `i`'th button is clicked ([PdfRendererBasic] will read and display that file).
     *
     * @param savedInstanceState we do not override `onSaveInstanceState` so do not use this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_file_display)
        val rootView = findViewById<FrameLayout>(R.id.root_view_pdf)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, windowInsets: WindowInsetsCompat ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = systemBars.left
                rightMargin = systemBars.right
                topMargin = systemBars.top
                bottomMargin = systemBars.bottom.coerceAtLeast(ime.bottom)
            }
            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
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
