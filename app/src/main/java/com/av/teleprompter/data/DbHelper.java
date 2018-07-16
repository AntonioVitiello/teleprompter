package com.av.teleprompter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Antonio Vitiello
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Scripts.db";
    private static final int DATABASE_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + ScriptContract.ScriptEntry.TABLE_NAME + " (" +
                ScriptContract.ScriptEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ScriptContract.ScriptEntry.TITLE + " TEXT NOT NULL, " +
                ScriptContract.ScriptEntry.BODY + " TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ScriptContract.ScriptEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
