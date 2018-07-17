package com.av.teleprompter.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.data.ScriptContract;
import com.av.teleprompter.view.BaseActivity;
import com.av.teleprompter.view.EditActivity;
import com.av.teleprompter.view.PlayActivity;
import com.av.teleprompter.view.PrePlayActivity;
import com.av.teleprompter.view.SettingsActivity;

import static com.av.teleprompter.utils.WidgetUtils.updateWidget;

/**
 * Created by Antonio Vitiello
 */
public class ActionUtils {

    public static void prePlay(Script script, BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, PrePlayActivity.class);
        intent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
        baseActivity.startActivity(intent);
        updateWidget(script, baseActivity);
    }

    public static void modify(Script script, BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, EditActivity.class);
        intent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
        baseActivity.startActivity(intent);
    }

    public static void delete(Script script, View layout, BaseActivity baseActivity) {
        // Build appropriate uri with String row id appended and delete a single row of data using a ContentResolver
        Uri uri = ScriptContract.ScriptEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Long.toString(script.getId())).build();
        baseActivity.getContentResolver().delete(uri, null, null);
        if(layout != null)
            Snackbar.make(layout, baseActivity.getString(R.string.script_removed, script.getTitle()), Snackbar.LENGTH_LONG).show();
    }

    public static void play(Script script, BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, PlayActivity.class);
        intent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
        baseActivity.startActivity(intent);
        updateWidget(script, baseActivity);
    }

    public static void settings(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, SettingsActivity.class);
        baseActivity.startActivity(intent);
    }

}
