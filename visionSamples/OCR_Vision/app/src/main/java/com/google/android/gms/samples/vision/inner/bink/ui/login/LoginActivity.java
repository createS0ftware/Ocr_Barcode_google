package com.google.android.gms.samples.vision.inner.bink.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.Authorisation;
import com.loyaltyangels.bink.ui.AuthoriseActivity;
import com.loyaltyangels.bink.ui.forgotpassword.ForgotPasswordActivity;
import com.loyaltyangels.bink.ui.widget.LoginInputLayout;
import com.loyaltyangels.bink.util.BinkUtil;
import com.loyaltyangels.bink.util.TintUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;

public class LoginActivity extends AuthoriseActivity {

    @BindView(R.id.next)
    Button next;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.email_layout)
    LoginInputLayout emailLayout;

    @BindView(R.id.password_layout)
    LoginInputLayout passwordLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //noinspection ConstantConditions
        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                boolean validEmail = BinkUtil.validateEmail(text.toString());
                passwordLayout.setVisibility(validEmail ? View.VISIBLE : View.GONE);
                next.setEnabled(validEmail);

                if (validEmail) {
                    emailLayout.showTick();
                } else {
                    emailLayout.hideTick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (BinkUtil.validatePassword(charSequence.toString())) {
                    passwordLayout.showTick();
                } else {
                    passwordLayout.hideTick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Login);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.facebook)
    void onFacebookClicked() {
        authoriseWithFacebook();
    }

    @OnClick(R.id.twitter)
    void onTwitterClicked() {
        authoriseWithTwitter();
    }

    @OnClick(R.id.next)
    void onNextClicked() {
        if (email.hasFocus()) {
            password.requestFocus();
        } else {
            login();
        }
    }

    @OnClick(R.id.forgot_password)
    void onForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @OnEditorAction(R.id.password)
    boolean onPasswordNext() {
        login();
        return true;
    }

    @OnEditorAction(R.id.email)
    boolean onEmailNext() {
        if (passwordLayout.getVisibility() == View.VISIBLE) {
            password.requestFocus();
        }
        return true;
    }

    private void login() {
        if (isConnected()) {
            if (validate()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

                String email = this.email.getText().toString();
                String password = this.password.getText().toString();

                Observable<Authorisation> observable = model.login(email, password);
                authorize(observable, true);
            }
        } else {
            showConnectionError();
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(email.getText())) {
            email.setError(getString(R.string.login_error_empty_email));
            return false;
        } else if (TextUtils.isEmpty(password.getText())) {
            password.setError(getString(R.string.login_error_empty_password));
            return false;
        }

        return true;
    }

    @Override
    protected void onDisableInputFields() {
        next.setEnabled(false);
    }

    @Override
    protected void onEnableInputFields() {
        next.setEnabled(true);
    }

    @Override
    protected void onAuthorised() {
        setResult(RESULT_OK);
        finish();
    }
}
