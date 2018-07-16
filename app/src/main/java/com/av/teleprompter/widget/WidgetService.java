package com.av.teleprompter.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.av.teleprompter.R;
import com.av.teleprompter.application.TeleApplication;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.data.ScriptContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio Vitiello
 */
public class WidgetService extends IntentService {
    public static String ACTION_UPDATE_WIDGET = TeleApplication.getStringResource(R.string.action_update_widget);
    private List<Script> mScripts = new ArrayList<>();

    public WidgetService() {
        super("WidgetService");
    }

    public static void setActionUpdateWidget(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_UPDATE_WIDGET)) {
                handleActionUpdateWidget();
            }
        } else {
            handleActionUpdateWidget();
        }
    }


    private void handleActionUpdateWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TeleWidget.class));
        new FetchTask().execute();
        Script script = mScripts.isEmpty() ? Script.deafult(this) : mScripts.get(0);
        TeleWidget.updateWidget(this, appWidgetManager, appWidgetIds, script);

    }

    public class FetchTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            try {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(ScriptContract.ScriptEntry.TITLE));
                    Long id = cursor.getLong(cursor.getColumnIndex(ScriptContract.ScriptEntry._ID));
                    String body = cursor.getString(cursor.getColumnIndex(ScriptContract.ScriptEntry.BODY));
                    mScripts.add(new Script(title, body, id));
                }
            } finally {
                cursor.close();
            }
        }

    }

}
