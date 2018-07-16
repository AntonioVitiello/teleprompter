package com.av.teleprompter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.data.ScriptContract;
import com.av.teleprompter.view.EditActivity;
import com.av.teleprompter.view.PlayActivity;
import com.av.teleprompter.view.PrePlayActivity;
import com.av.teleprompter.view.SettingsActivity;

import static com.av.teleprompter.utils.WidgetUtils.updateWidget;

/**
 * Created by Antonio Vitiello
 */
public class ActionUtils {

    public static void prePlay(Script script, Context context) {
        Intent intent = new Intent(context, PrePlayActivity.class);
        intent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
        context.startActivity(intent);
        updateWidget(script, context);
    }

    public static void modify(Script script, Context context) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
        context.startActivity(intent);
    }

    public static void delete(Script script, View layout, Context context) {
        // Build appropriate uri with String row id appended and delete a single row of data using a ContentResolver
        Uri uri = ScriptContract.ScriptEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Long.toString(script.getId())).build();
        context.getContentResolver().delete(uri, null, null);
        if(layout != null)
            Snackbar.make(layout, context.getString(R.string.script_removed, script.getTitle()), Snackbar.LENGTH_LONG).show();
    }

    public static void play(Script script, Context context) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
        context.startActivity(intent);
        updateWidget(script, context);
    }

    public static void settings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
