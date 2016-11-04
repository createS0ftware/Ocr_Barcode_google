package com.google.android.gms.samples.vision.inner.bink.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 17/08/2016.
 */

public class PromoValidation {

    @SerializedName("valid")
    boolean valid;

    public boolean isValid() {
        return valid;
    }
}
