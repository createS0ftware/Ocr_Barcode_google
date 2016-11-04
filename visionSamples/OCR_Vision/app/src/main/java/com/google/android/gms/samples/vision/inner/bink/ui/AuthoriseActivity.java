package com.google.android.gms.samples.vision.inner.bink.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.Authorisation;
import com.loyaltyangels.bink.model.Registration;
import com.trello.rxlifecycle.ActivityEvent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jmcdonnell on 16/08/2016.
 */

public abstract class AuthoriseActivity extends BaseActivity {

    private static final String TAG = AuthoriseActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private TwitterAuthClient twitterAuthClient;
    private TwitterSession twitterSession;
    private ProgressDialog loadingProgress;
    private Subscription delayedProgressSubscription;

    private Callback<String> twitterEmailCallback = new Callback<String>() {
        @Override
        public void success(Result<String> result) {
            hideProgress();

            if (twitterSession != null) {
                TwitterAuthToken token = twitterSession.getAuthToken();
                authorize(model.authoriseWithTwitter(token.token, token.secret), false);
            } else {
                Log.e(TAG, "TwitterSession must not be NULL here.");
                showErrorMessage(R.string.authorise_twitter_error_title, R.string.authorise_twitter_error);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            hideProgress();
            exception.getMessage();

            new AlertDialog.Builder(AuthoriseActivity.this, R.style.AlertDialogStyle)
                    .setTitle(R.string.authorise_twitter_error_title)
                    .setMessage(exception.getMessage())
                    .setPositiveButton(R.string.alert_ok, null)
                    .show();
        }
    };

    private Callback<TwitterSession> twitterSessionCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            twitterSession = result.data;
            twitterAuthClient.requestEmail(result.data, twitterEmailCallback);
            showProgress(getString(R.string.authorise_logging_in), false);
        }

        @Override
        public void failure(TwitterException exception) {
            exception.printStackTrace();
            new AlertDialog.Builder(AuthoriseActivity.this, R.style.AlertDialogStyle)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.authorise_twitter_error)
                    .setPositiveButton(R.string.alert_ok, null)
                    .show();
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        twitterAuthClient = new TwitterAuthClient();

        /**
         * Logout of Facebook and Twitter
         */
        TwitterCore.getInstance().logOut();
        AccessToken.setCurrentAccessToken(null);
        LoginManager.getInstance().logOut();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> deniedPermissions = loginResult.getRecentlyDeniedPermissions();

                for (String deniedPermission : deniedPermissions) {
                    if ("email".equals(deniedPermission)) {
                        showErrorMessage(R.string.authorise_facebook_error_title,
                                R.string.authorise_facebook_error_email);

                        /**
                         * Don't auth without email permission
                         */
                        return;
                    }
                }

                String userId = loginResult.getAccessToken().getUserId();
                String accessToken = loginResult.getAccessToken().getToken();

                authorize(model.authoriseWithFacebook(userId, accessToken), false);
            }

            @Override
            public void onCancel() {
                new AlertDialog.Builder(AuthoriseActivity.this, R.style.AlertDialogStyle)
                        .setMessage(R.string.authorise_facebook_error_cancelled)
                        .setPositiveButton(R.string.alert_ok, null)
                        .show();
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                new AlertDialog.Builder(AuthoriseActivity.this, R.style.AlertDialogStyle)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.authorise_facebook_error)
                        .setPositiveButton(R.string.alert_ok, null)
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    protected void authoriseWithTwitter() {
        twitterAuthClient.authorize(this, twitterSessionCallback);
    }

    protected void authoriseWithFacebook() {
        List<String> permissions = Arrays.asList("public_profile", "email");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
    }

    protected void authorize(Observable<Authorisation> authorisationObservable, boolean delayProgress) {
        onDisableInputFields();

        showProgress(getString(R.string.authorise_logging_in), false);

        authorisationObservable
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(this::hideProgress)
                .subscribe(authorisation -> {
                    onAuthorised();
                }, error -> {
                    showErrorMessage(R.string.login_error_title, R.string.login_error);
                    error.printStackTrace();
                    onEnableInputFields();
                });
    }

    private void showInvalidPromoCode(Registration registration) {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.sign_up_invalid_promo_title)
                .setMessage(R.string.sign_up_invalid_promo_message)
                .setPositiveButton(R.string.sign_up_invalid_promo_continue, (dialog, which) -> {
                    Registration newRegistration = Registration.create(
                            registration.getEmail(),
                            registration.getPassword(),
                            null);

                    registerFinal(newRegistration, true);
                })
                .setNegativeButton(R.string.sign_up_invalid_promo_try_again, (dialog, which) -> {
                    onEnableInputFields();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    protected void register(Registration registration) {
        onDisableInputFields();

        if (registration.getPromoCode() != null) {
            ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.authorise_creating_account));
            progress.setCancelable(false);

            Subscription delayedProgress = Observable.timer(500, TimeUnit.MILLISECONDS)
                    .compose(applySchedulers())
                    .subscribe(l -> progress.show());

            model.validatePromoCode(registration.getPromoCode())
                    .compose(applySchedulers())
                    .doOnTerminate(() -> {
                        delayedProgress.unsubscribe();
                        progress.dismiss();
                    })
                    .subscribe(promoValidation -> {
                        if (promoValidation.isValid()) {
                            registerFinal(registration, false);
                        } else {
                            showInvalidPromoCode(registration);
                        }
                    }, error -> {
                        showErrorMessage(R.string.sign_up_error_title, R.string.sign_up_error_message);
                        onEnableInputFields();
                    });
        } else {
            registerFinal(registration, true);
        }
    }

    private void showProgress(String message, boolean delayProgress) {
        hideProgress();
        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage(message);
        loadingProgress.setCancelable(false);

        delayedProgressSubscription = Observable.timer(delayProgress ? 500 : 0, TimeUnit.MILLISECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> loadingProgress.show());
    }

    private void hideProgress() {
        if (delayedProgressSubscription != null) {
            delayedProgressSubscription.unsubscribe();
        }

        if (loadingProgress != null) {
            loadingProgress.dismiss();
        }
    }

    private void registerFinal(Registration registration, boolean delayProgress) {
        showProgress(getString(R.string.authorise_creating_account), delayProgress);

        model.register(registration)
                .compose(applySchedulers())
                .doOnTerminate(this::hideProgress)
                .subscribe(authorisation -> {
                    onAuthorised();
                }, error -> {
                    onEnableInputFields();
                    showErrorMessage(R.string.app_name, R.string.sign_up_error_message);
                });
    }

    private void showErrorMessage(@StringRes int title, @StringRes int message) {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_ok, null)
                .show();
    }

    protected abstract void onDisableInputFields();

    protected abstract void onEnableInputFields();

    protected abstract void onAuthorised();

}
