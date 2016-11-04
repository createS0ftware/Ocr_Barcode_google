package com.google.android.gms.samples.vision.inner.bink.ui.forgotpassword;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.loyaltyangels.bink.EmailUtils;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.widget.LoginInputLayout;
import com.loyaltyangels.bink.util.BinkUtil;
import com.loyaltyangels.bink.util.TintUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;
import rx.Subscription;

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.send_recovery_email)
    Button sendEmail;

    @BindView(R.id.email)
    TextInputEditText email;

    @BindView(R.id.email_layout)
    LoginInputLayout emailInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        //noinspection ConstantConditions
        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean valid = BinkUtil.validateEmail(s.toString());
                sendEmail.setEnabled(valid);

                if (valid) {
                    emailInput.showTick();
                } else {
                    emailInput.hideTick();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.ForgotPassword);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.send_recovery_email)
    void onSendRecoveryEmailClicked() {
        sendRecoveryEmail();
    }

    @OnEditorAction(R.id.email)
    boolean onEmailNext() {
        sendRecoveryEmail();
        return true;
    }

    private void sendRecoveryEmail() {
        if (isConnected()) {
            if (EmailUtils.checkifEmail(email.getText().toString())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

                ProgressDialog progress = new ProgressDialog(this);
                progress.setCancelable(false);
                progress.setMessage(getString(R.string.forgot_password_sending_email));

                Subscription delayedProgress = Observable.timer(300, TimeUnit.MILLISECONDS)
                        .compose(applySchedulers())
                        .subscribe(l -> progress.show());

                model.forgottenPassword(email.getText().toString())
                        .compose(applySchedulers())
                        .doOnTerminate(() -> {
                            delayedProgress.unsubscribe();
                            progress.dismiss();
                        })
                        .subscribe(result -> showSuccess(), error -> showError());
            } else {
                email.setError(getString(R.string.forgot_password_invalid_email));
            }
        } else {
            showConnectionError();
        }

    }

    private void showError() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(getString(R.string.forgot_password_error_title))
                .setMessage(getString(R.string.forgot_password_error_message))
                .setPositiveButton(R.string.alert_ok, null)
                .show();
    }

    private void showSuccess() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(getString(R.string.forgot_password_sent_title))
                .setMessage(getString(R.string.forgot_password_sent_message))
                .setOnDismissListener(dialog -> finish())
                .setPositiveButton(R.string.alert_ok, null)
                .show();
    }

}
