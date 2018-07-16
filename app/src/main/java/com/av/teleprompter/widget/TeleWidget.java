package com.av.teleprompter.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.view.MainActivity;
import com.av.teleprompter.view.PlayActivity;

/**
 * Created by Antonio Vitiello
 */
public class TeleWidget extends AppWidgetProvider {

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Script script) {

        for (int appWidgetId : appWidgetIds) {
            if (script == null) {
                script = new Script(context.getString(R.string.app_name), context.getString(R.string.script_body), 1);
            }

            Intent mainIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(mainIntent);

            Intent playIntent = new Intent(context, PlayActivity.class);
            playIntent.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, script);
            stackBuilder.addNextIntent(playIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tele_widget);
            views.setTextViewText(R.id.tv_widget_title, script.getTitle());
            views.setTextViewText(R.id.tv_widget_body, script.getBody());
            views.setContentDescription(R.id.tv_widget_body, script.getBody());
            views.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        WidgetService.setActionUpdateWidget(context);

    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

