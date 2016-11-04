package com.google.android.gms.samples.vision.inner.bink.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loyaltyangels.bink.BuildConfig;
import com.loyaltyangels.bink.ChangePasswordActivity;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.ui.splash.SplashActivity;
import com.loyaltyangels.bink.util.BinkUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jmcdonnell on 08/09/2016.
 */

public class SettingsFragment extends BaseFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @BindView(R.id.version)
    TextView version;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_settings;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Settings);
    }

    @OnClick(R.id.email_feedback)
    void onEmailFeedbackClicked() {
        Intent intent = createFeedbackIntent();

        if (getContext().getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(Intent.createChooser(intent, null));
        } else {
            Toast.makeText(getActivity(), R.string.settings_email_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.email_support)
    void onEmailSupportClicked() {
        Intent intent = createSupportIntent();

        if (getContext().getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(Intent.createChooser(intent, null));
        } else {
            Toast.makeText(getActivity(), R.string.settings_email_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.app_review)
    void onAppReviewClicked() {
        Uri uri = Uri.parse(getString(R.string.app_store_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (getContext().getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(intent, null);
            tracker.trackScreen(Screen.WebView, uri.toString());
        } else {
            Log.e(TAG, "Unable to open app store URL");
        }
    }

    @OnClick(R.id.preferences)
    void onPreferencesClicked() {
        Intent intent = new Intent(getContext(), PreferencesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.logout)
    void onLogoutClicked() {
        showLogoutDialog();
    }

    @OnClick(R.id.change_password)
    void onChangePasswordClicked() {
        Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.privacy_policy)
    void onPrivacyPolicyClicked() {
        Uri uri = Uri.parse(getResources().getString(R.string.url_privacy));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);

        tracker.trackScreen(Screen.WebView, uri.toString());
    }

    @OnClick(R.id.terms)
    void onTermsClicked() {
        Uri uri = Uri.parse(getResources().getString(R.string.terms_url));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);

        tracker.trackScreen(Screen.WebView, uri.toString());
    }

    private Intent createSupportIntent() {
        String email = apiConfig.getUserEmail();
        String version = BuildConfig.VERSION_NAME;
        String platform = Build.VERSION.RELEASE;
        CharSequence body = Html.fromHtml(getString(R.string.settings_support_body, email, version, platform));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.bink_support_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject));
        intent.putExtra(Intent.EXTRA_TEXT, body);

        return intent;
    }

    private Intent createFeedbackIntent() {
        String email = apiConfig.getUserEmail();
        String version = BuildConfig.VERSION_NAME;
        String platform = Build.VERSION.RELEASE;

        CharSequence body = Html.fromHtml(getString(R.string.settings_feedback_body, email, version, platform));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.bink_feedback_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_subject));
        intent.putExtra(Intent.EXTRA_TEXT, body);

        return intent;
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(R.string.settings_logout_title)
                .setPositiveButton(R.string.settings_logout_confirm, (dialog, i) -> {
                    BinkUtil.resetApplication(getActivity(), model);

                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.alert_cancel, null)
                .show();
    }
}
