package com.google.android.gms.samples.vision.inner.bink.spreedly;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 24/08/2016.
 */

public class SpreedlyPayload {

    public static SpreedlyPayload create(PaymentMethod paymentMethod) {
        SpreedlyPayload payload = new SpreedlyPayload();
        payload.paymentMethod = paymentMethod;
        return payload;
    }

    @SerializedName("payment_method")
    PaymentMethod paymentMethod;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
