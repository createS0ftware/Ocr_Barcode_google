package com.google.android.gms.samples.vision.inner.bink.getaddress;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

/**
 * Created by jmcdonnell on 09/09/2016.
 * <p>
 * Implements the getaddress.io API calls.
 */
public class GetAddressApi {

    private static final String GET_ADDRESS_IO_URL = "https://api.getAddress.io/v2/uk/";

    private OkHttpClient okClient;
    private String apiKey;
    private Gson gson;

    public GetAddressApi(OkHttpClient okClient, Gson gson, String apiKey) {
        this.okClient = okClient;
        this.gson = gson;
        this.apiKey = apiKey;
    }

    public Observable<GetAddressResponse> searchPostcode(@NonNull String postcode) {
        return Observable.create(subscriber -> {
            HttpUrl url = HttpUrl.parse(GET_ADDRESS_IO_URL).newBuilder()
                    .addPathSegment(postcode)
                    .build();

            String auth = Base64.encodeToString(String.format("%s:%s", "api-key", apiKey).getBytes(), Base64.NO_WRAP);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Basic " + auth)
                    .build();

            try {
                Response response = okClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    GetAddressResponse result = gson.fromJson(response.body().charStream(), GetAddressResponse.class);
                    subscriber.onNext(result);
                } else {
                    subscriber.onError(new GetAddressException("GetAddress HTTP error: " + response.message() + ", HTTP code: " + response.code()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }

            subscriber.onCompleted();
        });
    }

}
