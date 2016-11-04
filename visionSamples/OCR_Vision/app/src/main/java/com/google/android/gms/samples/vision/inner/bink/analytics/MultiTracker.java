package com.google.android.gms.samples.vision.inner.bink.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * This class is used to wrap all Analytics trackers that may be required.
 * <p/>
 * Created by jmcdonnell on 20/09/2016.
 */
public class MultiTracker implements Tracker {

    private List<Tracker> trackers;

    public MultiTracker(@NonNull List<Tracker> trackers) {
        this.trackers = trackers;
    }

    @Override
    public void trackScreen(@NonNull Screen screen, @Nullable Object... args) {
        for (Tracker tracker : trackers) {
            tracker.trackScreen(screen, args);
        }
    }

    @Override
    public void trackEvent(Category category, Action action) {
        for (Tracker tracker : trackers) {
            tracker.trackEvent(category, action, null);
        }
    }

    @Override
    public void trackEvent(Category category, Action action, String label) {
        for (Tracker tracker : trackers) {
            tracker.trackEvent(category, action, label);
        }
    }
}
