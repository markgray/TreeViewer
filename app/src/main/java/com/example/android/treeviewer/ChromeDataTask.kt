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
     * `getApplicationContext` method of the `ChromeFileActivity` activity and then passed to
     * our constructor).
     */
    private var mContext: Context
) : AsyncTask<Int, String, Uri>() {
    /**
     * We override this method to perform a computation on a background thread. The [resourceId]
     * parameter is the parameter passed to [AsyncTask.execute] by the caller of this task. First
     * we initialize our `val inputStream` with an `InputStream` opened to read the raw resource
     * with the resource ID of the zeroth entry in our parameter [resourceId]. Then we initialize
     * our `val tv` with a new instance of [TypedValue] and then load it with the the raw data
     * associated with that resource ID. We initialize `val stringOfResourse` to the string held
     * in `tv` that is associated with the resource file of our parameter (the "path" to the file),
     * initialize `val startOfFileName` to the character after the last '/' character in that path,
     * and initialize `val fileName` to the substring of `stringOfResourse` from `startOfFileName`
     * to its end (this is the file name of the resource file). We then initialize `val fout` with
     * a `FileOutputStream` opened for writing to a file associated with our context's application
     * package named `fileName`. We initialize `val buffer` with a [ByteArray] instance sized to
     * hold 16384 bytes, and initialize `var n` to the number of bytes that the `read` method of
     * `inputStream` reads into `buffer`. We then loop while `n` is greater than or equal to 0
     * writing all `n` bytes read into `buffer` to `fout`, then again setting `n` to the number of
     * bytes that the `read` method of `inputStream` reads into `buffer` before looping around again.
     * When done copying `inputStream` to `fout` we close both of these streams and return the
     * [Uri] that the `getUriForFile` method creates for the authority our application uses for
     * its content Uri's: "com.example.android.treeviewer.fileprovider", and the file we just created
     * which is located in the directory on the filesystem where files created with `openFileOutput`
     * are stored and has the name `fileName`.
     *
     * @param resourceId The resource ID of the html file we are to send to Chrome for display.
     * @return The [Uri] that our `FileProvider` creates to use to share the resource file.
     */
    override fun doInBackground(vararg resourceId: Int?): Uri {
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
        return getUriForFile(
            mContext,
            "com.example.android.treeviewer.fileprovider",
            File(mContext.filesDir, fileName)
        )
    }

    /**
     * Our static constants.
     */
    companion object {
        /**
         * TAG used for logging.
         */
        const val TAG :String = "ChromeDataTask"
    }

}