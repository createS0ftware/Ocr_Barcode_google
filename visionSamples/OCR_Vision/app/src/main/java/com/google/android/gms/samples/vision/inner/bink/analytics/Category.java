package com.google.android.gms.samples.vision.inner.bink.analytics;

/**
 * Created by jmcdonnell on 20/09/2016.
 */

public enum Category {

    ButtonPress("buttonPress"),
    AddPaymentCard("addPaymentCard");

    String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}