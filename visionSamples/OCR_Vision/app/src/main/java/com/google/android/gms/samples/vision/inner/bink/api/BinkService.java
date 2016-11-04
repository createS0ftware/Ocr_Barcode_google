package com.google.android.gms.samples.vision.inner.bink.api;

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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by jm on 12/07/16.
 * <p>
 * This class defines all the endpoints for the Bink API.
 * <p>
 * It is not responsible for the request
 * authorisation headers (see {@link ApiAuthenticationInterceptor}).
 */

public interface BinkService {

    @FormUrlEncoded
    @POST("/users/login")
    Call<Authorisation> login(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("/users/auth/facebook")
    Call<Authorisation> authoriseWithFacebook(
            @Field("user_id") String userId,
            @Field("access_token") String accessToken);

    @FormUrlEncoded
    @POST("/users/auth/twitter")
    Call<Authorisation> authoriseWithTwitter(
            @Field("access_token") String accessToken,
            @Field("access_token_secret") String accessTokenSecret);

    @POST("/users/register")
    Call<Authorisation> register(@Body Registration registration);

    @PUT("/users/me/password")
    Call<JsonObject> changePassword(@Body ChangePasswordPayload payload);

    @FormUrlEncoded
    @POST("/users/promo_code")
    Call<PromoValidation> validatePromoCode(@Field("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("/users/forgotten_password")
    Call<String> forgottenPassword(@Field("email") String email);

    @GET("/users/me")
    Call<User> getUser();

    @PUT("/users/me")
    Call<User> updateUser(@Body User user);

    @PUT("/users/me/settings")
    Call<SettingsUpdate> updateSettings(@Body SettingsUpdate settingsUpdate);

    @GET("/users/me/settings")
    Call<ArrayList<Setting>> getUserSettings();

    @GET("/scheme_accounts")
    Call<ArrayList<SchemeAccount>> getSchemeAccounts();

    @GET("/scheme_accounts/{id}/balances")
    Call<Balance> getBalanceForScheme(@Path("id") String schemeAccountId);

    @GET("/scheme_accounts/{id}/transactions")
    Call<ArrayList<Transaction>> getTransactionsForScheme(@Path("id") String schemeAccountId);

    @POST("/schemes/accounts/{id}/link")
    Call<SchemeLinkResponse> linkScheme(
            @Path("id") String schemeId,
            @Body HashMap<String, String> credentials);

    @GET("/schemes")
    Call<ArrayList<Scheme>> getSchemes();

    @POST("/schemes/identify")
    Call<IdentifySchemeResult> identifySchemes(@Body IdentifySchemePayload payload);

    @GET("/payment_cards")
    Call<ArrayList<PaymentCard>> getPaymentCards();

    @POST("/payment_cards/accounts")
    Call<PaymentCardAccount> addPaymentCard(@Body PaymentCardAccount paymentCardAccount);

    @POST("/schemes/accounts")
    Call<AddSchemeResult> addSchemeAccount(@Body AddSchemePayload schemeAccount);

    @GET("/scheme_accounts/wallet")
    Call<Wallet> getWallet();

    @DELETE("/schemes/accounts/{id}")
    Call<SchemeAccount> deleteSchemeAccount(@Path("id") String loyaltyCardId);

    @GET("/schemes/accounts/{id}")
    Call<SchemeAccount> getSchemeAccount(@Path("id") String schemeAccountId);

    @DELETE("/payment_cards/accounts/{id}")
    Call<PaymentCardAccount> deletePaymentCard(@Path("id") String paymentCardId);

    @GET("/payment_cards/accounts/{id}")
    Call<PaymentCardAccount> getPaymentCardAccount(@Path("id") String paymentCardAccountId);

    @POST("/order")
    Call<ArrayList<Order>> postOrders(@Body ArrayList<Order> orders);

}
