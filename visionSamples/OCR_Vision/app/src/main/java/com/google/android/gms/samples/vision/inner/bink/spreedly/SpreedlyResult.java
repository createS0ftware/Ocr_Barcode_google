package com.google.android.gms.samples.vision.inner.bink.spreedly;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 24/08/2016.
 */

public class SpreedlyResult {

    @SerializedName("transaction")
    Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }
}
