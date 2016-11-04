package com.google.android.gms.samples.vision.inner.bink;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.user.User;
import com.loyaltyangels.bink.ui.BaseFragment;

import butterknife.OnClick;
import rx.Observable;


/**
 * Created by jmcdonnell on 16/09/16.
 */
public class ShareFragment extends BaseFragment {

    private User user;

    @Override
    protected int getLayoutRes() {
        return R.layout.share_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model.getUser()
                .compose(applySchedulers())
                .subscribe(user -> {
                    this.user = user;
                }, Throwable::printStackTrace);
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Share);
    }

    @OnClick(R.id.share)
    void onShare() {
        Observable.defer(() -> user != null ? Observable.just(user) : model.getUser())
                .compose(applySchedulers())
                .subscribe(user -> {
                    String body = getString(R.string.share_body, getString(R.string.app_store_url), user.getReferralCode());
                    String subject = getString(R.string.share_subject);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);

                    startActivity(Intent.createChooser(intent, null));
                });
    }

}
