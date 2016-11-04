package com.google.android.gms.samples.vision.inner.bink.model;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.gson.JsonObject;
import com.loyaltyangels.bink.BinkDataSource;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.model.common.Account;
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
import com.loyaltyangels.bink.util.BinkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Completable;
import rx.Observable;

/**
 * Created by jmcdonnell on 27/07/2016.
 * <p>
 * A proxy between the UI and the API / Cache
 */

public class Model implements BinkDataSource {

    private BinkDataSource api;
    private BinkCache cache;
    private ApiConfig apiConfig;
    private ModelListener listener;

    public Model(BinkDataSource api, BinkCache cache, ApiConfig apiConfig, ModelListener listener) {
        this.api = api;
        this.cache = cache;
        this.apiConfig = apiConfig;
        this.listener = listener;
    }

    @Override
    public Observable<Authorisation> login(String email, String password) {
        return api.login(email, password)
                .doOnNext(this::handleAuthorisation);
    }

    @Override
    public Observable<Authorisation> authoriseWithFacebook(String userId, String accessToken) {
        return api.authoriseWithFacebook(userId, accessToken)
                .doOnNext(this::handleAuthorisation);
    }

    @Override
    public Observable<Authorisation> authoriseWithTwitter(String accessToken, String accessTokenSecret) {
        return api.authoriseWithTwitter(accessToken, accessTokenSecret)
                .doOnNext(this::handleAuthorisation);
    }

    @Override
    public Observable<Authorisation> register(@NonNull Registration registration) {
        return api.register(registration)
                .doOnNext(this::handleAuthorisation);
    }

    @Override
    public Observable<JsonObject> changePassword(ChangePasswordPayload payload) {
        return api.changePassword(payload);
    }

    @Override
    public Observable<String> forgottenPassword(@NonNull String email) {
        return api.forgottenPassword(email);
    }

    @Override
    public Observable<PromoValidation> validatePromoCode(@NonNull String promoCode) {
        return api.validatePromoCode(promoCode);
    }

    @Override
    public Observable<SettingsUpdate> updateSettings(@NonNull SettingsUpdate settingsUpdate) {
        return api.updateSettings(settingsUpdate);
    }

    @Override
    public Observable<ArrayList<Setting>> getUserSettings() {
        return api.getUserSettings();
    }

    @Override
    public Observable<User> getUser() {
        if (cache.isUserExpired()) {
            return api.getUser();
        } else {
            return cache.getUser();
        }
    }

    @Override
    public Observable<User> updateUser(User user) {
        return api.updateUser(user)
                .doOnNext(cache::setUser);
    }

    @Override
    public Observable<Wallet> getWallet() {
        if (!cache.isWalletExpired()) {
            return cache.getWallet();
        } else {
            return loadWalletFromApi();
        }
    }

    public Observable<Wallet> refreshWallet() {
        return loadWalletFromApi();

    }

    private Observable<Wallet> loadWalletFromApi() {
        return api.getWallet()
                .doOnNext(wallet -> {
                    cache.setWallet(wallet);
                    listener.onWalletUpdated();
                });
    }

    @Override
    public Observable<Balance> getBalanceForSchemeAccount(@NonNull SchemeAccount schemeAccount) {
        return api.getBalanceForSchemeAccount(schemeAccount);
    }

    @Override
    public Observable<ArrayList<Transaction>> getTransactionsForSchemeAccount(@NonNull String schemeAccountId) {
        return api.getTransactionsForSchemeAccount(schemeAccountId);
    }

    @Override
    public Observable<SchemeLinkResponse> linkScheme(String id, HashMap<String, String> credentials) {
        return api.linkScheme(id, credentials);
    }

    @Override
    public Observable<ArrayList<Scheme>> getSchemes() {
        if (cache.areSchemesExpired()) {
            return api.getSchemes()
                    .doOnNext(cache::setSchemes);
        } else {
            return cache.getSchemes();
        }
    }

    @Override
    public Observable<IdentifySchemeResult> identifySchemes(IdentifySchemePayload payload) {
        return api.identifySchemes(payload);
    }

    @Override
    public Observable<ArrayList<PaymentCard>> getPaymentCards() {
        if (cache.arePaymentCardsExpired()) {
            return api.getPaymentCards()
                    .doOnNext(cache::setPaymentCards);
        } else {
            return cache.getPaymentCards();
        }
    }

