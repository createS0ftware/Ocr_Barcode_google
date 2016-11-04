package com.google.android.gms.samples.vision.inner.bink.ui.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.MainActivity;
import com.loyaltyangels.bink.ui.components.BinkVideoView;
import com.loyaltyangels.bink.ui.login.LoginActivity;
import com.loyaltyangels.bink.ui.register.SignUpActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jmcdonnell on 16/08/2016.
 */

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_AUTHORISE = 0;

    @BindView(R.id.video)
    BinkVideoView video;

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.login_layout)
    ViewGroup loginLayout;

    @BindView(R.id.cover)
    View cover;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            setContentView(R.layout.activity_splash_low);
            String path = "android.resource://" + getPackageName() + "/" + R.raw.intro_low_h;

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            /**
             *  0.64f is the ratio of the old video to new (low res) video width
             */
            final float ratioW = metrics.widthPixels / 0.64F;
            int height =  metrics.heightPixels;
            /**
             *  1.5 is the ratio of the new video content width to the full width but it is adjusted for the fact that
             *  the ratio between the videos is not the same as the ratio between screen resolutions so we want the
             *  video slightly wider
             */
            int width= (int) (ratioW * 1.55F);
            video.setVideoAspect(width,height);
            video.setVideoPath(path);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) video.getLayoutParams();
            /**
             *  1.5/0.64 = 2.374375
             *
             *  But we want to adjust for the new ratios and account for slight discrepancies so we
             *  make the margin offset larger
             *
             *  This is a temporary solution until we get a new video
             */
            params.setMargins((int)(-ratioW/2.2),0,0,0);
            video.setLayoutParams(params);
            video.setOnPreparedListener(player -> {
                player.setLooping(true);
                cover.animate().alpha(0).setStartDelay(600).setDuration(400).start();
            });
        } else {

            setContentView(R.layout.activity_splash);
            String path = "android.resource://" + getPackageName() + "/" + R.raw.intro;
            video.setVideoPath(path);
            video.setOnPreparedListener(player -> {
                player.setLooping(true);
                cover.animate().alpha(0).setStartDelay(600).setDuration(400).start();
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Onboarding);
    }

    @Override
    public void onResume() {
        super.onResume();
        video.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        video.stopPlayback();
        video.suspend();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTHORISE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.get_started)
    void onGetStarted() {
        if (isConnected()) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivityForResult(intent, REQUEST_AUTHORISE);
        } else {
            showConnectionError();
        }
    }

    @OnClick(R.id.login)
    void onLogin() {
        if (isConnected()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_AUTHORISE);
        } else {
            showConnectionError();
        }
    }
}
