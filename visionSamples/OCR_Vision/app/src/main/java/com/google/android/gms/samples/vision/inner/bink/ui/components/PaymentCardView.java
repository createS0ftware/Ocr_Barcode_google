package com.google.android.gms.samples.vision.inner.bink.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.util.UiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jmcdonnell on 19/08/2016.
 */

public class PaymentCardView extends FrameLayout {

    @BindView(R.id.image)
    ImageView cardImage;

    @BindView(R.id.number)
    TextView number;

    public PaymentCardView(Context context) {
        super(context);
        init();
    }

    public PaymentCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ButterKnife.bind(this, inflate(getContext(), R.layout.layout_payment_card, this));
    }

    public void setPaymentCard(PaymentCardAccount paymentCardAccount) {
        number.setText("•••• " + paymentCardAccount.getPanEnd());

        String image = paymentCardAccount.findImage(ImageType.HERO).getImageUrl();

        Glide.with(getContext())
                .load(image)
                .placeholder(UiUtil.getPaymentCardBackground(paymentCardAccount.getPaymentCardType()))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(cardImage);
    }
}
