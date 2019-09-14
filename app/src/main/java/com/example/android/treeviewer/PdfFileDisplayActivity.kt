package com.example.android.treeviewer

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class PdfFileDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_file_display)
        val linearLayout : LinearLayout = findViewById(R.id.pdf_choices)
        for(i in fileNames.indices) {
            addButton(fileNames[i], titles[i], linearLayout)
        }
    }


    /**
     * Adds a [Button] to its parameter `ViewGroup` [parent] whose label is given by its parameter
     * `String` [description] and whose `OnClickListener` creates an [Intent] `intent` to launch the
     * activity [PdfRendererBasic], adds an extra to `intent` with our parameter [fileName] and
     * start the activity.
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
        val fileNames = arrayOf(
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
        val titles = arrayOf(
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
