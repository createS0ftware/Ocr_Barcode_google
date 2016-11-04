package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;

/**
 * Created by jmcdonnell on 23/08/2016.
 */

public class IdentifySchemePayload {

    @SerializedName("base64img")
    String base64Image;

    public static IdentifySchemePayload create(byte[] image) throws IOException {
        byte[] encodedImage = Base64.encode(image, Base64.NO_WRAP);

        IdentifySchemePayload payload = new IdentifySchemePayload();
        payload.setEncodedImage(new String(encodedImage));
        return payload;
    }

    public void setEncodedImage(String base64Image) {
        this.base64Image = base64Image;
    }
}
