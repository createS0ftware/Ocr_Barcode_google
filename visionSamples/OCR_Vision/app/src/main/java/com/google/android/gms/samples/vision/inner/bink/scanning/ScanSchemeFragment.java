package com.google.android.gms.samples.vision.inner.bink.scanning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeView;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.common.Image;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.scanning.LoyaltyScanResult;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by John McDonnell on 24/09/2016.
 */

public class ScanSchemeFragment extends BaseScanFragment {

    private static final String TAG = ScanSchemeFragment.class.getSimpleName();

    private static final String EXTRA_JOIN_SCHEME = "join_scheme";

    public interface Listener {
        void onSchemeIdentified(@NonNull LoyaltyScanResult result);

        void onEnterManually();

        void onCancelled();
    }

    public static ScanSchemeFragment newInstance(@Nullable Scheme joinScheme) {
        Bundle args = new Bundle();

        if (joinScheme != null) {
            args.putParcelable(EXTRA_JOIN_SCHEME, joinScheme);
        }

        ScanSchemeFragment fragment = new ScanSchemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.scanner_view)
    ScannerView scannerView;

    @BindView(R.id.scheme_layout)
    ViewGroup schemeLayout;

    @BindView(R.id.scheme_name)
    TextView schemeName;

    @BindView(R.id.scheme_icon)
    ImageView schemeIcon;

    @BindView(R.id.scan_question)
    TextView scanQuestion;

    @BindView(R.id.scan_instruction)
    TextView scanInstruction;

    @BindView(R.id.error_message)
    TextView errorMessage;

    private Listener listener;
    private Subscription schemesSubscription;
    private Subscription barcodeSubscription;
    private IdentifySchemeProcessor scanProcessor;
    private Wallet wallet;
    private Scheme joinScheme;
    private AlertDialog unrecognisedBarcodeDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            joinScheme = getArguments().getParcelable(EXTRA_JOIN_SCHEME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_scheme, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanProcessor = new IdentifySchemeProcessor(model);
        setProcessor(scanProcessor);

        schemesSubscription = Subscriptions.empty();
        barcodeSubscription = Subscriptions.empty();

        loadData();

        if (joinScheme != null) {
            updateSchemePreview(joinScheme);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToBarcodeResults();

        if (joinScheme == null) {
            subscribeToSchemeResults();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        schemesSubscription.unsubscribe();
        barcodeSubscription.unsubscribe();

        if (unrecognisedBarcodeDialog != null) {
            unrecognisedBarcodeDialog.dismiss();
        }
    }

    @Override
    protected BarcodeView getBarcodeView() {
        return scannerView.getBarcodeView();
    }

    @OnClick(R.id.enter_manually)
    void onEnterManually() {
        listener.onEnterManually();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        super.possibleResultPoints(resultPoints);
        scannerView.addPossiblePoints(resultPoints);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void showLoadErrorDialog() {
        new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setMessage(R.string.add_scheme_loading_error)
                .setPositiveButton(R.string.alert_retry, (dialog, which) -> {
                    loadData();
                })
                .setNegativeButton(R.string.alert_cancel, (dialog, which) -> {
                    listener.onCancelled();
                })
                .setCancelable(false)
                .show();
    }

    private void loadData() {
        model.getSchemes()
                .compose(applySchedulers())
                .flatMap(schemes -> {
                    scanProcessor.setSchemes(schemes);
                    return model.getWallet();
                })
                .subscribe(wallet -> {
                    this.wallet = wallet;
                }, error -> {
                    showLoadErrorDialog();
                });
    }

    private void subscribeToSchemeResults() {
        schemesSubscription.unsubscribe();
        schemesSubscription = scanProcessor.schemeResults()
                .observeOn(AndroidSchedulers.mainThread())
                .retry(5)
                .subscribe(this::schemeIdentified, throwable -> {
                    showUnableToRecogniseDialog();
                });
    }

    private void subscribeToBarcodeResults() {
        barcodeSubscription = scanProcessor.barcodeResults()
                .observeOn(AndroidSchedulers.mainThread())
                .retry((integer, throwable) -> {
                    showUnrecognisedBarcodeDialog();
                    return true;
                })
                .subscribe(this::schemeAndBarcodeIdentified, Throwable::printStackTrace);
    }

    private void schemeAndBarcodeIdentified(LoyaltyScanResult result) {
        Log.d(TAG, "Scheme and barcode identified: scheme=" + result.getScheme().getName() + ", barcode=" + result.getBarcode());

        if (isDuplicate(result.getScheme())) {
            updateSchemePreview(result.getScheme());
        } else if (isWrongJoinScheme(result.getScheme())) {
            scanQuestion.setVisibility(View.GONE);
            errorMessage.setText(getString(R.string.add_scheme_error_incorrect, joinScheme.getName()));
            errorMessage.setVisibility(View.VISIBLE);
        } else {
            listener.onSchemeIdentified(result);
        }
    }

    private void schemeIdentified(Scheme scheme) {
        Log.d(TAG, "Scheme identified: " + scheme.getName());

        boolean duplicate = isDuplicate(scheme);

        if (duplicate || scheme.getScanQuestion() != null) {
            updateSchemePreview(scheme);
        } else {
            listener.onSchemeIdentified(new LoyaltyScanResult(scheme, null));
        }

        if (!duplicate) {
            schemesSubscription.unsubscribe();
        }
    }

    private void updateSchemePreview(Scheme scheme) {
        scanInstruction.setVisibility(View.INVISIBLE);
        schemeLayout.setVisibility(View.VISIBLE);

        Image image = scheme.findImageByType(ImageType.ICON);

        if (image != null) {
            schemeIcon.setVisibility(View.VISIBLE);

            Glide.with(getContext())
                    .load(image.getImageUrl())
                    .into(schemeIcon);
        } else {
            schemeIcon.setVisibility(View.GONE);
        }

        schemeName.setText(scheme.getName());

        if (isDuplicate(scheme)) {
            errorMessage.setText(R.string.add_scheme_error_duplicate);
            errorMessage.setVisibility(View.VISIBLE);
            scanQuestion.setVisibility(View.GONE);
        } else {
            errorMessage.setVisibility(View.GONE);
            scanQuestion.setText(scheme.getScanMessage());
            scanQuestion.setVisibility(View.VISIBLE);
        }
    }

    public void hideSchemePreview() {
        scanInstruction.setVisibility(View.VISIBLE);
        schemeLayout.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        schemeName.setText(null);
    }

    private boolean isDuplicate(Scheme scheme) {
        if (wallet != null && joinScheme == null) {
            for (SchemeAccount schemeAccount : wallet.getSchemeAccounts()) {
                if (scheme.getId().equals(schemeAccount.getScheme().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWrongJoinScheme(Scheme scheme) {
        return joinScheme != null && !TextUtils.equals(scheme.getId(), joinScheme.getId());
    }

    private void showUnableToRecogniseDialog() {
        new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setMessage(R.string.add_scheme_scan_error_message)
                .setPositiveButton(R.string.add_scheme_scan_error_manual, (dialog, which) ->
                        listener.onEnterManually())
                .setNegativeButton(R.string.add_scheme_scan_error_retry, (dialog, which) ->
                        subscribeToSchemeResults())
                .setCancelable(false)
                .show();
    }

    private void showUnrecognisedBarcodeDialog() {
        if (unrecognisedBarcodeDialog == null || !unrecognisedBarcodeDialog.isShowing()) {
            unrecognisedBarcodeDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                    .setMessage(R.string.add_scheme_unrecognised)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }
}