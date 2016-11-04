package com.google.android.gms.samples.vision.inner.bink.ui.wallet.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;

public class AddCardsDialogFragment extends CustomDialogFragment {

    public interface Listener {
        void onAddLoyaltyCard();

        void onAddPaymentCard();
    }

    public Listener listener;

    public static AddCardsDialogFragment newInstance() {
        AddCardsDialogFragment fragment = new AddCardsDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_add_cards;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View paymentInfoButton = view.findViewById(R.id.infoButton);
        View layout = view;
        paymentInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPaymentTooltip(layout);
            }
        });
        View loyaltyInfoButton = view.findViewById(R.id.infoButton2);
        loyaltyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoyaltyTooltip(layout);
            }
        });
        View closeButton = view.findViewById(R.id.imageView12);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        View addPaymentCardButton = view.findViewById(R.id.imageView10);
        addPaymentCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAddPaymentCard();
                dismiss();
            }
        });
        View addLoyaltyCardButton = view.findViewById(R.id.imageView11);
        addLoyaltyCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAddLoyaltyCard();
                dismiss();
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void startLoyaltyTooltip(View layout) {
        AddCardInfoDialogFragment.newInstance(
                AddCardInfoDialogFragment.loyaltyImages(),
                AddCardInfoDialogFragment.LOYALTY,
                layout.getHeight())
                .show(getFragmentManager(), "");

        tracker.trackScreen(Screen.HelpLoyaltyDialog);
    }

    private void startPaymentTooltip(View layout) {
        AddCardInfoDialogFragment.newInstance(
                AddCardInfoDialogFragment.paymentImages(),
                AddCardInfoDialogFragment.PAYMENT,
                layout.getHeight())
                .show(getFragmentManager(), "");

        tracker.trackScreen(Screen.HelpPaymentDialog);
    }
}
