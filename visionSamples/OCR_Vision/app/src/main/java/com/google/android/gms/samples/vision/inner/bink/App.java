package com.google.android.gms.samples.vision.inner.bink;

import com.apptentive.android.sdk.Apptentive;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.loyaltyangels.bink.module.AppComponent;
import com.loyaltyangels.bink.module.DaggerAppComponent;
import com.loyaltyangels.bink.util.BinkUtil;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by akumar on 06/01/16.
 */
public final class App extends android.app.Application {

    private static App mInstance = null;

    private AppComponent appComponent;
    private Boolean deviceRooted;

    public static App getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        initialiseFabric();
        FacebookSdk.sdkInitialize(this);

        mInstance = this;

        Apptentive.register(this, "95fa5c8a98975bbbc5fe0b906d7e2d25504475e535db8283105e1415aeff97ac");

        appComponent = DaggerAppComponent.builder().build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());

        checkForRoot();
    }

    private void initialiseFabric() {
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        Twitter twitter = new Twitter(twitterAuthConfig);

        if (!BuildConfig.DEBUG) {
            Crashlytics crashlytics = new Crashlytics();
            Fabric.with(this, crashlytics, twitter);
        } else {
            Fabric.with(this, twitter);
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void checkForRoot() {
        deviceRooted = false;
        if (RootUtils.isDeviceRooted()) {
            deviceRooted = true;
            BinkUtil.resetApplication(this, appComponent.model());
        }
    }

    public boolean isDeviceRooted() {
        if (deviceRooted == null) {
            throw new RuntimeException("App#checkForRoot must be called first");
        }

        return deviceRooted;
    }
}
