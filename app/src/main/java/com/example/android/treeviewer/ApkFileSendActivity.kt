package com.example.android.treeviewer

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ApkFileSendActivity : AppCompatActivity() {

    private lateinit var htmlFileButtonHolder: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apk_file_send)

        htmlFileButtonHolder = findViewById(R.id.html_file_buttons)
        for (i in resourceIDS.indices) {
            addButton(resourceIDS[i], titles[i], htmlFileButtonHolder)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun addButton(resourceID: Int, description: String, parent: ViewGroup) {
        val button = Button(this)
        button.text = description
        button.setOnClickListener {
            Toast.makeText(it.context, "$description was clicked", Toast.LENGTH_LONG).show()
            val intent = makeIntent(resourceID)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        parent.addView(button)
    }

    private fun makeIntent(resourceID: Int): Intent {

        val b: Uri.Builder = Uri.Builder()
        b.scheme("content")
        b.authority("com.example.android.treeviewer.apkfileprovider")
        val tv = TypedValue()
        resources.getValue(resourceID, tv, true)
        b.appendEncodedPath(tv.assetCookie.toString())
        b.appendEncodedPath(tv.string.toString())
        val uri: Uri = b.build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setClassName(
            "com.android.chrome",
            "com.google.android.apps.chrome.Main"
        )
        intent.setDataAndType(uri, "text/html")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.clipData = ClipData.newUri(contentResolver, "html", uri)

        return intent
    }


    companion object {
        val resourceIDS = intArrayOf(
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
