package com.google.android.gms.samples.vision.inner.bink.analytics;

import android.util.Log;

/**
 * Created by jmcdonnell on 20/09/2016.
 */

public class LogTracker implements Tracker {

    private static final String TAG = LogTracker.class.getSimpleName();

    @Override
    public void trackScreen(Screen screen, Object... args) {
        Log.d(TAG, "Track Screen: " + screen.getName(args));
    }

    @Override
    public void trackEvent(Category category, Action action) {
        trackEvent(category, action, null);
    }

    @Override
    public void trackEvent(Category category, Action action, String label) {
        Log.d(TAG, String.format("Track Event: Category:%s, Action:%s, Label:%s", category, action, label));
    }
}
