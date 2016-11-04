package com.google.android.gms.samples.vision.inner.bink.model;

import android.content.Context;
import android.content.Intent;

import com.loyaltyangels.bink.service.BalanceUpdateService;

/**
 * Created by jmcdonnell on 12/10/2016.
 */

public class AppModelListener implements ModelListener {

    private Context context;

    public AppModelListener(Context context) {
        this.context = context;
    }

    @Override
    public void onWalletUpdated() {
        Intent intent = new Intent(context, BalanceUpdateService.class);
        context.startService(intent);
    }
}
