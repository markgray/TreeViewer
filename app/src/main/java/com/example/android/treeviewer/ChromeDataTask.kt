package com.example.android.treeviewer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.util.TypedValue
import androidx.core.content.FileProvider.getUriForFile
import java.io.File

@SuppressLint("StaticFieldLeak")
open class ChromeDataTask
/**
 * Our constructor, we just save our parameter `Context context` in our field `mContext`.
 */
internal constructor(
    /**
     * `Context` to use to access resources from our application (in our case this is the
     * "context of the single, global Application object of the current process" obtained from the
     * `getApplicationContext` method of the `TextFileDisplayActivity` activity and then passed to
     * our constructor).
     */
    private var mContext: Context
) : AsyncTask<Int, String, Uri>() {
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to [.execute]
     * by the caller of this task.
     *
     * @param resourceId The parameters of the task.
     * @return The [Uri] that our `FileProvider` creates to use to share the resource file.
     *
     * @see .onPreExecute
     * @see .onPostExecute
     *
     * @see .publishProgress
     */
    override fun doInBackground(vararg resourceId: Int?): Uri {
        @Suppress("UNUSED_VARIABLE")
        val inputStream = mContext
            .resources
            .openRawResource(resourceId[0]!!)
        val tv = TypedValue()
        mContext.resources.getValue(resourceId[0]!!, tv, true)

        val stringOfResourse : String = tv.string.toString()
        val startOfFileName : Int = stringOfResourse.lastIndexOf('/') + 1
        val fileName = stringOfResourse.substring(startOfFileName)
        Log.i(TAG, "File name of ${resourceId[0]} is $fileName")

        val fout = mContext.openFileOutput(fileName, Context.MODE_PRIVATE)
        val buffer = ByteArray(16384)
        var n = inputStream.read(buffer)
        while (n >= 0) {
            fout.write(buffer, 0, n)
            n = inputStream.read(buffer)
        }
        inputStream.close()
        fout.close()
        val contentUri : Uri = getUriForFile(
            mContext,
            "com.example.android.treeviewer.fileprovider",
            File(mContext.getFilesDir(), fileName)
        )
        return contentUri
    }

    companion object {
        const val TAG :String = "ChromeDataTask"
    }

}