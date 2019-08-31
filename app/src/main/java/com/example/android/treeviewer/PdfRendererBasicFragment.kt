/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.treeviewer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*
 * This fragment has a big `ImageView` that shows PDF pages, and 2
 * [android.widget.Button]s to move between pages. We use a
 * [android.graphics.pdf.PdfRenderer] to render PDF pages as
 * [android.graphics.Bitmap]s.
 */
/**
 * Our required empty constructor.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class PdfRendererBasicFragment : Fragment(), View.OnClickListener {

    /**
     * File descriptor of the PDF, points to FILENAME in the application cache.
     */
    private var mFileDescriptor: ParcelFileDescriptor? = null

    /**
     * [android.graphics.pdf.PdfRenderer] used to render the PDF.
     */
    private var mPdfRenderer: PdfRenderer? = null

    /**
     * `Page` that is currently shown on the screen.
     */
    private var mCurrentPage: PdfRenderer.Page? = null

    /**
     * [android.widget.ImageView] that shows a PDF page as a [android.graphics.Bitmap],
     * its resource id in our layout file is R.id.image
     */
    private var mImageView: ImageView? = null

    /**
     * [android.widget.Button] to move to the previous page, resource id R.id.previous
     */
    private var mButtonPrevious: Button? = null

    /**
     * [android.widget.Button] to move to the next page, resource id R.id.next
     */
    private var mButtonNext: Button? = null

    /**
     * PDF page index, used only to restore a saved page number, or to start on page 0.
     */
    private var mPageIndex: Int = 0

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing, and unused.
     *
     * @return The number of pages.
     */
    @Suppress("unused")
    val pageCount: Int
        get() = mPdfRenderer!!.pageCount

    /**
     * Called to have the fragment instantiate its user interface view. We return the `View`
     * that our parameter `LayoutInflater inflater` inflates from our layout file
     * R.layout.fragment_pdf_renderer_basic, using our parameter `ViewGroup container` for the
     * LayoutParams of the view without attaching to it.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false)
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view. First we call our
     * super's implementation of `onViewCreated` then we initialize our field `ImageView mImageView`
     * by finding the view with id R.id.image, our field `Button mButtonPrevious` by finding the
     * view with id R.id.previous, and our field `Button mButtonNext` by finding the view with
     * id R.id.next. We then set the `OnClickListener` of both `mButtonPrevious` and
     * `mButtonNext` to this. We initialize our field `int mPageIndex` to 0, then if our
     * parameter `Bundle savedInstanceState` is not null we set `mPageIndex` to the int
     * stored under the key STATE_CURRENT_PAGE_INDEX ("current_page_index") in `savedInstanceState`
     * defaulting to 0.
     *
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retain view references.
        mImageView = view.findViewById(R.id.image)
        mButtonPrevious = view.findViewById(R.id.previous)
        mButtonNext = view.findViewById(R.id.next)
        // Bind events.
        mButtonPrevious!!.setOnClickListener(this)
        mButtonNext!!.setOnClickListener(this)

        mPageIndex = 0
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0)
        }
    }

    /**
     * Called when the Fragment is visible to the user. First we call our super's implementation of
     * `onStart`. Then wrapped in a try block intended to catch and toast IOException to the
     * user we call our method `openRenderer` to set up our `PdfRenderer` to render our
     * pdf file. We then call our method `showPage` to show the page of PDF at page index
     * `mPageIndex` on the screen.
     */
    override fun onStart() {
        super.onStart()
        try {

            openRenderer(activity as FragmentActivity)
            showPage(mPageIndex)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Error! " + e.message, Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Called when the Fragment is no longer started. Wrapped in a try block intended to catch and
     * log IOException we call our method `closeRenderer` to close the current `Page`
     * `mCurrentPage`, our `PdfRenderer`, and the file descriptor `mFileDescriptor`.
     * We then call our super's implementation of `onStop`.
     */
    override fun onStop() {
        try {
            closeRenderer()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        super.onStop()
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to [.onCreate],
     * [.onCreateView], and
     * [.onActivityCreated].
     *
     *
     * First we call our super's implementation of `onSaveInstanceState`, then if our field
     * `Page mCurrentPage` is not null we add the page index of `mCurrentPage` to our
     * parameter `Bundle outState` under the key STATE_CURRENT_PAGE_INDEX ("current_page_index").
     *
     * @param outState Bundle in which to place your saved state.
     */
    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage!!.index)
        }
    }

    /**
     * Sets up a [android.graphics.pdf.PdfRenderer] and related resources. First we initialize
     * `File file` using the absolute path to the application specific cache directory on the
     * filesystem for the parent path, and FILENAME ("sample.pdf") as the child pathname. If this file
     * does not exist (we have not been run before) we initialize `InputStream asset` by using
     * an AssetManager instance for the application's package to open the asset file named FILENAME,
     * initialize `FileOutputStream output` with a new instance to write to the file represented
     * by `File file`. We allocate 1024 bytes for `byte[] buffer`, and declare `int size`.
     * We then loop, setting `size` to the number of bytes that the `read` method of `asset`
     * reads into `buffer` and writing `size` bytes from `buffer` to `output`
     * until `size` is equal to -1 (end of file). Then we close both `asset` and `output`.
     *
     *
     * Now that we know that `File file` exists we initialize our field `ParcelFileDescriptor mFileDescriptor`
     * with a new instance to access `File file` in MODE_READ_ONLY mode. If `mFileDescriptor`
     * is not null we initialize our field `PdfRenderer mPdfRenderer` with a new instance constructed
     * to read from `mFileDescriptor`.
     *
     * @param context `Context` to use to access the application specific cache directory, and
     * the AssetManager instance for the application's package (in order to read our
     * example PDF from our assets). It is supplied by the `getActivity` method
     * in our `onStart` override.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    private fun openRenderer(context: Context) {
        // In this sample, we read a PDF from the assets directory.
        val file = File(context.cacheDir, FILENAME)
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            val asset = context.assets.open(FILENAME)
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int = asset.read(buffer)
            while (size != -1) {
                output.write(buffer, 0, size)
                size = asset.read(buffer)
            }
            asset.close()
            output.close()
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        // This is the PdfRenderer we use to render the PDF.
        if (mFileDescriptor != null) {
            mPdfRenderer = PdfRenderer(mFileDescriptor!!)
        }
    }

    /**
     * Closes the [android.graphics.pdf.PdfRenderer] and related resources. If our field
     * `Page mCurrentPage` is not null we call its `close` method to close the `Page`.
     * We then call the `close` method of our field `PdfRenderer mPdfRenderer` to close
     * the renderer, and the `close` method of our field `ParcelFileDescriptor mFileDescriptor`
     * to close the ParcelFileDescriptor as well as the underlying OS resources allocated to represent
     * the stream.
     *
     * @throws java.io.IOException When the PDF file cannot be closed.
     */
    @Throws(IOException::class)
    private fun closeRenderer() {
        if (null != mCurrentPage) {
            mCurrentPage!!.close()
        }
        mPdfRenderer!!.close()
        mFileDescriptor!!.close()
    }

    /**
     * Shows the specified page of PDF to the screen. If the number of pages in the document is less
     * than or equal to the request page index `index` we return having done nothing. If the
     * field `Page mCurrentPage` is not null we call its `close` method to close the page.
     * We then set `mCurrentPage` to the `Page` that the `openPage` method of our
     * field `PdfRenderer mPdfRenderer` opens for rendering for the page index `index`.
     * We initialize `Bitmap bitmap` with a ARGB_8888 bitmap created for the width and height
     * of `mCurrentPage`. We then use the `render` method of `mCurrentPage` to
     * render the page's content for display on a screen into `bitmap`, and set that image as
     * the content of our field `ImageView mImageView`. Finally we call our method `updateUi`
     * to enable or disable the control buttons based on the current index, and set the activity's
     * title to reflect the page number.
     *
     * @param index The page index.
     */
    private fun showPage(index: Int) {
        if (mPdfRenderer!!.pageCount <= index) {
            return
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage!!.close()
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer!!.openPage(index)
        // Important: the destination bitmap must be ARGB (not RGB).
        val bitmap = Bitmap.createBitmap(
            mCurrentPage!!.width, mCurrentPage!!.height,
            Bitmap.Config.ARGB_8888
        )
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        // We are ready to show the Bitmap to user.
        mImageView!!.setImageBitmap(bitmap)
        updateUi()
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index, and sets the
     * activity's title to reflect the page number. First we initialize `int index` with the
     * page index of our field `Page mCurrentPage`, and initialize `int pageCount` with
     * the number of pages in the document of `PdfRenderer mPdfRenderer`. We enable the button
     * `mButtonPrevious` if `index` is not 0 and disable it if it is. We enable the button
     * `mButtonNext` if `index+1` is less than `pageCount` and disable it if it is
     * greater than or equal to `pageCount`. We then set the title associated with this activity
     * to the string formatted using the format string with resource id R.string.app_name_with_index
     * to display the value `index+1` (page number) and `pageCount`.
     */
    private fun updateUi() {
        val index = mCurrentPage!!.index
        val pageCount = mPdfRenderer!!.pageCount
        mButtonPrevious!!.isEnabled = 0 != index
        mButtonNext!!.isEnabled = index + 1 < pageCount

        activity?.title = getString(R.string.app_name_with_index, index + 1, pageCount)
    }

    /**
     * Called when a view has been clicked. We switch on the id of our parameter `View view`:
     *
     *  *
     * R.id.previous: we call our `showPage` method to display the page that is 1 page
     * before the current page index of our field `Page mCurrentPage` then break.
     *
     *  *
     * R.id.next: we call our `showPage` method to display the page that is 1 page
     * after the current page index of our field `Page mCurrentPage` then break.
     *
     *
     *
     * @param view The view that was clicked.
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.previous -> {
                // Move to the previous page
                showPage(mCurrentPage!!.index - 1)
            }
            R.id.next -> {
                // Move to the next page
                showPage(mCurrentPage!!.index + 1)
            }
        }
    }

    companion object {

        /**
         * Key string for saving the state of current page index in the bundle passed to our
         * `onSaveInstanceState` override and restored in our `onViewCreated` override.
         */
        private const val STATE_CURRENT_PAGE_INDEX = "current_page_index"

        /**
         * The filename of the PDF, both in our assets and in the cache directory.
         */
        private const val FILENAME = "sample.pdf"
    }

}
