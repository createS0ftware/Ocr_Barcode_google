package com.google.android.gms.samples.vision.inner.bink.module;

import com.loyaltyangels.bink.spreedly.SpreedlyApi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by jmcdonnell on 24/08/2016.
 */

@Module
public class SpreedlyModule {

    private static final String APPLICATION_SECRET = "94iV3Iyvky86avhdjLgIh0z9IFeB0pw4cZvu64ufRgaur46mTM4xepsPDOdxVH51";
    private static final String ENVIRONMENT_KEY = "1Lf7DiKgkcx5Anw7QxWdDxaKtTa";

    @Provides
    @Singleton
    SpreedlyApi provideSpreedlyApi() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return new SpreedlyApi(okClient, APPLICATION_SECRET, ENVIRONMENT_KEY);
    }

}
