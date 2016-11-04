package com.google.android.gms.samples.vision.inner.bink;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.loyaltyangels.bink.model.Authorisation;
import com.loyaltyangels.bink.model.Balance;
import com.loyaltyangels.bink.model.PromoValidation;
import com.loyaltyangels.bink.model.Registration;
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

import rx.Observable;

/**
 * Created by jmcdonnell on 27/07/2016.
 */

public interface BinkDataSource {

    Observable<Authorisation> login(String email, String password);

    Observable<Authorisation> authoriseWithFacebook(String userId, String accessToken);

    Observable<Authorisation> authoriseWithTwitter(String accessToken, String accessTokenSecret);

    Observable<Authorisation> register(Registration registration);

    Observable<String> forgottenPassword(@NonNull String email);

    Observable<SettingsUpdate> updateSettings(@NonNull SettingsUpdate settingsUpdate);

    Observable<PromoValidation> validatePromoCode(@NonNull String promoCode);

    Observable<User> getUser();

    Observable<User> updateUser(User user);

    Observable<JsonObject> changePassword(ChangePasswordPayload payload);

    Observable<ArrayList<Setting>> getUserSettings();

    Observable<Wallet> getWallet();

    Observable<Balance> getBalanceForSchemeAccount(@NonNull SchemeAccount schemeAccount);

    Observable<ArrayList<Transaction>> getTransactionsForSchemeAccount(@NonNull String schemeAccountId);

    Observable<SchemeLinkResponse> linkScheme(String id, HashMap<String, String> credentials);

    Observable<ArrayList<Scheme>> getSchemes();

    Observable<IdentifySchemeResult> identifySchemes(IdentifySchemePayload payload);

    Observable<ArrayList<PaymentCard>> getPaymentCards();

    Observable<PaymentCardAccount> addPaymentCardAccount(PaymentCardAccount account);

    Observable<AddSchemeResult> addSchemeAccount(AddSchemePayload payload);

    Observable<SchemeAccount> deleteSchemeAccount(String id);

    Observable<SchemeAccount> getSchemeAccount(String id);

    Observable<PaymentCardAccount> deletePaymentCard(String id);

    Observable<PaymentCardAccount> getPaymentCardAccount(String id);

    Observable<ArrayList<Order>> updateOrders(ArrayList<Order> orders);

}
