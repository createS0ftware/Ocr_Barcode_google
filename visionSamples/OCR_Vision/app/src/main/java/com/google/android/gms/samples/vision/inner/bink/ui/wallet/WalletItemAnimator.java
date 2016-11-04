package com.google.android.gms.samples.vision.inner.bink.ui.wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;

/**
 * Created by jmcdonnell on 07/10/2016.
 */

public class WalletItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(0);
        dispatchAnimationFinished(holder);
        holder.itemView.setAlpha(1);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(.7f);
        holder.itemView.setScaleY(.7f);
        holder.itemView.setAlpha(0);
        holder.itemView.animate()
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(getAddDuration())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dispatchAnimationStarted(holder);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        dispatchAnimationFinished(holder);
                    }
                });

        return true;
    }

    @Override
    public long getAddDuration() {
        return 350;
    }

    @Override
    public long getChangeDuration() {
        return 300;
    }

    @Override
    public long getMoveDuration() {
        return 300;
    }

    @Override
    public long getRemoveDuration() {
        return 0;
    }
}
