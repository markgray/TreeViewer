package com.example.android.treeviewer

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * This is starting activity, it holds buttons which allow you to launch our other activities.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file R.layout.activity_main. We initialize our
     * `val linearLayout` by finding the [ViewGroup] with id R.id.linear_layout, then add call our
     * [addButton] method to add two buttons to `linearLayout`:
     *  - "Text File Display" Launches the activity [TextFileDisplayActivity]
     *  - "Html File Display" Launches the activity [HtmlFileActivity]
     *
     * @param savedInstanceState We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<ScrollView>(R.id.main_root_view)
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
