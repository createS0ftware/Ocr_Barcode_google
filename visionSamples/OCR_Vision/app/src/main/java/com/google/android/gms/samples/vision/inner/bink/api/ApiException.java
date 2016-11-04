package com.google.android.gms.samples.vision.inner.bink.api;

import com.google.gson.JsonObject;

/**
 * Created by jm on 12/07/16.
 *
 * TODO Add API error code enum
 */

public class ApiException extends Exception {

    private JsonObject response;

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public void setErrorResponse(JsonObject response) {
        this.response = response;
    }

    public JsonObject getErrorResponse() {
        return response;
    }
}
