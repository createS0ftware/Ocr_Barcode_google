package com.google.android.gms.samples.vision.inner.bink.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 17/08/2016.
 */

public class Registration {

    @SerializedName("email")
    String email;

    @SerializedName("password")
    String password;

    @SerializedName("promo_code")
    String promoCode;

    public static Registration create(String email, String password, String promoCode) {
        Registration registration = new Registration();
        registration.email = email;
        registration.password = password;
        registration.promoCode = promoCode;
        return registration;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPromoCode() {
        return promoCode;
    }
}
