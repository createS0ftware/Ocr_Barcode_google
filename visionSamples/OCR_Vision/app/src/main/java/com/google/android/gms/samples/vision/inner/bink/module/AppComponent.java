package com.google.android.gms.samples.vision.inner.bink.module;

import android.content.Context;

import com.loyaltyangels.bink.analytics.Tracker;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.config.AppConfig;
import com.loyaltyangels.bink.getaddress.GetAddressApi;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.spreedly.SpreedlyApi;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by hansonaboagye 05/08/16.
 */

@Singleton
@Component(modules = {AppModule.class, ModelModule.class, SpreedlyModule.class, ApiModule.class, GetAddressModule.class})
public interface AppComponent {
    Context context();

    Model model();

    AppConfig appConfig();

    ApiConfig apiConfig();

    Tracker tracker();

    SpreedlyApi spreedlyApi();

    GetAddressApi getAddressApi();

    OkHttpClient httpClient();
}

