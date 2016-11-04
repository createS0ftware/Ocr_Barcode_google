package com.google.android.gms.samples.vision.inner.bink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import io.card.payment.CardDetectionActivity;
import io.card.payment.i18n.LocalizedStrings;
import io.card.payment.i18n.StringKey;

/**
 * Created by bb on 05/08/16.
 */
public class ScanCardIntents {


    public  static final int GET_LOYALTY_CARD = 122;
    public  static final int GET_PAYMENT_CARD = 121;

    public static Intent scanPaymentCard(Context context) {
        Intent scanIntent = new Intent(context, CardDetectionActivity.class);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_KEEP_APPLICATION_THEME, true); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_USE_CARDIO_LOGO, false);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_GUIDE_COLOR, Color.CYAN);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_HIDE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_SCAN_INSTRUCTIONS, LocalizedStrings.getString(StringKey.SCAN_GUIDE));
        scanIntent.putExtra(CardDetectionActivity.EXTRA_SUPPRESS_CONFIRMATION, true);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_RETURN_CARD_IMAGE, true);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_PAYMENT_CARD, true);
        return scanIntent;
    }

    public static Intent scanLoyaltyCard(Context context) {
        Intent scanIntent = new Intent(context, CardDetectionActivity.class);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_KEEP_APPLICATION_THEME, true); // default: false
        scanIntent.putExtra(CardDetectionActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_USE_CARDIO_LOGO, false);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_GUIDE_COLOR, Color.CYAN);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_HIDE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_SCAN_INSTRUCTIONS, context.getString(R.string.card_detection_scan_instruction));
        scanIntent.putExtra(CardDetectionActivity.EXTRA_SUPPRESS_CONFIRMATION, true);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_RETURN_CARD_IMAGE, true);
        scanIntent.putExtra(CardDetectionActivity.EXTRA_PAYMENT_CARD, false);
        return scanIntent;
    }
}
