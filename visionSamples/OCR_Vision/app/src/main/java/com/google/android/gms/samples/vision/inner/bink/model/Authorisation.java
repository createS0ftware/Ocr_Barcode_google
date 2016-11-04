package com.google.android.gms.samples.vision.inner.bink.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 16/08/2016.
 */

public class Authorisation {

    @SerializedName("email")
    String email;
    @SerializedName("api_key")
    String apiKey;

    public String getEmail() {
        return email;
    }

    public String getApiKey() {
        return apiKey;
    }
}
