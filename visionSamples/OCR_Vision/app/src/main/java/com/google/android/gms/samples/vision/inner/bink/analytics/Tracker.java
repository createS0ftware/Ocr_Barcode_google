package com.google.android.gms.samples.vision.inner.bink.analytics;

/**
 * Created by jmcdonnell on 20/09/2016.
 */

public interface Tracker {

    void trackScreen(Screen screen, Object... args);

    void trackEvent(Category category, Action action);

    void trackEvent(Category category, Action action, String label);

}
