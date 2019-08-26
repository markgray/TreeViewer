package com.example.android.treeviewer

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayout = findViewById(R.id.linear_layout) as ViewGroup
        addButton(TextFileDisplayActivity::class.java, "Text File Display", linearLayout)
    }

    /**
     * Constructs and adds a `Button` to the `ViewGroup parent` designed to launch a
     * different Activity when it is clicked. First we initialize our variable `Button button`
     * with a new instance, then we set its text to the parameter `String description`, and we
     * set its `OnClickListener` to an anonymous class which will (when the `Button` is
     * clicked) create an `Intent` to launch the `Activity` whose `Class` is given
     * in our parameter `Class destination` and start that `Activity`. Finally we add
     * `Button button` to the parameter `ViewGroup parent` (the `LinearLayout` in
     * our layout file in our case).
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
