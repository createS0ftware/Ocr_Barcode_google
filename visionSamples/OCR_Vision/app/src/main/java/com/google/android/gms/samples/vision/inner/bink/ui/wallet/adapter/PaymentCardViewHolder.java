package com.google.android.gms.samples.vision.inner.bink.ui.wallet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.ui.components.PaymentCardView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jmcdonnell on 19/08/2016.
 */

public class PaymentCardViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.payment_card)
    PaymentCardView paymentCardView;

    public PaymentCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
