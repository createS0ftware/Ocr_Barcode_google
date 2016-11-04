package com.google.android.gms.samples.vision.inner.bink.module;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loyaltyangels.bink.BuildConfig;
import com.loyaltyangels.bink.api.ApiAuthenticationInterceptor;
import com.loyaltyangels.bink.api.ApiClient;
import com.loyaltyangels.bink.api.BinkService;
import com.loyaltyangels.bink.api.BinkUrlProvider;
import com.loyaltyangels.bink.api.HostProvider;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.api.config.PreferencesApiConfig;
import com.loyaltyangels.bink.model.scheme.AddSchemePayload;
import com.loyaltyangels.bink.model.user.settings.SettingsUpdate;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jm on 12/07/16.
 * <p>
 * This module is responsible for handling anything todo with the networking and API client.
 * In the future, the base URL can be dynamically created for testing and development build
 * purposes.
 */
@Module(includes = AppModule.class)
public class ApiModule {

    private static final String API_CONFIG_PREFS = "api_preferences";
    private static final String CERTIFICATE_PIN_PATTERN = "api.chingrewards.com";
    private static final String CERTIFICATE_PIN = "sha256/T/d0TinUmWHw1SU8AQSrmmaCS7poJWYHHBC1u3b759E=";

    @Provides
    @Singleton
    HostProvider provideHost() {
        return new BinkUrlProvider();
    }

    @Provides
    @Singleton
    ApiClient provideApiClient(BinkService binkService) {
        return new ApiClient(binkService);
    }

    @Provides
    @Singleton
    ApiConfig provideApiConfig(Context context) {
        return new PreferencesApiConfig(API_CONFIG_PREFS, context);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(TypeToken.get(SettingsUpdate.class).getType(), new SettingsUpdate.TypeAdapter())
                .registerTypeAdapter(TypeToken.get(AddSchemePayload.class).getType(), new AddSchemePayload.TypeAdapter())
                .create();
    }

    @Provides
    @Singleton
    BinkService provideBinkService(OkHttpClient okClient, HostProvider hostProvider, Gson gson) {
        return new Retrofit.Builder()
                .client(okClient)
                .baseUrl(hostProvider.getUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(BinkService.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkClient(Context context, ApiConfig apiConfig) {
        ApiAuthenticationInterceptor authInterceptor = new ApiAuthenticationInterceptor(context, apiConfig);

        /**
         * For more info on public key pinning see:
         * https://developer.mozilla.org/en/docs/Web/Security/Public_Key_Pinning
         */
        CertificatePinner.Builder certificatePinnerBuilder = new CertificatePinner.Builder();

        if (!BuildConfig.DEBUG) {
            certificatePinnerBuilder.add(CERTIFICATE_PIN_PATTERN, CERTIFICATE_PIN);
        }

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            return new OkHttpClient.Builder()
                    .addNetworkInterceptor(authInterceptor)
                    .certificatePinner(certificatePinnerBuilder.build())
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(new TLSSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        // we want the code to crash here and propagate an NPE
        return null;
    }

}
