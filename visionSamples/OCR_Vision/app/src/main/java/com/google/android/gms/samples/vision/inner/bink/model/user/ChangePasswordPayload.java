package com.google.android.gms.samples.vision.inner.bink.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 13/09/2016.
 */

public class ChangePasswordPayload {

    @SerializedName("password")
    String password;

    public void setPassword(String password) {
        this.password = password;
    }
}
