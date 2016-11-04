package com.google.android.gms.samples.vision.inner.bink.analytics;

import com.google.android.gms.analytics.HitBuilders;

import java.util.Map;

/**
 * Created by jmcdonnell on 20/09/2016.
 */

public class GoogleTracker implements Tracker {

    private com.google.android.gms.analytics.Tracker tracker;

    public GoogleTracker(com.google.android.gms.analytics.Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void trackScreen(Screen screen, Object... args) {
        String screenName = screen.getName(args);
        tracker.setScreenName(screenName);

        Map<String, String> event = new HitBuilders.ScreenViewBuilder()
                .set("$cd", screenName)
                .build();

        tracker.send(event);
    }

    @Override
    public void trackEvent(Category category, Action action) {
        trackEvent(category, action, null);
    }

    @Override
    public void trackEvent(Category category, Action action, String label) {
        Map<String, String> params = new HitBuilders.EventBuilder(category.getName(), action.getName())
                .setLabel(label)
                .build();

        tracker.send(params);
    }
}
