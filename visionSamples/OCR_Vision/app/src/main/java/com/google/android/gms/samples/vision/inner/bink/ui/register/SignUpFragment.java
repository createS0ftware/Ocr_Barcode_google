package com.google.android.gms.samples.vision.inner.bink.ui.register;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.user.settings.Setting;
import com.loyaltyangels.bink.model.user.settings.SettingsOption;
import com.loyaltyangels.bink.model.user.settings.SettingsUpdate;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.ui.widget.LoginInputLayout;
import com.loyaltyangels.bink.util.BinkUtil;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by jmcdonnell on 17/08/2016.
 */

public class SignUpFragment extends BaseFragment {

    public interface Listener {
        void onDetailsEntered(String email, SettingsUpdate settingsUpdate);

        void onSignUpWithFacebook();

        void onSignUpWithTwitter();
    }

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.email_layout)
    LoginInputLayout emailLayout;

    @BindView(R.id.next)
    Button next;

    @BindView(R.id.marketing)
    CheckBox marketing;

    @BindView(R.id.legal)
    TextView legal;

    private Listener listener;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        legal.setLinksClickable(true);

        Linkify.addLinks(legal, Linkify.WEB_URLS);
        legal.setMovementMethod(LinkMovementMethod.getInstance());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                boolean valid = validateEmail();
                next.setEnabled(valid);

                if (valid) {
                    emailLayout.showTick();
                } else {
                    emailLayout.hideTick();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.next)
    void onNextClicked() {
        validate();
    }

    @OnClick(R.id.facebook)
    void onFacebookClicked() {
        listener.onSignUpWithFacebook();
    }

    @OnClick(R.id.twitter)
    void onTwitterClicked() {
        listener.onSignUpWithTwitter();
    }

    @OnEditorAction(R.id.email)
    boolean onEmailEditorAction() {
        if (validateEmail()) {
            validate();
        }

        return true;
    }

    private boolean validateEmail() {
        return BinkUtil.validateEmail(email.getText().toString());
    }

    private void validate() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

        String emailAddress = email.getText().toString();

        SettingsOption option = marketing.isChecked() ?
                SettingsOption.Enabled : SettingsOption.Disabled;

        SettingsUpdate settingsUpdate = new SettingsUpdate();
        settingsUpdate.setOption(Setting.SLUG_BINK_MARKETING, option);
        settingsUpdate.setOption(Setting.SLUG_EXTERNAL_MARKETING, option);

        listener.onDetailsEntered(emailAddress, settingsUpdate);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
