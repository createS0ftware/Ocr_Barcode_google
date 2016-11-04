package com.google.android.gms.samples.vision.inner.bink.ui.register;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.ui.widget.LoginInputLayout;
import com.loyaltyangels.bink.util.BinkUtil;
import com.trello.rxlifecycle.FragmentEvent;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by jmcdonnell on 17/08/2016.
 */

public class SignUpPasswordFragment extends BaseFragment {

    public interface Listener {
        void onPasswordEntered(String password, String referralCode);
    }

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.password_layout)
    LoginInputLayout passwordLayout;

    @BindView(R.id.confirm_password)
    EditText confirmPassword;

    @BindView(R.id.confirm_password_layout)
    LoginInputLayout confirmPasswordLayout;

    @BindView(R.id.referral_code)
    EditText referralCode;

    @BindView(R.id.next)
    Button next;

    private Listener listener;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxTextView.textChanges(confirmPassword)
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribe(text -> {
                    boolean valid = doesPasswordMeetCriteria() && doPasswordsMatch();
                    next.setEnabled(valid);

                    if (valid) {
                        confirmPasswordLayout.showTick();
                    } else {
                        confirmPasswordLayout.hideTick();
                    }
                });

        confirmPassword.setOnFocusChangeListener((v, focused) -> {
            if (!focused && !doPasswordsMatch()) {
                confirmPassword.setError(getString(R.string.sign_up_password_mismatch));
            } else if (focused) {
                next.setEnabled(doesPasswordMeetCriteria() && doPasswordsMatch());
            }
        });

        RxTextView.textChanges(password)
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .map(charSequence -> doesPasswordMeetCriteria())
                .subscribe(valid -> {
                    confirmPassword.setText(null);
                    confirmPassword.setError(null);

                    next.setEnabled(valid);

                    if (valid) {
                        passwordLayout.showTick();
                        password.setError(null);
                    } else {
                        passwordLayout.hideTick();
                    }
                });

        RxView.focusChanges(password)
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribe(focused -> {
                    boolean valid = doesPasswordMeetCriteria();
                    next.setEnabled(valid);

                    if (!focused && !valid && !TextUtils.isEmpty(password.getText())) {
                        password.setError(getString(R.string.sign_up_error_password_criteria));
                    }
                });

        password.requestFocus();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_sign_up_password;
    }

    @OnClick(R.id.next)
    void onNextClicked() {
        if (password.hasFocus()) {
            confirmPassword.requestFocus();
        } else if (confirmPassword.hasFocus()) {
            referralCode.requestFocus();
        } else {
            createAccount();
        }
    }

    @OnEditorAction(R.id.referral_code)
    boolean onReferralCodeNext() {
        createAccount();
        return true;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void createAccount() {
        if (isPasswordEmpty()) {
            password.setError(getString(R.string.sign_up_error_password_empty));
        } else if (!doPasswordsMatch()) {
            confirmPassword.setError(getString(R.string.sign_up_password_mismatch));
        } else if (!doesPasswordMeetCriteria()) {
            password.setError(getString(R.string.sign_up_error_password_criteria));
        } else {
            String referral = referralCode.getText().toString();

            /**
             * Don't send empty String to API.
             */
            if (TextUtils.isEmpty(referral)) {
                referral = null;
            }

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

            listener.onPasswordEntered(password.getText().toString(), referral);
        }
    }

    private boolean isPasswordEmpty() {
        return TextUtils.isEmpty(password.getText());
    }

    private boolean doPasswordsMatch() {
        return TextUtils.equals(password.getText(), confirmPassword.getText());
    }

    private boolean doesPasswordMeetCriteria() {
        return BinkUtil.validatePassword(password.getText().toString());
    }

    public void disableCreateAccount() {
        next.setEnabled(false);
    }

    public void enableCreateAccount() {
        next.setEnabled(true);
    }
}
