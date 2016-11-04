package com.google.android.gms.samples.vision.inner.bink.module;

import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.ui.profile.EditAddressActivity;
import com.loyaltyangels.bink.ui.wallet.fragments.BaseDialogFragment;

import dagger.Component;
import io.card.payment.CardDetectionActivity;

/**
 * Created by hansonaboagye on 05/08/16.
 */
@ActivityScope
@Component(dependencies = AppComponent.class)
public interface ActivityComponent {
    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    void inject(BaseDialogFragment dialogFragment);

    void inject(CardDetectionActivity baseActivity);

    void inject(EditAddressActivity activity);

}
