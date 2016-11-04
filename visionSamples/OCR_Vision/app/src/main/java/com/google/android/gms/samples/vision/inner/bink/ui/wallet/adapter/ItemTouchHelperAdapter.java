package com.google.android.gms.samples.vision.inner.bink.ui.wallet.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by jmcdonnell on 28/07/2016.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition, RecyclerView.ViewHolder fromHolder, RecyclerView.ViewHolder targetHolder);

    boolean isLongPressDragEnabled();

}
