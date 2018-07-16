package com.av.teleprompter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Antonio Vitiello
 */
public class ScriptProvider extends ContentProvider {

    public static final int SCRIPTS = 100;
    public static final int SCRIPTS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private com.av.teleprompter.data.DbHelper DbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ScriptContract.AUTHORITY, ScriptContract.PATH_SCRIPTS, SCRIPTS);
        uriMatcher.addURI(ScriptContract.AUTHORITY, ScriptContract.PATH_SCRIPTS + "/#", SCRIPTS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        DbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case SCRIPTS: {
                long id = db.insert(ScriptContract.ScriptEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(ScriptContract.ScriptEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert rows into " + uri);
                }
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = DbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case SCRIPTS:
                cursor = db.query(ScriptContract.ScriptEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int scriptsDeleted;

        switch (match) {
            case SCRIPTS:
                scriptsDeleted = db.delete(ScriptContract.ScriptEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ScriptContract.ScriptEntry.TABLE_NAME + "'");
                break;
            case SCRIPTS_WITH_ID:
                // Get the script ID from the URI path
                String id = uri.getPathSegments().get(1);
                scriptsDeleted = db.delete(ScriptContract.ScriptEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (scriptsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return scriptsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int scriptsUpdated = 0;

        if (contentValues == null) {
            throw new NullPointerException("ContentValues can't be null.");
        }

        switch (sUriMatcher.match(uri)) {
            case SCRIPTS: {
                scriptsUpdated = db.update(ScriptContract.ScriptEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case SCRIPTS_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                scriptsUpdated = db.update(ScriptContract.ScriptEntry.TABLE_NAME,
                        contentValues,
                        ScriptContract.ScriptEntry._ID + " = ?",
                        new String[]{id});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (scriptsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return scriptsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        String type;
        switch (match) {
            case SCRIPTS:
                type = ScriptContract.ScriptEntry.CONTENT_DIR_TYPE;
                break;
            case SCRIPTS_WITH_ID:
                type = ScriptContract.ScriptEntry.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new SQLException("Unknown Uri: " + uri);
        }
        return type;
    }

}
