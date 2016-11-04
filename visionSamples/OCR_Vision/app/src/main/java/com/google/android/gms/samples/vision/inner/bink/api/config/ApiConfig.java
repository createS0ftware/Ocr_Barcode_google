package com.google.android.gms.samples.vision.inner.bink.api.config;

/**
 * Created by jm on 19/07/16.
 */

public interface ApiConfig {

    String getUserToken();

    String getUserEmail();

    void setUserToken(String userToken);

    void setUserEmail(String email);

    void save();

    long getWalletExpiryDate();

    void setWalletExpiryDate(long expiryDate);

    long getSchemesExpiryDate();

    void setSchemesExpiryDate(long expiryDate);

    long getPaymentCardsExpiryDate();

    void setPaymentCardsExpiryDate(long expiryDate);

    void setUserExpiryDate(long expiryDate);

    long getUserExpiryDate();

    void clear();

}
