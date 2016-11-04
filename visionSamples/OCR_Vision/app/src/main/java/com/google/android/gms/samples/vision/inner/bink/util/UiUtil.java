package com.google.android.gms.samples.vision.inner.bink.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.config.AppConfig;
import com.loyaltyangels.bink.model.Balance;
import com.loyaltyangels.bink.model.payment.PaymentCardType;
import com.loyaltyangels.bink.model.scheme.QuestionType;
import com.loyaltyangels.bink.model.scheme.Scheme;

/**
 * Created by jmcdonnell on 22/07/2016.
 */
public class UiUtil {

    @DrawableRes
    public static int getPaymentCardBackground(@NonNull PaymentCardType paymentCardType) {
        switch (paymentCardType) {
            default:
            case VISA:
                return R.drawable.visa_background_placeholder;
            case MASTERCARD:
                return R.drawable.mastercard_background_placeholder;
            case AMEX:
                return R.drawable.amex_background_placeholder;
        }
    }

    public static int getColorForSchemeLabel(@NonNull Context context, @NonNull Scheme scheme) {
        final int brightnessThreshold = 130;

        int lighterGrey = ContextCompat.getColor(context, R.color.lighterGrey);
        int darkerGrey = ContextCompat.getColor(context, R.color.darkerGrey);

        int colour = Color.parseColor(scheme.getColour());

        int r = Color.red(colour);
        int g = Color.green(colour);
        int b = Color.blue(colour);

        /**
         * The code below checks if a given colour is light or dark based on its RGB components.
         * http://www.nbdtech.com/Blog/archive/2008/04/27/Calculating-the-Perceived-Brightness-of-a-Color.aspx
         */
        double brightness = Math.sqrt(
                r * r * .241 +
                        g * g * .691 +
                        b * b * .068
        );

        if (brightness > brightnessThreshold) {
            return darkerGrey;
        } else {
            return lighterGrey;
        }
    }

    @Nullable
    public static String formatSchemePointsLabel(@NonNull AppConfig appConfig, @NonNull Balance balance, @NonNull Scheme scheme) {
        if (appConfig.shouldShowMonetaryValue() && !TextUtils.isEmpty(balance.getValueLabel())) {
            return balance.getValueLabel();
        } else if (!TextUtils.isEmpty(balance.getPointsLabel())) {
            return formatPoints(balance, scheme);
        }
        return null;
    }

    public static String formatPoints(@NonNull Balance balance, @NonNull Scheme scheme) {
        return String.format("%s %s", balance.getPointsLabel(), scheme.getPointName());
    }

    public static int getInputTypeForQuestionType(@NonNull QuestionType type) {
        switch (type) {
            case Username:
            case PlaceOfBirth:
            case LastName:
            default:
                return InputType.TYPE_CLASS_TEXT;
            case Email:
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            case CardNumber:
            case Barcode:
                return InputType.TYPE_CLASS_NUMBER;
            case Password:
            case MemorableDate:
            case DateOfBirth:
            case FavouritePlace:
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            case Postcode:
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
            case Pin:
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        }
    }
}
