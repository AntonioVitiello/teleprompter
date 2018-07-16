package com.av.teleprompter.log;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello
 */
public class TimberLogImplementation {
    public static void init(final String logTag) {
        Timber.plant(new ReleaseTree(logTag));
    }
}
