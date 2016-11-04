package com.google.android.gms.samples.vision.inner.bink.ui.wallet.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.loyaltyangels.bink.R;

/**
 * Created by jmcdonnell on 28/07/2016.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private static final String TAG = SimpleItemTouchHelperCallback.class.getSimpleName();

    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 1200;

    private ItemTouchHelperAdapter adapter;
    private View draggingView;
    private int cachedMaxScrollSpeed = -1;
    private boolean animateNextDrop;

    /**
     * Copied from android.support.v7.widget.helper.ItemTouchHelper.Callback
     */
    private static final Interpolator dragViewScrollCapInterpolator = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition(), viewHolder, target);
        return true;
    }

    @Override
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
        final int maxScroll = getMaxDragScroll(recyclerView);
        final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        // might be negative if other direction
        float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
        final int cappedScroll = (int) (direction * maxScroll *
                dragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
        final float timeRatio;
        if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
            timeRatio = 1f;
        } else {
            timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
        }
        final int value = (int) (cappedScroll * dragViewScrollCapInterpolator
                .getInterpolation(timeRatio));
        if (value == 0) {
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
        return value;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        /**
         * By reducing the move threshold from default .5f to 0, swapping cards becomes easier.
         * This is due to the overlapping nature of the cards in the wallet.
         */
        return 0;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (draggingView != null) {
            draggingView.animate().cancel();
            draggingView.clearAnimation();
            draggingView.setScaleX(1);
            draggingView.setScaleY(1);
            draggingView = null;
            recyclerView.getItemAnimator().dispatchAnimationFinished(viewHolder);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG) { // Start dragging card
            draggingView = viewHolder.itemView;
            animateNextDrop = true;
            Log.i(TAG, "Dragging View");
            draggingView.animate()
                    .scaleX(0.93f)
                    .scaleY(0.93f)
                    .setDuration(200)
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Log.w(TAG, "Drag animation cancelled");
                            /**
                             * In some cases, when the view is dragged very quickly, the ItemAnimator will reset the itemView's
                             * animation, causing the subsequent 'drop' animation to not start correctly.
                             *
                             * This was the cause of views occasionally being stuck in their shrunk state.
                             *
                             * This flag is a workaround for this issue. In the future we could animate different views
                             * instead of the itemView itself so that it does not interfere with the ItemAnimator
                             */
                            animateNextDrop = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            draggingView.animate().setListener(null);
                        }
                    })
                    .start();
        } else { // Drop card
            if (draggingView != null) {
                if (animateNextDrop) {
                    draggingView.animate()
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(200)
                            .setInterpolator(new DecelerateInterpolator())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    super.onAnimationCancel(animation);
                                    Log.w(TAG, "Drop animation cancelled");
                                    draggingView.setScaleX(1);
                                    draggingView.setScaleY(1);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    draggingView.animate().setListener(null);
                                }
                            })
                            .start();
                } else {
                    Log.w(TAG, "Dropping View without animation");
                    draggingView.setScaleX(1);
                    draggingView.setScaleY(1);
                }
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    private int getMaxDragScroll(RecyclerView recyclerView) {
        if (cachedMaxScrollSpeed == -1) {
            cachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(R.dimen.touch_helper_max_drag_scroll_per_frame);
        }
        return cachedMaxScrollSpeed;
    }

}
