package com.google.android.gms.samples.vision.inner.bink.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.Registration;
import com.loyaltyangels.bink.model.user.settings.SettingsUpdate;
import com.loyaltyangels.bink.service.SettingsUpdateService;
import com.loyaltyangels.bink.ui.AuthoriseActivity;
import com.loyaltyangels.bink.util.TintUtils;

import icepick.Icepick;
import icepick.State;

public class SignUpActivity extends AuthoriseActivity {

    private static final String SIGN_UP_TAG = "sign_up";
    private static final String PASSWORD_TAG = "sign_up_password";


    @State
    String emailAddress;

    @State
    SettingsUpdate userSettingsUpdate;

    @State
    String password;

    @State
    String referralCode;

    private SignUpPasswordFragment passwordFragment;

    private SignUpFragment.Listener signUpListener = new SignUpFragment.Listener() {
        @Override
        public void onDetailsEntered(String email, SettingsUpdate settingsUpdate) {
            emailAddress = email;
            userSettingsUpdate = settingsUpdate;

            showPasswordFragment();
        }

        @Override
        public void onSignUpWithFacebook() {

            authoriseWithFacebook();
        }

        @Override
        public void onSignUpWithTwitter() {
            authoriseWithTwitter();
        }
    };

    private SignUpPasswordFragment.Listener signUpPasswordListener = (password, referralCode) -> {
        if (isConnected()) {
            this.password = password;
            this.referralCode = referralCode;

            Registration registration = Registration.create(emailAddress, password, referralCode);

            register(registration);
        } else {
            showConnectionError();
        }
    };

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_sign_up);
        Icepick.restoreInstanceState(this, state);

        //noinspection ConstantConditions
        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);

        SignUpFragment signUpFragment = new SignUpFragment();

        if (state == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, signUpFragment, SIGN_UP_TAG)
                    .commit();

            signUpFragment.setListener(signUpListener);
        } else {
            restoreFragments();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Register);
    }

    private void restoreFragments() {
        FragmentManager manager = getSupportFragmentManager();

        SignUpFragment signUpFragment = (SignUpFragment) manager.findFragmentByTag(SIGN_UP_TAG);
        passwordFragment = (SignUpPasswordFragment) manager.findFragmentByTag(PASSWORD_TAG);

        if (signUpFragment != null) {
            signUpFragment.setListener(signUpListener);
        }

        if (passwordFragment != null) {
            passwordFragment.setListener(signUpPasswordListener);
        }
    }

    private void showPasswordFragment() {
        if (passwordFragment == null) {
            passwordFragment = new SignUpPasswordFragment();
            passwordFragment.setListener(signUpPasswordListener);
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_up_out, R.anim.slide_down_in, R.anim.slide_down_out)
                .replace(R.id.container, passwordFragment, PASSWORD_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Icepick.saveInstanceState(this, state);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDisableInputFields() {
        if (passwordFragment != null) {
            passwordFragment.disableCreateAccount();
        }
    }

    @Override
    protected void onEnableInputFields() {
        if (passwordFragment != null) {
            passwordFragment.enableCreateAccount();
        }
    }

    @Override
    protected void onAuthorised() {
        if (userSettingsUpdate != null) {
            Intent intent = new Intent(this, SettingsUpdateService.class);
            intent.putExtra(SettingsUpdateService.EXTRA_SETTINGS, userSettingsUpdate);
            startService(intent);
        }

        setResult(RESULT_OK);
        finish();
    }
}
