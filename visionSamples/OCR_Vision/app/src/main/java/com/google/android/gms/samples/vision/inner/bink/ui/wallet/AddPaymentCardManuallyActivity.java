package com.google.android.gms.samples.vision.inner.bink.ui.wallet;

/**
 * Created by akumar on 18/01/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Action;
import com.loyaltyangels.bink.analytics.Category;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.common.BinkIntents;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.model.payment.PaymentCardType;
import com.loyaltyangels.bink.spreedly.PaymentMethod;
import com.loyaltyangels.bink.spreedly.SpreedlyApi;
import com.loyaltyangels.bink.spreedly.SpreedlyPayload;
import com.loyaltyangels.bink.spreedly.Transaction;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.wallet.fragments.AddCardInfoDialogFragment;
import com.loyaltyangels.bink.util.BinkUtil;
import com.loyaltyangels.bink.util.TintUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.card.payment.CardDetectionActivity;
import io.card.payment.CreditCard;
import rx.Observable;
import rx.Subscription;

public class AddPaymentCardManuallyActivity extends BaseActivity {

    final String TAG = getClass().getName();

    public static final int PAYMENT_CARD_ADDED = 61;

    @BindView(R.id.add_card_button)
    Button addc;

    @BindView(R.id.cardioImage)
    ImageView cardioImage;
    @BindView(R.id.alert)
    ImageView alert;
    @BindView(R.id.card_logo)
    ImageView cardIcon;
    @BindView(R.id.card_label)
    TextView cardType;
    @BindView(R.id.card_name_edit)
    EditText cardName;
    @BindView(R.id.card_no_edit)
    EditText cardNo;
    @BindView(R.id.card_expiry_edit)
    EditText expiry;

    @BindView(R.id.card_number_label)
    TextView cardNumberLabel;

    @BindView(R.id.scroll_view)
    ScrollView mainView;

    private String mCardType;
    private String name, type, cardnum, cvv, exp, barclCheck;
    private CreditCard scanResult;
    private boolean unsupportedCardShown;
    private Bitmap cardImage;

    private SpreedlyApi spreedlyApi;

    @BindView(R.id.info_btn_i)
    ImageView infoButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_payment_card_manually_layout);

        if (toolbar != null) {
            TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
        }

        Drawable infoIcon = getResources().getDrawable(R.drawable.infocard);
        infoIcon = TintUtils.tintDrawable(this, infoIcon, R.color.colorAccent);
        infoButton.setImageDrawable(infoIcon);

        App application = (App) getApplication();

        spreedlyApi = application.getAppComponent().spreedlyApi();

        cardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean showIcon = false;

                if (start >= 6 || count >= 6) {
                    //Barclay card check
                    barclCheck = s.toString();

                    if (!unsupportedCardShown && BinkUtil.isBarclayCard(barclCheck)) {
                        showUnsupportedCard();
                    } else {
                        showIcon = true;

                        if (s.toString().startsWith("4")) {
                            cardIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.visa));
                        } else {
                            if (s.toString().startsWith("51") || s.toString().startsWith("52") || s.toString().startsWith("53") ||
                                    s.toString().startsWith("54") || s.toString().startsWith("55")) {
                                cardIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mastercard));
                            } else if (s.toString().startsWith("34") || s.toString().startsWith("37")) {
                                cardIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.amex));
                            } else {
                                if (!unsupportedCardShown) {
                                    showUnsupportedCard();
                                }
                            }
                        }
                    }
                } else {
                    showIcon = false;
                    cardIcon.setImageBitmap(null);
                }

                if (s.length() <= 5) {
                    hideUnsupportedCard();
                }

                cardIcon.setVisibility(showIcon ? View.VISIBLE : View.INVISIBLE);
                cardNumberLabel.setVisibility(showIcon ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        expiry.addTextChangedListener(new TextWatcher() {
            String mLastInput = "";

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy", Locale.UK);

                Calendar expiryDateDate = Calendar.getInstance();
                try {
                    expiryDateDate.setTime(formatter.parse(input));
                    if (s.length() == 7) {
                        int month = Integer.parseInt(input.substring(0, 2));
                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                        int year = Integer.parseInt(input.substring(3));
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        if (currentYear > year || year > currentYear + 10) {
                            expiry.setText(expiry.getText().toString().substring(0, 3));
                            expiry.setSelection(expiry.getText().toString().length());
                            Toast.makeText(getApplicationContext(), R.string.add_payment_invalid_expiry, Toast.LENGTH_LONG).show();
                        } else if (currentYear == year && currentMonth + 1 > month) {
                            expiry.setText("");
                            expiry.setSelection(expiry.getText().toString().length());
                            Toast.makeText(getApplicationContext(), R.string.add_payment_invalid_expiry, Toast.LENGTH_LONG).show();
                        }
                    } else if (s.length() > 7) {
                        expiry.setText(expiry.getText().toString().substring(0, 7));
                        expiry.setSelection(expiry.getText().toString().length());
                    }
                } catch (ParseException | NumberFormatException e) {
                    try {
                        if (s.length() == 2 && !mLastInput.endsWith("/")) {
                            int month = Integer.parseInt(input);
                            if (month <= 12) {
                                expiry.setText(expiry.getText().toString() + "/");
                                expiry.setSelection(expiry.getText().toString().length());
                            } else {
                                expiry.setText("");
                                expiry.setSelection(expiry.getText().toString().length());
                                Toast.makeText(getApplicationContext(), R.string.add_payment_invalid_expiry, Toast.LENGTH_LONG).show();
                            }
                        } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                            int month = Integer.parseInt(input);
                            if (month <= 12) {
                                expiry.setText(expiry.getText().toString().substring(0, 1));
                                expiry.setSelection(expiry.getText().toString().length());
                            } else {
                                expiry.setText("");
                                expiry.setSelection(expiry.getText().toString().length());
                                Toast.makeText(getApplicationContext(), R.string.add_payment_invalid_expiry, Toast.LENGTH_LONG).show();
                            }
                        } else if (s.length() == 1) {
                            int month = Integer.parseInt(input);
                            if (month > 1) {
                                expiry.setText("0" + expiry.getText().toString() + "/");
                                expiry.setSelection(expiry.getText().toString().length());
                            }
                        }
                        mLastInput = expiry.getText().toString();

                    } catch (NumberFormatException e1) {
                        // Bad input - clear expiry date
                        mLastInput = null;
                        expiry.setText(null);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        cardioImage.setVisibility(View.GONE);
        addc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected()) {
                    if (manualEnterValidationPasses()) {
                        attemptEnterManually();
                    }
                } else {
                    showConnectionError();
                }
            }
        });

        if (getIntent().hasExtra(CardDetectionActivity.EXTRA_PAYMENT_CARD)) {
            scanResult = getIntent().getParcelableExtra(CardDetectionActivity.EXTRA_PAYMENT_CARD);
            cardNo.setText(scanResult.cardNumber);
            cardName.setText(scanResult.cardholderName);
            if (scanResult.expiryMonth == 0) {
                expiry.setText("");
            } else {
                expiry.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
            }
            mCardType = scanResult.getCardType().toString();
            cardType.setText(scanResult.getCardType().toString());
            cardIcon.setVisibility(View.VISIBLE);
            cardNumberLabel.setVisibility(View.INVISIBLE);
            cardType.setVisibility(View.INVISIBLE);

            switch (scanResult.getCardType()) {
                case VISA: {
                    cardIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.visa));
                    break;
                }
                case MASTERCARD: {
                    cardIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mastercard));
                    break;
                }
                case AMEX: {
                    cardIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.amex));
                    break;
                }
                default: {
                    cardIcon.setVisibility(View.INVISIBLE);
                    cardNumberLabel.setVisibility(View.VISIBLE);
                    cardType.setVisibility(View.VISIBLE);
                    break;
                }
            }

            if (!unsupportedCardShown && BinkUtil.isBarclayCard(scanResult.cardNumber)) {
                showUnsupportedCard();
            }
        }


        if (getIntent().hasExtra(CardDetectionActivity.EXTRA_CAPTURED_CARD_IMAGE)) {
            cardImage = CardDetectionActivity.getCapturedCardImage(getIntent(), CardDetectionActivity.EXTRA_CAPTURED_CARD_IMAGE);
            cardioImage.setVisibility(View.VISIBLE);
            cardioImage.setImageBitmap(cardImage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.AddPaymentCard);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.info_btn_i)
    void onPaymentInfoClicked() {
        AddCardInfoDialogFragment.newInstance(
                AddCardInfoDialogFragment.paymentImages(),
                AddCardInfoDialogFragment.SECURITY,
                mainView.getHeight())
                .show(getSupportFragmentManager(), "");
    }

    private void showIsEmptyDialog() {
        showErrorDialog("All of the fields are required");
    }

    private boolean manualEnterValidationPasses() {
        if (TextUtils.isEmpty(cardName.getText().toString()) ||
                TextUtils.isEmpty(cardNo.getText().toString()) ||
                TextUtils.isEmpty(expiry.getText().toString())) {
            showIsEmptyDialog();
            return false;
        } else if (expiry.getText().toString().length() != 7) {
//                } else if(expiry.getText().toString().substring(3).length()!=4 ||
//                        expiry.getText().toString().substring(0,2).length()!=2){
            showErrorDialog("The expiry date has been entered incorrectly.");
            return false;
        }
        return true;
    }

    private void attemptEnterManually() {
        name = cardName.getText().toString();
        cardnum = cardNo.getText().toString();
//                cvv = cv2.getText().toString();
        exp = expiry.getText().toString();
        type = cardType.getText().toString();

        //check if they are amex or visa or master card
        if (cardnum.startsWith("34") || cardnum.startsWith("37") || cardnum.startsWith("4") || cardnum.startsWith("51") || cardnum.startsWith("52") || cardnum.startsWith("53") ||
                cardnum.startsWith("54") || cardnum.startsWith("55")) {

            View dialogLayout = LayoutInflater.from(this).inflate(R.layout.layout_payment_terms_dialog, null);
            TextView terms = (TextView) dialogLayout.findViewById(R.id.terms);
            terms.setLinksClickable(true);

            Linkify.addLinks(terms, Linkify.WEB_URLS);
            terms.setMovementMethod(LinkMovementMethod.getInstance());

            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(cardNo.getWindowToken(), 0);

            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle(getString(R.string.add_payment_terms_title))
                    .setView(dialogLayout)
                    .setNegativeButton(getString(R.string.add_payment_terms_disagree), null)
                    .setPositiveButton(getString(R.string.add_payment_terms_agree), (dialog, i) -> {
                        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                                .setTitle(getString(R.string.add_payment_terms_confirm_title))
                                .setNegativeButton(getString(R.string.add_payment_terms_confirm_no), null)
                                .setPositiveButton(getString(R.string.add_payment_terms_confirm_yes), (dialog2, i1) -> {
                                    addCardToSpreedly();
                                    if (BinkUtil.isBarclayCard(cardnum)) {
                                        tracker.trackEvent(Category.AddPaymentCard, Action.BarclayCard);
                                    }

                                    dialog2.dismiss();
                                }).show();

                        dialog.dismiss();
                    })
                    .show();

            tracker.trackScreen(Screen.PaymentTermsAndConditions);
        } else {

//only master card/ visa or amex is allowed at the moment
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Error")
                    .setMessage("Card type is not supported")
                    .setPositiveButton(R.string.alert_ok, null)
                    .show();
        }
    }

    private void addCardToSpreedly() {
        String month = exp.substring(0, 2);
        String year = exp.substring(3, 7);
        String fullName = name.toUpperCase();

        PaymentMethod paymentMethod = PaymentMethod.create(cardnum, fullName, month, year);
        SpreedlyPayload payload = SpreedlyPayload.create(paymentMethod);
        spreedlyApi.addCreditCard(payload)
                .compose(applySchedulers())
                .subscribe(spreedlyResult -> {
                    Transaction transaction = spreedlyResult.getTransaction();
                    if (transaction.succeeded()) {
                        Transaction.PaymentMethod method = transaction.getPaymentMethod();
                        addCardToBink(method.getToken(), method.getFingerprint());
                    } else {
                        showSpreedlyError();
                    }
                }, error -> {
                    error.printStackTrace();
                    showSpreedlyError();
                });
    }

    private void showSpreedlyError() {
        new AlertDialog.Builder(AddPaymentCardManuallyActivity.this, R.style.AlertDialogStyle)
                .setTitle("Error")
                .setMessage("Card type is invalid")
                .setPositiveButton(R.string.alert_ok, null)
                .show();
    }

    private void addCardToBink(String token, String fingerprint) {
        PaymentCardAccount card = new PaymentCardAccount();
        card.setNameOnCard(name);
        card.setExpiryMonth(exp.substring(0, 2));
        card.setExpiryYear(exp.substring(3, 7));
        card.setCurrencyCode("GBP");
        card.setToken(token);
        card.setFingerprint(fingerprint);
        card.setPanStart(cardnum.substring(0, 6));
        card.setPanEnd(cardnum.substring(cardnum.length() - 4, cardnum.length()));
        card.setCountry("England");
        card.setUser(1);
        card.setIssuer("2");

        if (!TextUtils.isEmpty(mCardType)) {
            card.setPaymentCardType(determineCardTypeAfterScan());
        } else {
            card.setPaymentCardType(determineCardTypeWithoutScan());
        }

        ProgressDialog dialog = new ProgressDialog(this, R.style.AlertDialogStyle);
        dialog.setMessage(getString(R.string.add_payment_adding_card));

        Subscription delayedProgressSubscription = Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(applySchedulers())
                .subscribe(l -> dialog.show());

        model.addPaymentCardAccount(card)
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    delayedProgressSubscription.unsubscribe();
                    dialog.dismiss();
                })
                .subscribe(account -> {
                    Intent data = new Intent();
                    data.putExtra(BinkIntents.EXTRA_PAYMENT_CARD_ACCOUNT, account);

                    setResult(PAYMENT_CARD_ADDED, data);
                    finish();
                }, error -> {
                    error.printStackTrace();

                    String message = getResources().getString(R.string.add_payment_invalid_details);

                    if (error.getMessage().contains("Forbidden")) {
                        message = getString(R.string.add_payment_card_exists);
                    }
                    new AlertDialog.Builder(AddPaymentCardManuallyActivity.this, R.style.AlertDialogStyle)
                            .setTitle(R.string.add_payment_error_title)
                            .setMessage(message)
                            .setPositiveButton(R.string.alert_ok, null)
                            .show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showUnsupportedCard() {
        unsupportedCardShown = true;
        cardNo.setTextColor(Color.RED);
        alert.setVisibility(View.VISIBLE);

        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.add_payment_unsupported_card_title)
                .setMessage(R.string.add_payment_unsupported_card)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.add_payment_unsupported_card_add, (dialog, i) -> dialog.dismiss())
                .show();
    }

    private void hideUnsupportedCard() {
        unsupportedCardShown = false;
        cardNo.setTextColor(Color.BLACK);
        alert.setVisibility(View.GONE);
    }

    /**
     * If it was manually entered, then this method must be used, which is not ideal.
     *
     * @return The card type as a String representing an Integer. See Constants.paymentcardtype for the mappings.
     */
    @NonNull
    private PaymentCardType determineCardTypeWithoutScan() {
        if (cardnum.startsWith("4")) {
            return PaymentCardType.VISA;
        }
        if (cardnum.startsWith("5")) {
            return PaymentCardType.MASTERCARD;
        }
        if (cardnum.startsWith("3")) {
            return PaymentCardType.AMEX;
        }

        return PaymentCardType.MASTERCARD;
    }

    /**
     * We only get the card type if the card was scanned. If it was manually entered, then another method must be used.
     *
     * @return The card type as a String representing an Integer. See Constants.paymentcardtype for the mappings.
     */
    @NonNull
    private PaymentCardType determineCardTypeAfterScan() {
        switch (mCardType.toLowerCase()) {
            case "visa":
                return PaymentCardType.VISA;
            case "mastercard":
                return PaymentCardType.MASTERCARD;
            case "amex":
                return PaymentCardType.AMEX;
            /*
            Default to Mastercard.
             */
            default:
                return PaymentCardType.MASTERCARD;
        }
    }
}