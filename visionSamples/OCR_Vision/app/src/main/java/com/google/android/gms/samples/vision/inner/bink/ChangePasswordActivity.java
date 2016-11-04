package com.google.android.gms.samples.vision.inner.bink;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.user.ChangePasswordPayload;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.util.BinkUtil;
import com.loyaltyangels.bink.util.TintUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by akumar on 18/05/16.
 */
public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_view);
        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.ChangePassword);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnEditorAction(R.id.confirm_password)
    boolean onConfirmPasswordDone() {
        changePassword();
        return true;
    }

    @OnClick(R.id.change_password)
    void onChangePassword() {
        changePassword();
    }

    private void changePassword() {
        if (!BinkUtil.validatePassword(newPassword.getText().toString())) {
            newPassword.setError(getString(R.string.change_password_new_invalid));
        } else if (TextUtils.isEmpty(newPassword.getText())) {
            newPassword.setError(getString(R.string.change_password_new_error));
        } else if (TextUtils.isEmpty(confirmPassword.getText())) {
            confirmPassword.setError(getString(R.string.change_password_confirm_error));
        } else if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            ChangePasswordPayload payload = new ChangePasswordPayload();
            payload.setPassword(newPassword.getText().toString());

            ProgressDialog progress = new ProgressDialog(this, R.style.AlertDialogStyle);
            progress.setMessage(getString(R.string.api_saving));
            progress.show();

            model.changePassword(payload)
                    .compose(applySchedulers())
                    .doOnTerminate(progress::hide)
                    .subscribe(response -> {
                        new AlertDialog.Builder(ChangePasswordActivity.this, R.style.AlertDialogStyle)
                                .setMessage(getString(R.string.change_password_saved))
                                .setPositiveButton(R.string.alert_ok, (dialogInterface, i) -> {
                                    finish();
                                })
                                .show();
                    }, Throwable::printStackTrace); // TODO Handle error.
        } else {
            confirmPassword.setError(getString(R.string.change_password_mismatch));
        }
    }
}