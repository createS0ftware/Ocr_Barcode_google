package com.google.android.gms.samples.vision.inner.bink.ui.card_detail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.model.scheme.SchemeOfferImage;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.components.PaymentCardView;
import com.loyaltyangels.bink.util.TintUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import me.relex.circleindicator.CircleIndicator;
import rx.Observable;
import rx.Subscription;

/**
 * Created by akumar on 09/03/16.
 */
public class PayDetailActivity extends BaseActivity {

    public static final String EXTRA_PAYMENT_CARD_ACCOUNT = "payment_card_account";
    public static final String EXTRA_DELETED_ACCOUNT_ID = "deleted_account_id";
    public static final int RESULT_PAYMENT_CARD_DELETED = 31;

    private PaymentCardAccount paymentCardAccount;

    @BindView(R.id.payment_card)
    PaymentCardView paymentCardView;

    @BindView(R.id.personal_offers)
    CardView personalOffersLayout;

    @BindView(R.id.generic_offers)
    CardView genericOffersLayout;

    private MenuItem deleteItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_detail);

        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);

        paymentCardAccount = getIntent().getExtras().getParcelable(EXTRA_PAYMENT_CARD_ACCOUNT);
        paymentCardView.setPaymentCard(paymentCardAccount);

        ArrayList<SchemeOfferImage> personalImages = imagesForType(ImageType.PERSONAL_OFFER);
        showOffers(personalOffersLayout, personalImages);

        ArrayList<SchemeOfferImage> genericImages = imagesForType(ImageType.OFFER);
        showOffers(genericOffersLayout, genericImages);
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.PaymentDetail, paymentCardAccount.getId());
    }

    @NonNull
    private ArrayList<SchemeOfferImage> imagesForType(ImageType type) {
        ArrayList<SchemeOfferImage> images = new ArrayList<>();
        for (SchemeOfferImage image : paymentCardAccount.getImages()) {
            if (image.getImageType() == type) {
                images.add(image);
            }
        }
        return images;
    }

    private void showOffers(ViewGroup offersLayout, @NonNull ArrayList<SchemeOfferImage> images) {
        if (!images.isEmpty()) {
            offersLayout.setVisibility(View.VISIBLE);
            CircleIndicator indicator = (CircleIndicator) offersLayout.findViewById(R.id.indicator);
            ViewPager pager = (ViewPager) offersLayout.findViewById(R.id.viewpager);

            pager.setAdapter(new OffersAdapter(this, images));
            pager.setPageMargin(40);
            pager.setOffscreenPageLimit(2);
            indicator.setViewPager(pager);
        } else {
            offersLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        deleteItem = menu.findItem(R.id.deleteItem);
        deleteItem.setOnMenuItemClickListener(menuItem -> {
            if (isConnected()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PayDetailActivity.this);
                builder.setMessage(String.format(getString(R.string.pay_detail_delete_card), paymentCardAccount.getPaymentCardType().name()))
                        .setTitle("Delete " + paymentCardAccount.getPaymentCardType().name())
                        .setPositiveButton(R.string.alert_cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Delete", (dialogInterface, i) -> {
                            dialogInterface.dismiss();

                            ProgressDialog dialog = new ProgressDialog(this, R.style.AlertDialogStyle);
                            dialog.setMessage(getString(R.string.payment_detail_deleting));

                            Subscription delayedProgressSubscription = Observable.timer(500, TimeUnit.MILLISECONDS)
                                    .compose(applySchedulers())
                                    .subscribe(l -> dialog.show());

                            model.deletePaymentCard(paymentCardAccount.getId())
                                    .compose(applySchedulers())
                                    .doOnTerminate(() -> {
                                        delayedProgressSubscription.unsubscribe();
                                        dialog.dismiss();
                                    })
                                    .subscribe(schemeAccount -> {
                                        Intent data = new Intent();
                                        data.putExtra(EXTRA_DELETED_ACCOUNT_ID, paymentCardAccount.getId());

                                        setResult(RESULT_PAYMENT_CARD_DELETED, data);
                                        finish();
                                    }, Throwable::printStackTrace);
                        }).create().show();
            } else {
                showConnectionError();
            }
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}