package com.google.android.gms.samples.vision.inner.bink.module;

import android.content.Context;

import com.loyaltyangels.bink.api.ApiClient;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.model.AppModelListener;
import com.loyaltyangels.bink.model.BinkCache;
import com.loyaltyangels.bink.model.MemoryCache;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.model.ModelListener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jmcdonnell on 27/07/2016.
 */

@Module(includes = {ApiModule.class, AppModule.class})
public class ModelModule {

    @Provides
    @Singleton
    public BinkCache provideBinkCache(ApiConfig apiConfig) {
        return new MemoryCache(apiConfig);
    }

    @Provides
    @Singleton
    public Model provideModel(ApiClient api, BinkCache cache, ApiConfig apiConfig, Context context) {
        ModelListener listener = new AppModelListener(context);
        return new Model(api, cache, apiConfig, listener);
    }

}
