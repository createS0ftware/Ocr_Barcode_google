package com.google.android.gms.samples.vision.inner.bink.api.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jm on 19/07/16.
 */

public class PreferencesApiConfig implements ApiConfig {

    private static final String PREF_USER_TOKEN = "user_token";
    private static final String PREF_USER_EMAIL = "user_email";
    private static final String PREF_WALLET_EXPIRY_DATE = "wallet_expiry_date";
    private static final String PREF_SCHEMES_EXPIRY_DATE = "schemes_expiry_date";
    private static final String PREF_PAYMENT_CARDS_EXPIRY_DATE = "payment_cards_expiry_date";
    private static final String PREF_USER_EXPIRY_DATE = "user_expiry_date";

    private SharedPreferences preferences;

    private String userToken;
    private String userEmail;
    private long walletExpiryDate;
    private long schemesExpiryDate;
    private long paymentCardsExpiryDate;
    private long userExpiryDate;

    public PreferencesApiConfig(String preferencesName, Context context) {
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        loadPreferences();
    }

    private void loadPreferences() {
        userToken = preferences.getString(PREF_USER_TOKEN, null);
        userEmail = preferences.getString(PREF_USER_EMAIL, null);
        walletExpiryDate = preferences.getLong(PREF_WALLET_EXPIRY_DATE, 0);
        schemesExpiryDate = preferences.getLong(PREF_SCHEMES_EXPIRY_DATE, 0);
        paymentCardsExpiryDate = preferences.getLong(PREF_PAYMENT_CARDS_EXPIRY_DATE, 0);
        userExpiryDate = preferences.getLong(PREF_USER_EXPIRY_DATE, 0);
    }

    @Override
    public void save() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_USER_TOKEN, userToken);
        editor.putString(PREF_USER_EMAIL, userEmail);
        editor.putLong(PREF_WALLET_EXPIRY_DATE, walletExpiryDate);
        editor.putLong(PREF_SCHEMES_EXPIRY_DATE, schemesExpiryDate);
        editor.putLong(PREF_PAYMENT_CARDS_EXPIRY_DATE, paymentCardsExpiryDate);
        editor.putLong(PREF_USER_EXPIRY_DATE, userExpiryDate);
        editor.apply();
    }

    @Override
    public String getUserToken() {
        return userToken;
    }

    @Override
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public void setUserEmail(String email) {
        userEmail = email;
    }

    @Override
    public long getWalletExpiryDate() {
        return walletExpiryDate;
    }

    @Override
    public void setWalletExpiryDate(long expiryDate) {
        this.walletExpiryDate = expiryDate;
    }

    @Override
    public long getSchemesExpiryDate() {
        return schemesExpiryDate;
    }

    @Override
    public void setSchemesExpiryDate(long expiryDate) {
        schemesExpiryDate = expiryDate;
    }

    @Override
    public long getPaymentCardsExpiryDate() {
        return paymentCardsExpiryDate;
    }

    @Override
    public void setPaymentCardsExpiryDate(long expiryDate) {
        paymentCardsExpiryDate = expiryDate;
    }

    @Override
    public long getUserExpiryDate() {
        return userExpiryDate;
    }

    @Override
    public void setUserExpiryDate(long userExpiryDate) {
        this.userExpiryDate = userExpiryDate;
    }

    @Override
    public void clear() {
        preferences.edit()
                .clear()
                .apply();

        loadPreferences();
    }
}
