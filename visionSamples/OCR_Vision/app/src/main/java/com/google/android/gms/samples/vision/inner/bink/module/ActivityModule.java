package com.google.android.gms.samples.vision.inner.bink.module;
import com.loyaltyangels.bink.ShareFragment;
import com.loyaltyangels.bink.ui.wallet.fragments.WalletFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hansonaboagye on 05/08/16.
 */

@Module
public class ActivityModule {

    @Provides
    @ActivityScope
    WalletFragment provideWalletFragment() { return WalletFragment.newInstance(); }


    @Provides
    @ActivityScope
    ShareFragment provideShareFragment() { return new ShareFragment(); }


}
