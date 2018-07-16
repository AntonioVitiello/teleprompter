package com.av.teleprompter.data.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.av.teleprompter.data.ScriptContract;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello
 */
public class ScriptAsyncTaskLoader extends AsyncTaskLoader<Cursor> {

    Cursor cursor = null;

    public ScriptAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            // Delivers any previously loaded data immediately
            deliverResult(cursor);
        } else {
            // Force a new load
            forceLoad();
        }
    }

    // loadInBackground() performs asynchronous loading of data
    @Override
    public Cursor loadInBackground() {
        try {
            return getContext().getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI, null, null, null, null);
        } catch (Exception exc) {
            Timber.e(exc, "Failed to asynchronously load data.");
            return null;
        }
    }

    // deliverResult sends the result of the load, a Cursor, to the registered listener


    @Override
    public void deliverResult(@Nullable Cursor data) {
        cursor = data;
        super.deliverResult(data);
    }

}
