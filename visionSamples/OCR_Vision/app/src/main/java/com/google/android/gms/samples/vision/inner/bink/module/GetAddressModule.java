package com.google.android.gms.samples.vision.inner.bink.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loyaltyangels.bink.getaddress.AddressData;
import com.loyaltyangels.bink.getaddress.GetAddressApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by jmcdonnell on 09/09/2016.
 */
@Module
public class GetAddressModule {

    private static final String GET_ADDRESS_API_KEY = "KLogDsJB7E2Oy-2WHME-tQ2451";

    @Provides
    @Singleton
    public GetAddressApi provideGetAddressApi() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TypeToken.get(AddressData.class).getType(), new AddressData.TypeAdapter())
                .create();

        OkHttpClient okClient = new OkHttpClient();
        return new GetAddressApi(okClient, gson, GET_ADDRESS_API_KEY);
    }

}
