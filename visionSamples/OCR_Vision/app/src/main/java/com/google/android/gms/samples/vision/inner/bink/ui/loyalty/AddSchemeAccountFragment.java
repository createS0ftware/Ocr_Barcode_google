package com.google.android.gms.samples.vision.inner.bink.ui.loyalty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.scheme.AddSchemePayload;
import com.loyaltyangels.bink.model.scheme.Question;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.model.scheme.SchemeOfferImage;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.util.UiUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

/**
 * Created by jmcdonnell on 24/10/2016.
 */

public class AddSchemeAccountFragment extends BaseFragment {

    private static final String TAG = AddSchemeAccountFragment.class.getSimpleName();
    private static final String EXTRA_SCHEME = "scheme";
    private static final String EXTRA_BARCODE = "barcode";

    public static interface Listener {
        void onSchemeAccountAdded(SchemeAccount account);
    }

    public static AddSchemeAccountFragment newInstance(@NonNull Scheme scheme, @Nullable String barcode) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SCHEME, scheme);
        args.putString(EXTRA_BARCODE, barcode);

        AddSchemeAccountFragment fragment = new AddSchemeAccountFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private Scheme scheme;
    private String barcode;
    private Listener listener;

    @BindView(R.id.scheme_image)
    ImageView schemeImage;

    @BindView(R.id.barcode)
    EditText questionInput;

    @BindView(R.id.card_title)
    TextView cardTitle;

    @BindView(R.id.question_layout)
    TextInputLayout barcodeInputLayout;

    @BindView(R.id.add_card)
    Button addCard;

    @BindView(R.id.topLayout)
    LinearLayout topLayout;

    @BindView(R.id.join)
    Button joinScheme;


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_add_scheme_account;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            scheme = getArguments().getParcelable(EXTRA_SCHEME);
            barcode = getArguments().getString(EXTRA_BARCODE);
        }

        if (scheme != null) {
            displayScheme();
        } else {
            Log.e(TAG, "Scheme not provided");
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.add_card)
    void onAddCard() {
        boolean scanned = barcode != null;
        String question;

        if (scanned && scheme.getScanQuestion() != null) {
            question = scheme.getScanQuestion().getType().getName().toLowerCase();
        } else if (scheme.getManualQuestion() != null) {
            question = scheme.getManualQuestion().getType().getName().toLowerCase();
        } else {
            question = null; // this should never happen
        }
        barcode = questionInput.getText().toString();

        if (barcode.length() == 0) {
            questionInput.setError(
                    String.format(
                            getResources().getString(R.string.add_scheme_manually_question_empty),
                            scheme.getManualQuestion().getLabel().toLowerCase()));
        } else {
            if (isConnected()) {
                ProgressDialog dialog = new ProgressDialog(getContext(), R.style.AlertDialogStyle);
                dialog.setMessage(getString(R.string.add_scheme_adding_card));

                Subscription delayedProgressSubscription = Observable.timer(500, TimeUnit.MILLISECONDS)
                        .compose(applySchedulers())
                        .subscribe(l -> dialog.show());

                AddSchemePayload payload = new AddSchemePayload(scheme.getId(), question, barcode);

                model.addSchemeAccountToModel(payload)
                        .compose(applySchedulers())
                        .doOnTerminate(() -> {
                            delayedProgressSubscription.unsubscribe();
                            dialog.dismiss();
                        })
                        .subscribe(result -> {
                            listener.onSchemeAccountAdded(result);
                        }, error -> {
                            error.printStackTrace();

                            new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                                    .setTitle(getResources().getString(R.string.add_scheme_manually_error_title))
                                    .setMessage(getResources().getString(R.string.add_scheme_manually_error_alert))
                                    .setPositiveButton(R.string.alert_ok, null)
                                    .show();

                            addCard.setEnabled(true);
                        });
            } else {
                showConnectionError();
                addCard.setEnabled(true);
            }
        }
    }

    @OnClick(R.id.join)
    void onJoinScheme() {
        String url = scheme.getJoinUrl();
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            if (getContext().getPackageManager().resolveActivity(intent, 0) != null) {
                startActivity(intent);
            }
        }
    }


    private void displayScheme() {
        topLayout.setVisibility(View.VISIBLE);
        cardTitle.setVisibility(View.VISIBLE);
        cardTitle.setText(scheme.getCompany());
        questionInput.setText(barcode);
        showHint();

        View vt = getView().findViewById(R.id.card_view_layout);
        ImageView appIcon = (ImageView) vt.findViewById(R.id.appIcon);
        SchemeOfferImage schemeImageHero = scheme.findImageByType(ImageType.HERO);

        appIcon.setBackgroundColor(Color.parseColor(scheme.getColour()));

        Glide.with(this)
                .load(schemeImageHero.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        appIcon.setImageDrawable(new ColorDrawable(Color.parseColor(scheme.getColour())));
                        if (!isConnected()) {
                            showConnectionError();
                        }
                        topLayout.setVisibility(View.VISIBLE);
                        cardTitle.setVisibility(View.VISIBLE);
                        cardTitle.setText(scheme.getCompany());
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        topLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(appIcon);

        if (!TextUtils.isEmpty(scheme.getJoinUrl())) {
            joinScheme.setVisibility(View.VISIBLE);
        } else {
            joinScheme.setVisibility(View.GONE);
        }
    }

    private void showHint() {
        if (scheme.getManualQuestion() != null) {
            String hint = "";
            if (scheme.getManualQuestion().getType() != null) {
                hint = scheme.getManualQuestion().getLabel().toLowerCase();
                Question manualQuestion = scheme.getManualQuestion();
                questionInput.setInputType(UiUtil.getInputTypeForQuestionType(manualQuestion.getType()));
            }
            barcodeInputLayout.setHint(getResources().getString(R.string.manual_prompt) + " " + hint);
        }
    }
}
