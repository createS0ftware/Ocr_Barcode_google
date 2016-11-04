package com.google.android.gms.samples.vision.inner.bink.analytics;

/**
 * Created by jmcdonnell on 20/09/2016.
 */

public enum Action {

    PointsToggle("buttonPressPointsToggle"),
    BarclayCard("Barclaycard"),
    LaunchApp("buttonPressLaunchApp");

    String name;

    Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
