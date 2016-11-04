package com.google.android.gms.samples.vision.inner.bink.model;

import com.loyaltyangels.bink.BinkDataSource;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.payment.PaymentCard;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.user.User;

import java.util.ArrayList;

/**
 * Created by jmcdonnell on 27/07/2016.
 */

public interface BinkCache extends BinkDataSource {

    void setWallet(Wallet wallet);

    void setSchemes(ArrayList<Scheme> schemes);

    void setPaymentCards(ArrayList<PaymentCard> paymentCards);

    boolean isWalletExpired();

    void clearWallet();

    boolean areSchemesExpired();

    void clearSchemes();

    boolean arePaymentCardsExpired();

    boolean isUserExpired();

    void setUser(User user);

    void clearPaymentCards();

    void clear();

}
