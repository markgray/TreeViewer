package com.example.android.treeviewer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * This activity allows the user to choose a html file and have it displayed by Chrome.
 */
class ChromeFileActivity : AppCompatActivity() {

    private lateinit var htmlFileButtonHolder: LinearLayout
    /**
     * `TextView` used to display "Waiting for data to load…" message while waiting
     */
    internal lateinit var htmlWaiting: TextView
    /**
     * `ScrollView` that holds the `LinearLayout htmlChapter`
     */
    internal lateinit var htmlChapterScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrome_file)

        htmlWaiting = findViewById(R.id.chrome_waiting)
        htmlChapterScrollView = findViewById(R.id.chrome_file_scrollView)
        htmlFileButtonHolder = findViewById(R.id.chrome_file_buttons)
        for (i in resourceIDS.indices) {
            addButton(resourceIDS[i], titles[i], htmlFileButtonHolder)
        }
    }

    private fun addButton(resourceID: Int, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            Toast.makeText(it.context, "$description was clicked", Toast.LENGTH_LONG).show()
            htmlChapterScrollView.visibility = View.GONE
            htmlWaiting.visibility = View.VISIBLE
            sendResourceFileToChrome(resourceID)
        }
        parent.addView(button)
    }

    @SuppressLint("StaticFieldLeak")
    fun sendResourceFileToChrome(resourceID: Int) {
        val mHtmlDataTask = object : ChromeDataTask(applicationContext) {
            override fun onPostExecute(uri: Uri) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setClassName(
                    "com.android.chrome",
                    "com.google.android.apps.chrome.Main"
                )
                intent.setDataAndType(uri, "text/html")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
                htmlChapterScrollView.visibility = View.VISIBLE
                htmlWaiting.visibility = View.GONE
            }
        }
        mHtmlDataTask.execute(resourceID)
    }

    companion object {

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
