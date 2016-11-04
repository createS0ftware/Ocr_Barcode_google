package com.google.android.gms.samples.vision.inner.bink.ui.card_detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.ui.wallet.fragments.CustomDialogFragment;

/**
 * Created by hansonaboagye on 19/08/16.
 */
public class UseTill_InfoDialogFragment extends CustomDialogFragment {


    @Override
    protected int getLayoutRes() {
        return R.layout.help_popup;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton closeButton = (ImageButton) view.findViewById(R.id.help_close);
        closeButton.setOnClickListener(view1 -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.HelpBarcodeDialog);
    }
}
