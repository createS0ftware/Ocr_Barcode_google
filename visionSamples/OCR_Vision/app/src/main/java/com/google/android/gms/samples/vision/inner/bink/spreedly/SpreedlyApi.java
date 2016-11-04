package com.google.android.gms.samples.vision.inner.bink.spreedly;

import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;

/**
 * Created by jmcdonnell on 24/08/2016.
 */

public class SpreedlyApi {

    private static final String SPREEDLY_PAYMENT_METHODS_URL = "https://core.spreedly.com/v1/payment_methods.json";

    private OkHttpClient okClient;
    private String applicationSecret;
    private String environmentKey;

    public SpreedlyApi(OkHttpClient okClient, String applicationSecret, String environmentKey) {
        this.okClient = okClient;
        this.applicationSecret = applicationSecret;
        this.environmentKey = environmentKey;
    }

    public Observable<SpreedlyResult> addCreditCard(SpreedlyPayload payload) {
        return Observable.create(subscriber -> {
            String json = new Gson().toJson(payload);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(SPREEDLY_PAYMENT_METHODS_URL)
                    .addHeader("Authorization", "Basic " + encodeBasicAuthorisation())
                    .post(body)
                    .build();

            try {
                Response response = okClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    SpreedlyResult result = new Gson().fromJson(response.body().charStream(), SpreedlyResult.class);
                    subscriber.onNext(result);
                } else {
                    subscriber.onError(new SpreedlyException("Spreedly HTTP error: " + response.message() + ", HTTP code: " + response.code()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }

            subscriber.onCompleted();
        });
    }

    private String encodeBasicAuthorisation() {
        return Base64.encodeToString(String.format("%s:%s",
                environmentKey, applicationSecret).getBytes(), Base64.NO_WRAP);
    }

}
