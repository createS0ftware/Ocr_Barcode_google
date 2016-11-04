package com.google.android.gms.samples.vision.inner.bink.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.loyaltyangels.bink.BinkDataSource;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by jm on 12/07/16.
 * <p>
 * This class is responsible for all interactions with the Bink API.
 * <p> Features include:
 * <br>- Certificate pinning
 * <br>- Error handling
 * <br>- Request creation (using {@link BinkService})
 * <br>- Exposing the endpoints and relative model objects through {@link Observable} objects
 */

public class ApiClient implements BinkDataSource {

    private static final String TAG = ApiClient.class.getSimpleName();

    private BinkService binkService;

    /**
     * Used for parsing the error response
     */
    private Gson gson;

    public ApiClient(@NonNull BinkService binkService) {
        this.binkService = binkService;
        gson = new Gson();
    }

    @Override
    public Observable<Authorisation> login(@NonNull String email, @NonNull String password) {
        return makeApiRequest(binkService.login(email, password));
    }

    @Override
    public Observable<Authorisation> authoriseWithFacebook(String userId, String accessToken) {
        return makeApiRequest(binkService.authoriseWithFacebook(userId, accessToken));
    }

    @Override
    public Observable<Authorisation> authoriseWithTwitter(String accessToken, String accessTokenSecret) {
        return makeApiRequest(binkService.authoriseWithTwitter(accessToken, accessTokenSecret));
    }

    @Override
    public Observable<Authorisation> register(@NonNull Registration registration) {
        return makeApiRequest(binkService.register(registration));
    }

    @Override
    public Observable<JsonObject> changePassword(ChangePasswordPayload payload) {
        return makeApiRequest(binkService.changePassword(payload));
    }

    @Override
    public Observable<String> forgottenPassword(@NonNull String email) {
        return makeApiRequest(binkService.forgottenPassword(email));
    }

    @Override
    public Observable<PromoValidation> validatePromoCode(@NonNull String promoCode) {
        return makeApiRequest(binkService.validatePromoCode(promoCode));
    }

    @Override
    public Observable<SettingsUpdate> updateSettings(@NonNull SettingsUpdate settingsUpdate) {
        return makeApiRequest(binkService.updateSettings(settingsUpdate));
    }

    @Override
    public Observable<ArrayList<Setting>> getUserSettings() {
        return makeApiRequest(binkService.getUserSettings());
    }

    @Override
    public Observable<User> getUser() {
        return makeApiRequest(binkService.getUser());
    }

    @Override
    public Observable<User> updateUser(User user) {
        return makeApiRequest(binkService.updateUser(user));
    }

    @Override
    public Observable<Wallet> getWallet() {
        return makeApiRequest(binkService.getWallet());
    }

    @Override
    public Observable<Balance> getBalanceForSchemeAccount(@NonNull SchemeAccount schemeAccount) {
        return makeApiRequest(binkService.getBalanceForScheme(schemeAccount.getId()));
    }

    @Override
    public Observable<ArrayList<Transaction>> getTransactionsForSchemeAccount(@NonNull String schemeAccountId) {
        return makeApiRequest(binkService.getTransactionsForScheme(schemeAccountId));
    }

    @Override
    public Observable<SchemeLinkResponse> linkScheme(String id, HashMap<String, String> credentials) {
        return makeApiRequest(binkService.linkScheme(id, credentials));
    }

    @Override
    public Observable<ArrayList<Scheme>> getSchemes() {
        return makeApiRequest(binkService.getSchemes());
    }

    @Override
    public Observable<IdentifySchemeResult> identifySchemes(IdentifySchemePayload payload) {
        return makeApiRequest(binkService.identifySchemes(payload));
    }

    @Override
    public Observable<ArrayList<PaymentCard>> getPaymentCards() {
        return makeApiRequest(binkService.getPaymentCards());
    }

    @Override
    public Observable<PaymentCardAccount> addPaymentCardAccount(PaymentCardAccount account) {
        return makeApiRequest(binkService.addPaymentCard(account));
    }

    @Override
    public Observable<AddSchemeResult> addSchemeAccount(AddSchemePayload schemeAccount) {
        return makeApiRequest(binkService.addSchemeAccount(schemeAccount));
    }

    @Override
    public Observable<SchemeAccount> deleteSchemeAccount(String id) {
        return makeApiRequest(binkService.deleteSchemeAccount(id));
    }

    @Override
    public Observable<SchemeAccount> getSchemeAccount(String id) {
        return makeApiRequest(binkService.getSchemeAccount(id));
    }

    @Override
    public Observable<PaymentCardAccount> deletePaymentCard(String id) {
        return makeApiRequest(binkService.deletePaymentCard(id));
    }

    @Override
    public Observable<PaymentCardAccount> getPaymentCardAccount(String id) {
        return makeApiRequest(binkService.getPaymentCardAccount(id));
    }

    @Override
    public Observable<ArrayList<Order>> updateOrders(ArrayList<Order> orders) {
        return makeApiRequest(binkService.postOrders(orders));
    }

    private <T> Observable<T> makeApiRequest(@NonNull Call<T> call) {
        return Observable.create(subscriber -> {
            try {

                Response<T> response = call.execute();

                if (response.isSuccessful()) {
                    subscriber.onNext(response.body());
                } else {
                    Log.w(TAG, "Unsuccessful HTTP response (code " + response.code() + ")");

                    ApiException exception = new ApiException(response.message());

                    try {
                        ResponseBody errorBody = response.errorBody();

                        if (errorBody != null && errorBody.contentLength() > 0) {
                            JsonObject error = gson.fromJson(errorBody.string(), JsonObject.class);
                            exception.setErrorResponse(error);
                        }
                    } catch (IOException | JsonParseException e) {
                        Log.e(TAG, "Could not parse error body");
                        e.printStackTrace();
                    }

                    subscriber.onError(exception);
                }
            } catch (Exception e) {
                if (e instanceof IOException) {
                    // TODO Handle network / IOException
                    Log.w(TAG, "Network request failed with unexpected exception");
                }

                subscriber.onError(e);
            }

            subscriber.onCompleted();
        });
    }


}
