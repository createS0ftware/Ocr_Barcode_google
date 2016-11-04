package com.google.android.gms.samples.vision.inner.bink.api;

import android.content.Context;

import com.loyaltyangels.bink.api.config.ApiConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jm on 12/07/16.
 * <p>
 * This intercepts network requests and injects the current user token (if one exists) in order
 * to authenticate API calls.
 */

public class ApiAuthenticationInterceptor implements Interceptor {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN = "Token ";

    private ApiConfig apiConfig;

    public ApiAuthenticationInterceptor(Context context, ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String apiKey = apiConfig.getUserToken();

        // TODO Get API key from ApiConfig when we move over to that.

        Request request;

        if (apiKey != null) {
            request = chain.request().newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, TOKEN + apiKey)
                    .build();
        } else {
            request = chain.request();
        }


        Response response = chain.proceed(request);

        /**
         * Always return a valid JSON object.
         */
        if (response.body().contentLength() == 0) {
            response = response.newBuilder()
                    .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                    .build();
        }

        return response;
    }

}
