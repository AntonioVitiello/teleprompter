package com.av.teleprompter.application;

import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.av.teleprompter.R;
import com.av.teleprompter.log.TimberLogImplementation;
import com.av.teleprompter.utils.WidgetUtils;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Antonio Vitiello
 */
public class TeleApplication extends Application {
    private static Resources mResources;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @NonNull
    public static String getStringResource(int resId) {
        return mResources.getString(resId);
    }

    @NonNull
    public static String getStringResource(int resId, Object... formatArgs) {
        return mResources.getString(resId, formatArgs);
    }

    public static int getIntResource(int resId) {
        return mResources.getInteger(resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mResources = this.getApplicationContext().getResources();

        // Analytics initialization
        sAnalytics = GoogleAnalytics.getInstance(this);

        // Timber initialization
        TimberLogImplementation.init("Antonio");

        // Add default script on very first app launch and update Widget
        WidgetUtils.initOnFirstLaunch(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

}