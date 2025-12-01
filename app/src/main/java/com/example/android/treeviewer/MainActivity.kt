package com.example.android.treeviewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * This is starting activity, it holds buttons which allow you to launch our other activities.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to edge
     * display, then we call our super's implementation of `onCreate`, and set our content view to
     * our layout file `R.layout.activity_main`.
     *
     * We initialize our [ScrollView] variable `rootView` to the view with ID `R.id.main_root_view`
     * then call [ViewCompat.setOnApplyWindowInsetsListener] to take over the policy for applying
     * window insets to `rootView`, with the `listener` argument a lambda that accepts the [View]
     * passed the lambda in variable `v` and the [WindowInsetsCompat] passed the lambda in variable
     * `windowInsets`. It initializes its [Insets] variable `insets` to the
     * [WindowInsetsCompat.getInsets] of `windowInsets` with [WindowInsetsCompat.Type.systemBars]
     * as the argument, then it updates the layout parameters of `v` to be a
     * [ViewGroup.MarginLayoutParams] with the left margin set to `insets.left`, the right margin
     * set to `insets.right`, the top margin set to `insets.top`, and the bottom margin set to
     * `insets.bottom`. Finally it returns [WindowInsetsCompat.CONSUMED] to the caller (so that the
     * window insets will not keep passing down to descendant views).
     *
     * We initialize our `val linearLayout` by finding the [ViewGroup] with id `R.id.linear_layout`,
     * then add call our [addButton] method to add four buttons to `linearLayout`:
     *  - "Text File Display" Launches the activity [TextFileDisplayActivity]
     *  - "Html File Display" Launches the activity [HtmlFileActivity]
     *  - "Pdf File Display" Launches the activity [PdfFileDisplayActivity]
     *  - "Chrome FileProvider usage" Launches the activity [ChromeFileActivity]
     *
     * @param savedInstanceState We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<ScrollView>(R.id.main_root_view)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, windowInsets: WindowInsetsCompat ->
            val insets: Insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
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
        val linearLayout = findViewById<ViewGroup>(R.id.linear_layout)
        addButton(TextFileDisplayActivity::class.java, "Text File Display", linearLayout)
        addButton(HtmlFileActivity::class.java, "Html File Display", linearLayout)
        addButton(PdfFileDisplayActivity::class.java, "Pdf File Display", linearLayout)
        addButton(ChromeFileActivity::class.java, "Chrome FileProvider usage", linearLayout)
    }

    /**
     * Constructs and adds a `Button` to the `ViewGroup parent` designed to launch a
     * different Activity when it is clicked. First we initialize our variable `Button button`
     * with a new instance, then we set its text to the parameter `String description`, and we
     * set its `OnClickListener` to a lambda which will (when the `Button` is clicked) create an
     * `Intent` to launch the `Activity` whose `Class` is given in our parameter `Class destination`
     * and start that `Activity`. Finally we add `Button button` to the parameter `ViewGroup parent`
     * (the `LinearLayout` in our layout file in our case).
     *
     * @param destination Activity Class to be started by an Intent we create and start
     * when the Button is clicked
     * @param description text for the Button
     * @param parent the LinearLayout we are adding the Button to using **ViewGroup.addView**
     */
    private fun addButton(destination: Class<*>, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            val intent = Intent(this@MainActivity, destination)
            startActivity(intent)
        }
        parent.addView(button)
    }
}
