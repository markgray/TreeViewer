/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.example.android.treeviewer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentProvider.PipeDataWriter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * A very simple content provider that can serve arbitrary asset files from
 * our .apk.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ApkFileProvider extends ContentProvider implements PipeDataWriter<InputStream> {
    /**
     * TAG for logging
     */
    private static final String TAG = "ApkFileProvider";

    /**
     * Implement this to initialize your content provider on startup, we simply return true
     *
     * @return true -- the provider was successfully loaded,
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * Implement this to handle query requests from clients. We initialize {@code displayNameIndex}
     * and {@code sizeIndex} to -1. If our parameter {@code String[] projection} is null we create
     * a {@code projection} consisting of both of our columns: DISPLAY_NAME and SIZE. We go through
     * the String entries in {@code projection} one by one indexed by {@code i}: if the current one
     * matches DISPLAY_NAME we set the variable {@code displayNameIndex} to the index value {@code i},
     * and if it matches SIZE we set the variable {@code sizeIndex} to {@code i}. We create a new
     * {@code MatrixCursor cursor} using {@code String[] projection} for the column names, and an
     * {@code Object[] result} to hold the row for {@code cursor} which we will build. We build this
     * row by going through the columns needed to build a row and if the index of the column matches
     * {@code displayNameIndex} we store the decoded path of the {@code Uri uri} in that column, and
     * if the index of the column matches {@code sizeIndex} we store the arbitrary value 42L in that
     * column (the size of the file being piped is unknown, but gmail needs it.) Then we add the row
     * {@code result} to {@code cursor} and return it to the caller.
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // content providers that support open and openAssetFile should support queries for all
        // android.provider.OpenableColumns.

        int displayNameIndex = -1;
        int sizeIndex = -1;

        // If projection is null, return all columns.
        if (projection == null) {
            projection = new String[]{
                    OpenableColumns.DISPLAY_NAME,
                    OpenableColumns.SIZE};
        }

        for (int i = 0; i < projection.length; i++) {
            if (OpenableColumns.DISPLAY_NAME.equals(projection[i])) {
                displayNameIndex = i;
            }
            if (OpenableColumns.SIZE.equals(projection[i])) {
                sizeIndex = i;
            }
        }

        MatrixCursor cursor = new MatrixCursor(projection);
        Object[] result = new Object[projection.length];

        for (int i = 0; i < result.length; i++) {
            if (i == displayNameIndex) {
                result[i] = uri.getPath();
            }
            if (i == sizeIndex) {
                result[i] = 42L; // Size is unknown, so let us pretend it is 42 and surprise, it works!
            }
        }

        cursor.addRow(result);
        return cursor;
    }

    /**
     * Implement this to handle requests to insert a new row. We return null, having done nothing.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Don't support inserts.
        return null;
    }

    /**
     * Implement this to handle requests to delete one or more rows. We return 0, having done nothing.
     *
     * @param uri       The full URI to query, including a row ID (if a specific record is requested).
     * @param selection An optional restriction to apply to rows when deleting.
     * @return The number of rows affected.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Don't support deletes.
        return 0;
    }

    /**
     * Implement this to handle requests to update one or more rows. We return 0, having done nothing.
     *
     * @param uri       The URI to query. This can potentially have a record ID if this
     *                  is an update request for a specific record.
     * @param values    A set of column_name/value pairs to update in the database.
     *                  This must not be {@code null}.
     * @param selection An optional filter to match rows to update.
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Don't support updates.
        return 0;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the given URI. We always
     * return the String ""text/html" for this demo.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        // For this sample, assume all files are html.
        return "text/html";
    }

    /**
     * Override this to handle requests to open a file blob. Wrapped in a try block intended to catch
     * IOException, we set {@code String path} to the decoded path of {@code Uri uri}
     * (Since {@code uri} is "content://com.example.android.treeviewer.ApkFileProvider/2/res/drawable-nodpi-v4/jellies.jpg"
     * "/2/res/drawable-nodpi-v4/jellies.jpg" in our case), locate the index of the '/' character
     * following the cookie to set {@code off} and if we don't find it where we expect it we throw
     * FileNotFoundException. We extract the cookie substring ("2" in our case) and convert it to
     * {@code int cookie}, and set {@code String assetPath} to the rest of the {@code path} following
     * the '/' character that terminated the cookie part of the path. ("res/drawable-nodpi-v4/jellies.jpg"
     * in our case).
     * <p>
     * We create {@code AssetFileDescriptor asset} by getting the context this provider is running in,
     * using it to get an {@code AssetManager} instance for the application's package, which we in
     * turn use to open a non-asset file descriptor using {@code cookie} and {@code assetPath}.
     * <p>
     * We then create and return a {@code ParcelFileDescriptor} constructed using the {@code ParcelFileDescriptor}
     * returned by {@code openPipeHelper} which it builds using {@code uri} as the URI whose data is to be written,
     * a mime type of "text/html", a null option Bundle, a {@code FileInputStream} created from {@code asset}
     * as the arguments to the function that will actually stream the data, and "this" as the
     * {@code PipeDataWriter<InputStream>} that will actually stream the data from the {@code FileInputStream}
     * argument.
     *
     * @param uri  The URI whose file is to be opened.
     * @param mode Access mode for the file.  May be "r" for read-only access,
     *             "rw" for read and write access, or "rwt" for read and write access
     *             that truncates any existing file.
     * @return Returns a new ParcelFileDescriptor which you can use to access
     * the file.
     * @throws FileNotFoundException Throws FileNotFoundException if there is
     *                               no file associated with the given URI or the mode is invalid.
     * @throws SecurityException     Throws SecurityException if the caller does
     *                               not have permission to access the file.
     */
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        // Try to open an asset with the given name.
        try {
            String path = uri.getPath();
            int off = path.indexOf('/', 1);
            if (off < 0 || off >= (path.length() - 1)) {
                throw new FileNotFoundException("Unable to open " + uri);
            }
            int cookie = Integer.parseInt(path.substring(1, off));
            String assetPath = path.substring(off + 1);
            Log.i("ApkFileProvider", "Providing:" + assetPath);
            //noinspection ConstantConditions
            AssetFileDescriptor asset = getContext().getAssets().openNonAssetFd(cookie, assetPath);
            return new ParcelFileDescriptor(openPipeHelper(uri, "text/html", null, asset.createInputStream(), this));
        } catch (IOException e) {
            throw new FileNotFoundException("Unable to open " + uri);
        }
    }

    /**
     * Called from a background thread to stream data out to a pipe. Note that the pipe is blocking,
     * so this thread can block on writes for an arbitrary amount of time if the client is slow
     * at reading.
     * <p>
     * First we allocate 8192 bytes for {@code byte[] buffer}, declare an {@code int n} to hold the
     * number of bytes read for each read attempt, and create {@code FileOutputStream fout} using
     * the actual FileDescriptor associated with our parameter {@code ParcelFileDescriptor output}.
     * <p>
     * Then wrapped in a try block intended to catch IOException we read from {@code InputStream args}
     * into {@code buffer} capturing the number of bytes read in {@code n} and for as long as
     * {@code n} is greater than or equal to 0, we write the {@code n} bytes in {@code buffer} to
     * {@code FileOutputStream fout}. When we are at the end of file of {@code args}, {@code n}
     * will be -1 and we fall through to the finally block where we close both {@code args} and
     * {@code fout}.
     *
     * @param output   The pipe where data should be written.  This will be
     *                 closed for you upon returning from this function.
     * @param uri      The URI whose data is to be written.
     * @param mimeType The desired type of data to be written.
     * @param opts     Options supplied by caller.
     * @param args     Our own custom arguments, the {@code InputStream} we will use to read our resource
     *                 file from.
     */
    @Override
    public void writeDataToPipe(@NonNull ParcelFileDescriptor output,
                                @NonNull Uri uri,
                                @NonNull String mimeType,
                                Bundle opts,
                                InputStream args) {
        // Transfer data from the asset to the pipe the client is reading.
        byte[] buffer = new byte[8192];
        int n;
        FileOutputStream fout = new FileOutputStream(output.getFileDescriptor());
        try {
            while ((n = args.read(buffer)) >= 0) {
                fout.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.i(TAG, "Failed transferring", e);
        } finally {
            try {
                args.close();
            } catch (IOException e) {
                Log.i(TAG, e.getLocalizedMessage());
            }
            try {
                fout.close();
            } catch (IOException e) {
                Log.i(TAG, e.getLocalizedMessage());
            }
        }
    }
}