    public Observable<SchemeAccount> deleteSchemeAccount(String id) {
        return getWallet()
                .flatMap(wallet ->
                        api.deleteSchemeAccount(id)
                                .doOnNext(account -> {
                                    wallet.deleteSchemeAccount(id);
                                    cache.setWallet(wallet);
                                }));
    }

    public Observable<SchemeAccount> getSchemeAccount(String id) {
        return api.getSchemeAccount(id);

    }

    public Observable<PaymentCardAccount> deletePaymentCard(String id) {
        return getWallet()
                .flatMap(wallet ->
                        api.deletePaymentCard(id)
                                .doOnNext(account -> {
                                    wallet.deletePaymentCard(id);
                                    cache.setWallet(wallet);
                                }));
    }

    @Override
    public Observable<PaymentCardAccount> getPaymentCardAccount(String id) {
        return api.getPaymentCardAccount(id);
    }

    @Override
    public Observable<PaymentCardAccount> addPaymentCardAccount(PaymentCardAccount account) {
        return getWallet()
                .flatMap(wallet -> {
                    account.setOrder(wallet.getWalletSize() + 1);
                    return api.addPaymentCardAccount(account)
                            .doOnNext(paymentCard -> {
                                wallet.addPaymentCard(paymentCard);
                                cache.setWallet(wallet);
                            });
                });
    }

    public Observable<SchemeAccount> addSchemeAccountToModel(AddSchemePayload payload) {
        return getWallet()
                .flatMap(wallet -> {
                    payload.setOrder(wallet.getWalletSize() + 1);

                    for (SchemeAccount account : wallet.getSchemeAccounts()) {
                        Scheme accountScheme = account.getScheme();
                        if (accountScheme.getId().equals(payload.getSchemeId()) && account.getStatus() == SchemeAccount.Status.JOIN) {
                            payload.setOrder(account.getOrder());
                        }
                    }

                    return addSchemeAccount(payload)
                            .flatMap(result ->
                                    getSchemeAccount(result.getId())
                                            .doOnNext(schemeAccount -> {
                                                wallet.addSchemeAccount(schemeAccount);
                                                cache.setWallet(wallet);
                                            }));
                });
    }

    @Override
    public Observable<AddSchemeResult> addSchemeAccount(AddSchemePayload payload) {
        return api.addSchemeAccount(payload);
    }

    public Observable<ArrayList<Order>> updateOrdersFromAccounts(List<Account> accounts) {
        ArrayList<Order> orders = new ArrayList<>();

        for (Account account : accounts) {
            orders.add(Order.fromAccount(account));
        }

        return updateOrders(orders);
    }

    public Completable updateStaleSchemeAccounts() {
        return cache.getWallet()
                .flatMapIterable(Wallet::getSchemeAccounts)
                .filter(BinkUtil::hasStaleBalance)
                .flatMap(this::getBalanceForSchemeAccount)
                .collect(ArrayList<Balance>::new, ArrayList<Balance>::add)
                .zipWith(getWallet(), Pair::create)
                .doOnNext(result -> {
                    ArrayList<Balance> balances = result.first;
                    Wallet wallet = result.second;

                    for (SchemeAccount account : wallet.getSchemeAccounts()) {
                        for (Balance balance : balances) {
                            if (account.getId().equals(balance.getSchemeAccountId())) {
                                account.setBalance(balance);
                            }
                        }
                    }

                    cache.setWallet(wallet);
                })
                .toCompletable();
    }

    @Override
    public Observable<ArrayList<Order>> updateOrders(ArrayList<Order> orders) {
        return Observable.zip(getWallet(), api.updateOrders(orders), (wallet, newOrders) -> {
            for (Order order : newOrders) {
                for (Account account : wallet.getSchemeAccounts()) {
                    if (account.getId().equals(order.getId())) {
                        account.setOrder(order.getOrder());
                    }
                }

                for (Account account : wallet.getPaymentCardAccounts()) {
                    if (account.getId().equals(order.getId())) {
                        account.setOrder(order.getOrder());
                    }
                }
            }

            cache.setWallet(wallet);
            return newOrders;
        });
    }

    public void clear() {
        cache.clear();
        apiConfig.clear();
    }

    private void handleAuthorisation(Authorisation authorisation) {
        apiConfig.setUserToken(authorisation.getApiKey());
        apiConfig.setUserEmail(authorisation.getEmail());
        apiConfig.save();
    }

}
