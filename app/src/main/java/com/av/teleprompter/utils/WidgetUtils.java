package com.av.teleprompter.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.data.ScriptContract;
import com.av.teleprompter.widget.TeleWidget;

/**
 * Created by Antonio Vitiello
 */
public class WidgetUtils {

    private static final String FIRST_START_KEY = "firstStart";
    private static final long FADE_ANIMATION_DURATION = 500L;

    public static void updateWidget(Script script, Context context) {
        if (script != null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TeleWidget.class));
            TeleWidget.updateWidget(context, appWidgetManager, appWidgetIds, script);
        }
    }

    public static void initOnFirstLaunch(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean(FIRST_START_KEY, true)) {

            preferences.edit().putBoolean(FIRST_START_KEY, false)
                    .putBoolean(context.getString(R.string.pref_mirror_key), false)
                    .putInt(context.getString(R.string.pref_background_color_key), -16777216)
                    .putString(context.getString(R.string.pref_font_key), context.getString(R.string.font_face_default))
                    .putInt(context.getString(R.string.pref_text_size_key), context.getResources().getInteger(R.integer.pref_text_size_default))
                    .putInt(context.getString(R.string.pref_text_color_key), -8320)
                    .putInt(context.getString(R.string.pref_speed_key), context.getResources().getInteger(R.integer.pref_scroll_speed_default))
                    .apply();

            Script script = Script.deafult(context);
            updateWidget(script, context);

            ContentValues contentValues = new ContentValues();
            contentValues.put(ScriptContract.ScriptEntry.TITLE, script.getTitle());
            contentValues.put(ScriptContract.ScriptEntry.BODY, script.getBody());
            contentValues.put(ScriptContract.ScriptEntry._ID, script.getId());
            context.getContentResolver().insert(ScriptContract.ScriptEntry.CONTENT_URI, contentValues);
        }

    }

    public static void fadeOutAndGone(final View v) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(FADE_ANIMATION_DURATION);

        fadeOut.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
                //do nothind
            }

            public void onAnimationStart(Animation animation) {
                //do nothind
            }
        });

        v.startAnimation(fadeOut);
    }

}
