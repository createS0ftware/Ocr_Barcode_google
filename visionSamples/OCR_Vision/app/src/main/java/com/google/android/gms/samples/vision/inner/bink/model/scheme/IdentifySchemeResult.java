package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 23/08/2016.
 */

public class IdentifySchemeResult {

    @SerializedName("scheme_id")
    String schemeId;

    public String getSchemeId() {
        return schemeId;
    }
}
