package com.google.android.gms.samples.vision.inner.bink.spreedly;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 24/08/2016.
 */

public class PaymentMethod {

    public static PaymentMethod create(String number, String fullName, String month, String year) {
        CreditCard creditCard = new CreditCard();
        creditCard.number = number;
        creditCard.fullName = fullName;
        creditCard.month = month;
        creditCard.year = year;

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.retained = true;
        paymentMethod.creditCard = creditCard;
        return paymentMethod;
    }

    @SerializedName("credit_card")
    CreditCard creditCard;

    @SerializedName("retained")
    Boolean retained;

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public Boolean isRetained() {
        return retained;
    }

    public static class CreditCard {

        @SerializedName("number")
        String number;

        @SerializedName("full_name")
        String fullName;

        @SerializedName("month")
        String month;

        @SerializedName("year")
        String year;

        public String getNumber() {
            return number;
        }

        public String getFullName() {
            return fullName;
        }

        public String getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }
    }


}
