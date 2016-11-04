package com.google.android.gms.samples.vision.inner.bink.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loyaltyangels.bink.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jmcdonnell on 03/10/2016.
 */

public class LoginInputLayout extends FrameLayout {

    @BindView(R.id.tick)
    ImageView tick;

    public LoginInputLayout(Context context) {
        super(context);
        init();
    }

    public LoginInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View layout = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_login_input, this, true);

        ButterKnife.bind(this, layout);
    }

    public void showTick() {
        if (tick.getVisibility() != View.VISIBLE) {
            tick.setVisibility(View.VISIBLE);
            tick.setAlpha(0f);
            tick.setScaleX(.3f);
            tick.setScaleY(.3f);
            tick.animate()
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(300)
                    .start();
        }
    }

    public void hideTick() {
        if (tick.getVisibility() == View.VISIBLE) {
            tick.animate()
                    .alpha(0)
                    .scaleX(.3f)
                    .scaleY(.3f)
                    .setDuration(300)
                    .withEndAction(() -> tick.setVisibility(View.INVISIBLE))
                    .start();
        }
    }
}
