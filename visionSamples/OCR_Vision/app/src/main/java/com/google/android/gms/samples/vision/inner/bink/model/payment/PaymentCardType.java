package com.google.android.gms.samples.vision.inner.bink.model.payment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 14/07/16.
 */

public enum PaymentCardType {
    @SerializedName("1")
    VISA("1"),

    @SerializedName("2")
    MASTERCARD("2"),

    @SerializedName("3")
    AMEX("3");

    String id;

    PaymentCardType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
