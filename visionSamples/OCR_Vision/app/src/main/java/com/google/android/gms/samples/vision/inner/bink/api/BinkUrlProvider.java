package com.google.android.gms.samples.vision.inner.bink.api;

import okhttp3.HttpUrl;

/**
 * Created by jm on 13/07/16
 * <p>
 * Will provide either the live or development/Staging host depending on the current build settings.
 * Currently only provides the live host.
 */

public class BinkUrlProvider implements HostProvider {

    private static final HttpUrl URL_LIVE = new HttpUrl.Builder()
            .scheme("https")
            .host("api.chingrewards.com")
            .build();

    @Override
    public HttpUrl getUrl() {
        return URL_LIVE;
    }
}
