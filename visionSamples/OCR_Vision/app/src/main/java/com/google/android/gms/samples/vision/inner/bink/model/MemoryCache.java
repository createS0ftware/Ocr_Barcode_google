package com.google.android.gms.samples.vision.inner.bink.model;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.model.common.Order;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.payment.PaymentCard;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.model.scheme.AddSchemePayload;
import com.loyaltyangels.bink.model.scheme.AddSchemeResult;
import com.loyaltyangels.bink.model.scheme.IdentifySchemePayload;
import com.loyaltyangels.bink.model.scheme.IdentifySchemeResult;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.model.scheme.SchemeLinkResponse;
import com.loyaltyangels.bink.model.scheme.Transaction;
import com.loyaltyangels.bink.model.user.ChangePasswordPayload;
import com.loyaltyangels.bink.model.user.User;
import com.loyaltyangels.bink.model.user.settings.Setting;
import com.loyaltyangels.bink.model.user.settings.SettingsUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by jmcdonnell on 27/07/2016.
 * <p>
 * This class stores generic data that is not specific to the user inside a memory cache.
 */

public class MemoryCache implements BinkCache {

    private static final long WALLET_CACHE_TIME = TimeUnit.MINUTES.toMillis(15);
    private static final long USER_CACHE_TIME = TimeUnit.MINUTES.toMillis(15);
    private static final long SCHEMES_CACHE_TIME = TimeUnit.HOURS.toMillis(1);
    private static final long PAYMENT_CARDS_CACHE_TIME = TimeUnit.HOURS.toMillis(1);

    private Wallet wallet;
    private ArrayList<Scheme> schemes;
    private ArrayList<PaymentCard> paymentCards;
    private ApiConfig apiConfig;
    private User user;

    public MemoryCache(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Override
    public Observable<User> getUser() {
        return Observable.just(user);
    }

    @Override
    public Observable<Wallet> getWallet() {
        if (!isWalletExpired()) {
            return Observable.just(wallet);
        } else {
            clearWallet();
            return Observable.empty();
        }
    }

    @Override
    public Observable<Balance> getBalanceForSchemeAccount(@NonNull SchemeAccount schemeAccount) {
        return Observable.empty();
    }

    @Override
    public Observable<ArrayList<Transaction>> getTransactionsForSchemeAccount(@NonNull String schemeAccountId) {
        return Observable.empty();
    }

    @Override
    public Observable<ArrayList<Scheme>> getSchemes() {
        if (schemes != null) {
            return Observable.just(schemes);
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Observable<ArrayList<PaymentCard>> getPaymentCards() {
        if (paymentCards != null) {
            return Observable.just(paymentCards);
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Observable<ArrayList<Setting>> getUserSettings() {
        return Observable.empty();
    }

    @Override
    public boolean isWalletExpired() {
        return wallet == null || (System.currentTimeMillis() > apiConfig.getWalletExpiryDate());
    }

    @Override
    public boolean areSchemesExpired() {
        return schemes == null || (System.currentTimeMillis() > apiConfig.getSchemesExpiryDate());
    }

    @Override
    public boolean arePaymentCardsExpired() {
        return paymentCards == null || (System.currentTimeMillis() > apiConfig.getPaymentCardsExpiryDate());
    }

    @Override
    public boolean isUserExpired() {
        return user == null || (System.currentTimeMillis() > apiConfig.getUserExpiryDate());
    }

    @Override
    public void clearWallet() {
        wallet = null;
        apiConfig.setWalletExpiryDate(0);
        apiConfig.save();
    }

    @Override
    public void clearSchemes() {
        schemes = null;
        apiConfig.setSchemesExpiryDate(0);
        apiConfig.save();
    }

    @Override
    public void clearPaymentCards() {
        paymentCards = null;
        apiConfig.setPaymentCardsExpiryDate(0);
        apiConfig.save();
    }

    @Override
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
        apiConfig.setWalletExpiryDate(System.currentTimeMillis() + WALLET_CACHE_TIME);
        apiConfig.save();
    }

    @Override
    public void setSchemes(ArrayList<Scheme> schemes) {
        this.schemes = schemes;
        apiConfig.setSchemesExpiryDate(System.currentTimeMillis() + SCHEMES_CACHE_TIME);
        apiConfig.save();
    }

    @Override
    public void setPaymentCards(ArrayList<PaymentCard> paymentCards) {
        this.paymentCards = paymentCards;
        apiConfig.setPaymentCardsExpiryDate(System.currentTimeMillis() + PAYMENT_CARDS_CACHE_TIME);
        apiConfig.save();
    }

    @Override
    public void setUser(User user) {
        this.user = user;
        apiConfig.setUserExpiryDate(System.currentTimeMillis() + USER_CACHE_TIME);
        apiConfig.save();
    }

    @Override
    public Observable<JsonObject> changePassword(ChangePasswordPayload payload) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<SchemeLinkResponse> linkScheme(String id, HashMap<String, String> credentials) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<User> updateUser(User user) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<IdentifySchemeResult> identifySchemes(IdentifySchemePayload payload) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<SettingsUpdate> updateSettings(@NonNull SettingsUpdate settingsUpdate) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<PromoValidation> validatePromoCode(@NonNull String promoCode) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<Authorisation> login(String email, String password) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<Authorisation> authoriseWithFacebook(String userId, String accessToken) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<Authorisation> authoriseWithTwitter(String accessToken, String accessTokenSecret) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<Authorisation> register(Registration registration) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<String> forgottenPassword(@NonNull String email) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<SchemeAccount> deleteSchemeAccount(String id) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<SchemeAccount> getSchemeAccount(String id) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<PaymentCardAccount> deletePaymentCard(String id) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<PaymentCardAccount> getPaymentCardAccount(String id) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<PaymentCardAccount> addPaymentCardAccount(PaymentCardAccount account) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<AddSchemeResult> addSchemeAccount(AddSchemePayload schemeAccount) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public Observable<ArrayList<Order>> updateOrders(ArrayList<Order> orders) {
        throw new UnsupportedOperationException("Method not cacheable");
    }

    @Override
    public void clear() {
        clearWallet();
        clearSchemes();
        clearPaymentCards();
    }
}
