package com.google.android.gms.samples.vision.inner.bink.ui.wallet.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyaltyangels.bink.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hansonaboagye on 03/08/16.
 */
class WalletCardViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_title)
    TextView cardTitle;

    @BindView(R.id.card_bg)
    ImageView cardBg;

    @BindView(R.id.binkShow)
    ImageView binkShow;

    @BindView(R.id.points)
    TextView points;

    @BindView(R.id.card)
    CardView card;

    @BindView(R.id.account)
    TextView account;

    WalletCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}
