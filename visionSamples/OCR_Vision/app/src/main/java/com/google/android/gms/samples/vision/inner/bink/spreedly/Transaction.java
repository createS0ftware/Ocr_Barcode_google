package com.google.android.gms.samples.vision.inner.bink.spreedly;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 24/08/2016.
 */

public class Transaction {

    @SerializedName("payment_method")
    PaymentMethod paymentMethod;

    @SerializedName("succeeded")
    boolean succeeded;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public boolean succeeded() {
        return succeeded;
    }

    public static class PaymentMethod {
        @SerializedName("token")
        String token;

        @SerializedName("fingerprint")
        String fingerprint;

        public String getToken() {
            return token;
        }

        public String getFingerprint() {
            return fingerprint;
        }
    }
}
