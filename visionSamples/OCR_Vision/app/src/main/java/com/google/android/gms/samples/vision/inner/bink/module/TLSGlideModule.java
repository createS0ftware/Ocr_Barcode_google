package com.google.android.gms.samples.vision.inner.bink.module;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.loyaltyangels.bink.App;

import java.io.InputStream;

import okhttp3.OkHttpClient;


/**
 * Created by hansonaboagye on 06/09/16.
 */
public class TLSGlideModule implements GlideModule {

    private OkHttpClient newHttpClient;


    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        newHttpClient = ((App)context).getAppComponent().httpClient();
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(newHttpClient));
    }


}

