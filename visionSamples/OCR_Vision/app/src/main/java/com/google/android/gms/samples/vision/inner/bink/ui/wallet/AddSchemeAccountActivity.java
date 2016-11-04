package com.google.android.gms.samples.vision.inner.bink.ui.wallet;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.scanning.LoyaltyScanResult;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.scanning.ScanSchemeFragment;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.loyalty.AddSchemeAccountFragment;
import com.loyaltyangels.bink.ui.loyalty.SchemesListActivity;
import com.loyaltyangels.bink.util.TintUtils;


/**
 * Created by akumar on 04/02/16.
 */
public class AddSchemeAccountActivity extends BaseActivity {

    private static final String TAG = AddSchemeAccountActivity.class.getSimpleName();

    private static final String FRAGMENT_TAG_ADD_SCHEME = "add_scheme";
    private static final String FRAGMENT_TAG_SCAN_SCHEME = "scan_scheme";

    private static final int REQUEST_SELECT_SCHEME = 0;
    private static final int REQUEST_PERMISSION_CAMERA = 0;

    public static final String EXTRA_JOIN_SCHEME = "join_scheme";
    public static final String EXTRA_SCHEME_ACCOUNT = "scheme_account";

    public static final int RESULT_CARD_NOT_SELECTED = 5;
    public static final int RESULT_CARD_SELECTED = 7;


    private Scheme joinScheme;

    private AddSchemeAccountFragment.Listener addSchemeListener = account -> {
        Intent data = new Intent();
        data.putExtra(EXTRA_SCHEME_ACCOUNT, account);
        setResult(RESULT_OK, data);
        finish();
    };

    private ScanSchemeFragment.Listener scanListener = new ScanSchemeFragment.Listener() {
        @Override
        public void onSchemeIdentified(@NonNull LoyaltyScanResult result) {
            showAddScheme(result.getScheme(), result.getBarcode(), false);
        }

        @Override
        public void onEnterManually() {
            if (joinScheme != null) {
                showAddScheme(joinScheme, null, false);
            } else {
                showPickScheme();
            }
        }

        @Override
        public void onCancelled() {
            finish();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scheme_account);

        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
        joinScheme = getIntent().getParcelableExtra(EXTRA_JOIN_SCHEME);

        if (savedInstanceState == null) {
            if (joinScheme == null || joinScheme.getScanQuestion() != null) {
                showScanScheme();
            } else {
                showAddScheme(joinScheme, null, false);
            }
        } else {
            restoreListeners();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.AddLoyaltyCard);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showScanScheme();
        } else {
            showPickScheme();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_SCHEME && resultCode == RESULT_OK) {
            Scheme scheme = data.getParcelableExtra(SchemesListActivity.EXTRA_SELECTED_SCHEME);

            ScanSchemeFragment fragment = (ScanSchemeFragment)
                    getSupportFragmentManager().findFragmentById(R.id.container);

            if (fragment != null) {
                fragment.hideSchemePreview();
            }

            new Handler().postDelayed(() -> {
                showAddScheme(scheme, null, true);
            }, 250);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        if (!hasCameraPermissions()) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        } else {
            showScanScheme();
        }
    }

    private void showPickScheme() {
        Intent intent = new Intent(this, SchemesListActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_SCHEME);
    }

    private void showScanScheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasCameraPermissions()) {
            requestCameraPermission();
            return;
        }

        ScanSchemeFragment fragment = ScanSchemeFragment.newInstance(joinScheme);
        fragment.setListener(scanListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, FRAGMENT_TAG_SCAN_SCHEME)
                .commit();
    }

    private void showAddScheme(@NonNull Scheme scheme, @Nullable String barcode, boolean allowStateLoss) {
        AddSchemeAccountFragment addSchemeFragment = AddSchemeAccountFragment.newInstance(scheme, barcode);
        addSchemeFragment.setListener(addSchemeListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.transition_slide_in, R.anim.transition_slide_out, R.anim.transition_slide_return_in, R.anim.transition_slide_return_out)
                .replace(R.id.container, addSchemeFragment, FRAGMENT_TAG_ADD_SCHEME);

        if (getSupportFragmentManager().findFragmentById(R.id.container) != null) {
            transaction.addToBackStack(FRAGMENT_TAG_ADD_SCHEME);
        }

        if (allowStateLoss) {
            transaction.commitAllowingStateLoss();
        } else {
            transaction.commit();
        }
    }

    private void restoreListeners() {
        AddSchemeAccountFragment addSchemeFragment =
                (AddSchemeAccountFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_ADD_SCHEME);

        if (addSchemeFragment != null) {
            addSchemeFragment.setListener(addSchemeListener);
        }

        ScanSchemeFragment scanFragment =
                (ScanSchemeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SCAN_SCHEME);

        if (scanFragment != null) {
            scanFragment.setListener(scanListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasCameraPermissions() {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

}
