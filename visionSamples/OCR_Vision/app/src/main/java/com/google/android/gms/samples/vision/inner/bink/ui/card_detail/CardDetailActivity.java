package com.google.android.gms.samples.vision.inner.bink.ui.card_detail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loyaltyangels.bink.EmailUtils;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Action;
import com.loyaltyangels.bink.analytics.Category;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.Balance;
import com.loyaltyangels.bink.model.BarcodeType;
import com.loyaltyangels.bink.model.common.Image;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.scheme.Question;
import com.loyaltyangels.bink.model.scheme.QuestionType;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.model.scheme.SchemeOfferImage;
import com.loyaltyangels.bink.model.scheme.Tier;
import com.loyaltyangels.bink.model.scheme.Transaction;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.wallet.AddSchemeAccountActivity;
import com.loyaltyangels.bink.util.TintUtils;
import com.loyaltyangels.bink.util.UiUtil;
import com.trello.rxlifecycle.ActivityEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.card.payment.CardDetectionActivity;
import me.relex.circleindicator.CircleIndicator;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CardDetailActivity extends BaseActivity {

    public static final String EXTRA_SCHEME_ACCOUNT = "scheme_account";
    public static final String EXTRA_SCHEME = "scheme";
    public static final String EXTRA_DELETED_SCHEME_ACCOUNT_ID = "deleted_scheme_account_id";

    private static final int REQUEST_WRITE_PERMISSION = 186;

    public static final int RESULT_CARD_DELETED = 51;
    public static final int RESULT_LOGGED_IN = 52;

    private static final String TAG = "CARD DETAIL ACTIVITY";
    private static final int REQUEST_ADD_CARD = 31;
    private static final int REQUEST_JOIN_CARD = 32;
    private SchemeAccount detailCard;
    private Scheme scheme;
    private ArrayList<Transaction> transactions;

    public HashMap<String, String> loginCredentials = new HashMap<>();
    private Writer writer = null;
    private BarcodeFormat barcodeFormat = null;

    /**********
     * Card View Row 1
     ****************/
    @Nullable
    @BindView(R.id.cardImage)
    ImageView cardImage;

    @Nullable
    @BindView(R.id.points)
    TextView pointsText;
    @Nullable
    @BindView(R.id.smallIcon)
    ImageView binkIcon;
    @Nullable
    @BindView(R.id.emailOrNumber)
    TextView emailNumberText;
    @Nullable
    @BindView(R.id.description)
    TextView descriptionText;
    @Nullable
    @BindView(R.id.top_card_layout)
    LinearLayout topCardLayout;
    @Nullable
    @BindView(R.id.account)
    TextView accountLabel;
    @Nullable
    @BindView(R.id.infoButton)
    ImageButton infoImageButton;

    @Nullable
    @BindView(R.id.lower_layout_bar)
    RelativeLayout lowerLayoutBar;

    @Nullable
    @BindView(R.id.card_number_text)
    TextView cardNumberText;

    /***********
     * Link Question
     ***********************/
    @Nullable
    @BindView(R.id.form)
    LinearLayout formLayout;

    @Nullable
    @BindView(R.id.barcodeProgress)
    ContentLoadingProgressBar barcodeProgress;


    @Nullable
    @BindView(R.id.barcodeWideProgress)
    ContentLoadingProgressBar barcodeWideProgress;

    /************
     * Transactions Row 4
     **************/
    @Nullable
    @BindView(R.id.title)
    TextView noTransactionTextView;
    @Nullable
    @BindView(R.id.row_transactions)
    View transactionView;

    @Nullable
    @BindView(R.id.loading)
    ContentLoadingProgressBar progressBar;

    @Nullable
    @BindView(R.id.detailView)
    View detailView;

    @Nullable
    @BindView(R.id.appView)
    View appView;

    /*************
     * Questions
     *************/
    View questionsView;
    Button formLoginButton;


    /***********
     * Pre-Login
     ***************/
    @Nullable
    @BindView(R.id.row_pre)
    View preLoginLayout;
    @Nullable
    @BindView(R.id.login_prompt_text)
    TextView loginPromptText;
    @Nullable
    @BindView(R.id.forgotp)
    Button forgotPassButton;
    @Nullable
    @BindView(R.id.login)
    Button loginButton;
    @Nullable
    @BindView(R.id.web)
    Button websiteButton;

    @Nullable
    @BindView(R.id.btn_add_card)
    Button addCardButton;

    @Nullable
    @BindView(R.id.topLine)
    RelativeLayout topLine;

    /***********
     * Landscape View
     ***************/
    @Nullable
    @BindView(R.id.barcodeLarge)
    ImageView barcodeImageLarge;

    @Nullable
    @BindView(R.id.barcodeImage)
    ImageView barcodeImageView;

    private Bitmap largeBarcodeBitmap;

    private String barcodeFileName;

    private MenuItem deleteItem;
    private String balanceInfo = "";
    private String balanceLabel = "";
    private String points = "";

    private boolean barcodeFileExists = false;
    private File barcodeFile;
    private boolean barcodeGenerated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        transactions = new ArrayList<>();

        if (savedInstanceState != null) {
            largeBarcodeBitmap = savedInstanceState.getParcelable("mBitmapBig");
        }

        detailCard = getIntent().getParcelableExtra(EXTRA_SCHEME_ACCOUNT);
        scheme = getIntent().getParcelableExtra(EXTRA_SCHEME);
        barcodeFileName = getCacheDir() + "/" + scheme.getCompany() + ".jpg";
        barcodeFileExists = checkFile(barcodeFileName);
        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.SchemeAccountDetail, detailCard.getId());
    }


    private void updateUI() {

        if (toolbar != null) {
            TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        int orientation = getWindowManager().getDefaultDisplay().getOrientation();
        if (orientation != Surface.ROTATION_90 &&
                orientation != Surface.ROTATION_270) {
            if (transactionView != null) {
                transactionView.setVisibility(View.GONE);
                setUpCardView();
                if (isConnected()) {
                    showOffers();
                }
            } else {  // the screen may have started in landscape mode thus not triggering onConfigurationChange yet
                setupLandscapeView();
            }
        } else // screen may have started in landscape and onConfigurationChanged will not be called
        {
            if (detailCard.getBarcode() == null) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setupLandscapeView();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        deleteItem = menu.findItem(R.id.deleteItem);
        deleteItem.setOnMenuItemClickListener(menuItem -> {
            if (isConnected()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CardDetailActivity.this);
                builder.setMessage(getString(R.string.scheme_account_delete_message))
                        .setPositiveButton(R.string.alert_cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton(R.string.scheme_account_delete, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            deleteAccount();
                        }).create().show();
            } else {
                showConnectionError();
            }
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_card_detail);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tracker.trackScreen(Screen.FullscreenBarcode);
            if (detailCard.getBarcode() != null) {
                barcodeFileName = getCacheDir() + "/" + scheme.getCompany() + ".jpg";
                setupLandscapeView();
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            tracker.trackScreen(Screen.SchemeAccountDetail, detailCard.getId());
            transactionView.setVisibility(View.GONE);
            setUpCardView();
            if (isConnected()) {
                showOffers();
            }
        }
    }

    int activityResult = -1;

    @Override
    public void onBackPressed() {
        setResult(activityResult);
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_JOIN_CARD && resultCode == RESULT_OK) {
            resetFromJoin();
        }
    }

    private boolean checkFile(String filename) {
        barcodeFile = new File(filename);
        return barcodeFile.exists();
    }

    private void deleteAccount() {
        ProgressDialog dialog = new ProgressDialog(this, R.style.AlertDialogStyle);
        dialog.setMessage(getString(R.string.scheme_account_deleting));

        Subscription delayedProgressSubscription = Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(applySchedulers())
                .subscribe(l -> dialog.show());

        model.deleteSchemeAccount(detailCard.getId())
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    delayedProgressSubscription.unsubscribe();
                    dialog.dismiss();
                })
                .subscribe(schemeAccount -> {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_DELETED_SCHEME_ACCOUNT_ID, detailCard.getId());

                    setResult(RESULT_CARD_DELETED, data);
                    finish();
                }, Throwable::printStackTrace);
    }

    private void setupLandscapeView() {

        cardNumberText.setText(detailCard.getCardLabel());
        cardNumberText.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardNumberText.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cardNumberText.setLayoutParams(layoutParams);
        if (barcodeGenerated || barcodeFileExists && largeBarcodeBitmap != null) {
            barcodeWideProgress.hide();
            barcodeImageLarge.setImageBitmap(largeBarcodeBitmap);
        } else {
            loadBarcodes();
        }


    }

    private void showJoinLink() {
        lowerLayoutBar.setVisibility(View.INVISIBLE);
        detailView.setVisibility(View.VISIBLE);
        addCardButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Intent captureIntent = new Intent(CardDetailActivity.this, AddSchemeAccountActivity.class);
                                                 captureIntent.putExtra(AddSchemeAccountActivity.EXTRA_JOIN_SCHEME, scheme);
                                                 startActivityForResult(captureIntent, REQUEST_JOIN_CARD);
                                             }
                                         }
        );

        TextView terms = (TextView) findViewById(R.id.join_terms);
        terms.setText(scheme.getJoinTerms());

        if (!TextUtils.isEmpty(scheme.getJoinTerms())) {
            terms.setVisibility(View.VISIBLE);
        } else {
            terms.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(scheme.getJoinUrl())) {
            Button joinCardButton = (Button) findViewById(R.id.btn_join);
            joinCardButton.setVisibility(View.VISIBLE);
            joinCardButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      String url = scheme.getJoinUrl();

                                                      Intent joinUrlIntent = new Intent(Intent.ACTION_VIEW);
                                                      joinUrlIntent.setData(Uri.parse(url));
                                                      startActivity(joinUrlIntent);

                                                      tracker.trackScreen(Screen.WebView, url);
                                                  }
                                              }
            );
        }
    }

    private void schemeNotActive() {
        preLoginLayout.setVisibility(View.VISIBLE);
        transactionView.setVisibility(View.GONE);
        topLine.setVisibility(View.VISIBLE);

        forgotPassButton.setOnClickListener(v -> {
            try {
                String url = detailCard.getScheme().getForgottenPasswordUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                tracker.trackScreen(Screen.WebView, url);
            } catch (NullPointerException e) {
                Toast.makeText(CardDetailActivity.this, "No website available.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(v -> {
            questionsView = findViewById(R.id.row_linkquestion);

            Animation move = AnimationUtils.loadAnimation(this, R.anim.slide_left_right);
            move.setDuration(300);
            Animation move2 = AnimationUtils.loadAnimation(this, R.anim.slider);
            move2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    questionsView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            move2.setDuration(300);

            move2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    preLoginLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            preLoginLayout.startAnimation(move2);
            questionsView.startAnimation(move);
            //method used to add all the values to form
            buildForm();
            TextView linkAccountText = (TextView) findViewById(R.id.linkText);
            if (scheme.getLinkAccountText().length() > 0) {
                linkAccountText.setVisibility(View.VISIBLE);
                linkAccountText.setText(scheme.getLinkAccountText());
            } else {
                linkAccountText.setVisibility(View.GONE);
            }
            formLoginButton = (Button) findViewById(R.id.add_card_button);
            questionsView.setVisibility(View.VISIBLE);
            formLoginButton.setVisibility(View.VISIBLE);
            formLoginButton.setOnClickListener(view -> {
                if (isConnected()) {
                    view.bringToFront();
                    view.requestFocus();

                    boolean validForm = checkFormValues();
                    if (validForm) {
                        linkScheme();
                        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    progressBar.hide();
                    showConnectionError();

                }
            });
        });
    }

    LinearLayout.LayoutParams formLayoutParams;

    private void linkScheme() {

        formLoginButton.setVisibility(View.INVISIBLE);
        progressBar.show();
        model.linkScheme(detailCard.getId(), loginCredentials)
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    if (progressBar != null) {
                        progressBar.hide();
                    }
                })
                .subscribe(response -> {
                    /**
                     * Temporary solution to handle rotation changes. In the future we will not handle
                     * configuration changes.
                     */
                    if (formLayout == null) {
                        return;
                    }

                    if (response.getStatus().equals(1)) {
                        detailCard.setStatus(SchemeAccount.Status.ACTIVE);

                        formLayout.setVisibility(View.GONE);
                        questionsView.setVisibility(View.GONE);
                        detailCard.setBalance(response.getBalance());
                        if (scheme.hasTransactions()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (formLayout != null) {
                                        loadTransactions();
                                    }
                                }
                            }, 1500);
                        } else {
                            noTransactionTextView.setText(getResources().getString(R.string.scheme_account_transactions_unavailable));
                        }
                        setUpCardView();
                        activityResult = RESULT_LOGGED_IN;
                    } else {
                        formLoginButton.setVisibility(View.VISIBLE);
                        formLayout.setVisibility(View.VISIBLE);
                        if (detailCard.getStatus() == SchemeAccount.Status.JOIN) {
                            detailView.setVisibility(View.VISIBLE);
                        }
                        String errorMessage = response.getStatusName();

                        if (response.getStatusName().equalsIgnoreCase("tripped captcha")) {
                            errorMessage = getResources().getString(R.string.scheme_account_link_tripped_captcha, scheme.getName());
                        }

                        if (response.getStatusName().contains("too many retries")) {
                            errorMessage = getResources().getString(R.string.card_detail_login_error_message);
                        }


                        new AlertDialog.Builder(CardDetailActivity.this, R.style.AlertDialogStyle)
                                .setTitle("Error")
                                .setMessage(errorMessage) //display the error from the back-end back to the user
                                .setPositiveButton(R.string.alert_ok, null)
                                .show();
                    }
                }, error -> {
                    if (formLayout == null) {
                        return;
                    }

                    formLayout.setVisibility(View.VISIBLE);
                    formLoginButton.setVisibility(View.VISIBLE);
                    new AlertDialog.Builder(CardDetailActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage(R.string.scheme_account_link_error) //display the error from the back-end back to the user
                            .setPositiveButton(R.string.alert_ok, null)
                            .show();
                });
    }

    private boolean checkFormValues() {
        loginCredentials.clear();
        boolean validForm = true;
        List<Question> questions = scheme.getLinkQuestions();

        for (int z = 0; z < questions.size(); z++) {
            boolean validField = true;
            Question question = questions.get(z);
            TextInputLayout inputLayout = ((TextInputLayout) formLayout.getChildAt(z));
            inputLayout.setLayoutParams(formLayoutParams);
            inputLayout.setError(null);

            String value = inputLayout.getEditText().getText().toString();

            /**
             * If the type is null, skip this.
             */
            QuestionType type = question.getType();
            if (type == null) {
                continue;
            }

            /**
             * Always invalid if empty
             */
            if (TextUtils.isEmpty(value)) {
                validField = false;
            }

            if (type == QuestionType.Email) {
                if (!EmailUtils.checkifEmail(value)) {
                    validField = false;
                }
            }

            /**
             * Add to credentials if valid, otherwise show an error for this field.
             */
            if (validField) {
                loginCredentials.put(type.getName(), value);
            } else {
                inputLayout.setError(String.format("%s%s", getString(R.string.card_detail_incorrect_error), inputLayout.getHint()));
            }

            validForm = validForm && validField;
        }

        return validForm;
    }

    private void buildForm() {
        formLayout.removeAllViews();

        for (int i = 0; i <= scheme.getLinkQuestions().size() - 1; i++) {
            Question question = scheme.getLinkQuestions().get(i);
            addFormField(question);
        }
    }

    private int sId = 0;

    private void clearErrors() {
        for (int i = 0; i < formLayout.getChildCount(); i++) {
            TextInputLayout inputLayout = ((TextInputLayout) formLayout.getChildAt(i));
            inputLayout.setErrorEnabled(false);
        }
    }

    private int id() {
        return sId++;
    }

    private void addFormField(Question question) {
        TextInputLayout fieldTextInputLayout = new TextInputLayout(this);
        EditText questionInputEditText = new TextInputEditText(this);
        fieldTextInputLayout.addView(questionInputEditText);
        fieldTextInputLayout.setErrorEnabled(true);
        fieldTextInputLayout.setHint(question.getLabel());
        questionInputEditText.setId(id());
        questionInputEditText.setSingleLine(true);

        if (question.getType() != null) {
            questionInputEditText.setInputType(UiUtil.getInputTypeForQuestionType(question.getType()));
        }

        formLayout.addView(fieldTextInputLayout);
        if (formLayoutParams == null) {
            formLayoutParams = (LinearLayout.LayoutParams) fieldTextInputLayout.getLayoutParams();
            formLayoutParams.topMargin = 8;
        }
        fieldTextInputLayout.setLayoutParams(formLayoutParams);


    }

    private void setUpCardView() {
        if (toolbar != null) {
            TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (!TextUtils.isEmpty(scheme.getPlayStoreUrl()) || !TextUtils.isEmpty(scheme.getAndroidAppId()) || !TextUtils.isEmpty(scheme.getCompanyUrl())) {
            appView.setVisibility(View.VISIBLE);
            setUpAppLinks();
        } else {
            appView.setVisibility(View.GONE);
        }

        infoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpPopup();
            }
        });

        if (detailCard.getCardLabel() != null) {
            String target = "Card No";
            Question question = scheme.getManualQuestion();

            if (question != null) {
                if (question.getType() == QuestionType.Email || question.getType() == QuestionType.Username) {
                    target = "Email/Username";
                }
            }

            emailNumberText.setText(target + ": " + detailCard.getCardLabel());
            cardNumberText.setText(detailCard.getCardLabel());
        }

        // check for barcode and generate it
        if (detailCard.getBarcode() != null && detailCard.getScheme().getBarcodeType() != null) {
            showUseInfo(true);
            if (largeBarcodeBitmap == null) {
                loadBarcodes();
            }
        } else {
            showUseInfo(false);
            barcodeImageView.setVisibility(View.GONE);
        }
        Image hero = detailCard.findImage(ImageType.HERO);
        cardImage.setBackgroundColor(Color.parseColor(scheme.getColour()));

        if (accountLabel != null) {
            accountLabel.setText(scheme.getCompany());
            accountLabel.setVisibility(View.VISIBLE);
        }

        if (hero != null) {
            Glide.with(this)
                    .load(hero.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(new ColorDrawable(Color.parseColor(scheme.getColour())))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (accountLabel != null) {
                                accountLabel.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .into(cardImage);
        }


        if (detailCard.getStatus() == SchemeAccount.Status.JOIN) {
            setJoinCurveView();
            showJoinLink();
        } else {
            if (scheme.getTier() == Tier.TIER_1) {
                binkIcon.setVisibility(View.VISIBLE);
                binkIcon.setImageDrawable(getResources().getDrawable(R.drawable.pinktick));
            } else if (scheme.getTier() == Tier.TIER_2) {
                binkIcon.setVisibility(View.VISIBLE);
                binkIcon.setImageDrawable(getResources().getDrawable(R.drawable.barcode));
            } else {
                binkIcon.setVisibility(View.GONE);
            }
            if (detailCard.getStatus() != SchemeAccount.Status.ACTIVE) {
                schemeNotActive();
                forgotPassButton.setVisibility(TextUtils.isEmpty(scheme.getForgottenPasswordUrl()) ? View.GONE : View.VISIBLE);
                if (scheme.hasPoints()) {
                    pointsText.setText(R.string.login_prompt);
                    loginPromptText.setText(String.format(getString(R.string.scheme_account_link_prompt), detailCard.getScheme().getName()));
                } else {
                    pointsText.setText(" ");
                    topLine.setVisibility(View.GONE);
                    loginButton.setVisibility(View.GONE);
                    loginPromptText.setText(String.format(getString(R.string.scheme_account_login_unsupported), detailCard.getScheme().getName()));

                }
            } else {
                updateBalanceViews();
                if (transactionView != null) {  // this accounts for rotation before getting transactions completes
                    if (scheme.hasTransactions()) {
                        if (transactions.size() <= 1) {
                            loadTransactions();
                        } else {
                            showTransactions();
                            transactionView.setVisibility(View.VISIBLE);
                            noTransactionTextView.setText("Transactions");
                        }
                    } else {
                        // scheme is active but no transactions
                        transactionView.setVisibility(View.VISIBLE);
                        noTransactionTextView.setText(R.string.scheme_account_transactions_unavailable);
                    }
                }
            }
        }
    }

    private void resetFromJoin() {
        pointsText.setText(R.string.login_prompt);
        pointsText.setBackgroundDrawable(getResources().getDrawable(R.drawable.curve));
        binkIcon.setVisibility(View.GONE);
        accountLabel.setVisibility(View.VISIBLE);
        pointsText.setTextColor(Color.BLACK);
        lowerLayoutBar.setVisibility(View.VISIBLE);
        detailView.setVisibility(View.GONE);
        emailNumberText.setVisibility(View.VISIBLE);
        cardNumberText.setVisibility(View.VISIBLE);
        barcodeImageView.setVisibility(View.VISIBLE);
        activityResult = CardDetectionActivity.RESULT_CODE_ADDED_CARD;

        model.getSchemeAccount(detailCard.getId())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, " Updating UI ");
                        schemeNotActive();
                        updateUI();
                    }
                })
                .subscribe(new Action1<SchemeAccount>() {
                    @Override
                    public void call(SchemeAccount schemeAccount) {
                        Log.e(TAG, " Got fresh card ");
                        synchronized (detailCard) {
                            detailCard = schemeAccount;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

        Button joinCardButton = (Button) findViewById(R.id.btn_join);
        if (joinCardButton != null) {
            joinCardButton.setVisibility(View.GONE);
        }
    }

    private void updateBalanceViews() {
        Balance balance = detailCard.getBalance();
        balanceLabel = UiUtil.formatSchemePointsLabel(appConfig, balance, scheme);
        pointsText.setText(balanceLabel);
        points = UiUtil.formatPoints(detailCard.getBalance(), scheme);
        if (!TextUtils.isEmpty(detailCard.getBalance().getValueLabel())) {
            balanceInfo = " worth " + detailCard.getBalance().getValueLabel();
        }
        emailNumberText.setText(emailNumberText.getText().toString() + "\n" + String.valueOf((int) balance.getPoints()) + " " + scheme.getPointName() + balanceInfo);
    }

    private void showUseInfo(boolean visible) {
        if (visible) {
            infoImageButton.setVisibility(View.VISIBLE);
        } else {
            infoImageButton.setVisibility(View.GONE);
        }
    }

    private void setJoinCurveView() {
        pointsText.setText(R.string.join_prompt);
        pointsText.setBackgroundDrawable(getResources().getDrawable(R.drawable.pinkcurve));
        binkIcon.setVisibility(View.VISIBLE);
        binkIcon.setImageDrawable(getResources().getDrawable(R.drawable.join_action));
        pointsText.setTextColor(Color.WHITE);
    }

    private void loadTransactions() {
        progressBar.show();

        if (isConnected()) {
            model.getTransactionsForSchemeAccount(detailCard.getId())
                    .compose(applySchedulers())
                    .doOnTerminate(() -> {
                        progressBar.hide();
                    })
                    .subscribe(transactions -> {
                        this.transactions = transactions;

                        String headerReference = getString(R.string.scheme_account_transaction_reference);
                        String headerDate = getString(R.string.scheme_account_transaction_date);
                        String headerPoints = getString(R.string.scheme_account_transaction_points);
                        Transaction header = new Transaction();
                        header.setDescription(headerReference);
                        header.setDate(headerDate);
                        header.setPoints(headerPoints);
                        transactions.add(0, header);

                        if (transactionView != null) { // not in landscape mode
                            if (transactions.size() > 1) {
                                showTransactions();
                                transactionView.setVisibility(View.VISIBLE);
                                updateBalanceViews();
                            } else {
                                transactionView.setVisibility(View.VISIBLE);
                                detailView.setVisibility(View.GONE);
                                noTransactionTextView.setVisibility(View.VISIBLE);
                                noTransactionTextView.setText(getResources().getString(R.string.scheme_account_empty_transactions, detailCard.getScheme().getName()));
                            }
                        }
                    }, error -> {
                        error.printStackTrace();

                        if (transactionView != null) {
                            transactionView.setVisibility(View.GONE);
                        }
                        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                                .setMessage(R.string.scheme_account_transactions_error)
                                .setPositiveButton(R.string.alert_ok, null)
                                .show();
                    });
        } else {
            progressBar.hide();
            transactionView.setVisibility(View.VISIBLE);
            detailView.setVisibility(View.GONE);
            noTransactionTextView.setVisibility(View.VISIBLE);
            noTransactionTextView.setText(getResources().getString(R.string.scheme_account_empty_transactions, detailCard.getScheme().getName()));

            Observable.timer(1800, TimeUnit.MILLISECONDS)
                    .compose(bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showConnectionError();
                                }
                            });
                        }
                    });
        }
    }

    private void showTransactions() {

        transactionView.setVisibility(View.VISIBLE);
        if (transactions.size() > 1) {
            ListView lv = (ListView) findViewById(R.id.listView2);
            lv.setVisibility(View.VISIBLE);
            TransactionsAdapter tAdapter = new TransactionsAdapter(this, transactions);
            lv.setFocusableInTouchMode(false);
            lv.setFocusable(false);
            lv.setAdapter(tAdapter);
        } else {
            noTransactionTextView.setVisibility(View.VISIBLE);
            noTransactionTextView.setText(R.string.scheme_account_empty_transactions);
            addCardButton.setVisibility(View.GONE);
        }

        /**
         * Show today as we are not caching transactions yet.
         */
        SimpleDateFormat lastUpdatedFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date lastUpdatedDate = new Date();

        TextView lastUpdated = (TextView) findViewById(R.id.last_updated);
        lastUpdated.setText(getString(R.string.scheme_account_last_updated, lastUpdatedFormat.format(lastUpdatedDate)));
    }

    private void showOffers() {
        if (detailCard.getPersonalisedOfferImages() != null) {
            if (detailCard.getPersonalisedOfferImages().size() > 0) {
                View v4 = findViewById(R.id.row_swipeable_personal);
                v4.setVisibility(View.VISIBLE);
                CircleIndicator indicator = (CircleIndicator) v4.findViewById(R.id.indicator);
                ViewPager viewPager = (ViewPager) v4.findViewById(R.id.viewpager);

                List<SchemeOfferImage> tempList = detailCard.getPersonalisedOfferImages();
                Collections.sort(tempList, new Comparator<SchemeOfferImage>() {
                    @Override
                    public int compare(SchemeOfferImage schemeOfferImage, SchemeOfferImage t1) {
                        return schemeOfferImage.getOrder() - t1.getOrder();
                    }
                });
                viewPager.setAdapter(new OffersAdapter(this, tempList));
                viewPager.setPageMargin(40);
                viewPager.setOffscreenPageLimit(2);
                indicator.setViewPager(viewPager);
            }
        }
        if (detailCard.getGenericOfferImages() != null) {
            if (detailCard.getGenericOfferImages().size() > 0) {
                View v3 = findViewById(R.id.row_swipeable);
                v3.setVisibility(View.VISIBLE);
                CircleIndicator indicator = (CircleIndicator) v3.findViewById(R.id.indicator);
                ViewPager viewPager = (ViewPager) v3.findViewById(R.id.viewpager);
                List<SchemeOfferImage> tempList = detailCard.getGenericOfferImages();
                Collections.sort(tempList, new Comparator<SchemeOfferImage>() {
                    @Override
                    public int compare(SchemeOfferImage schemeOfferImage, SchemeOfferImage t1) {
                        return schemeOfferImage.getOrder() - t1.getOrder();
                    }
                });
                viewPager.setAdapter(new OffersAdapter(this, tempList));
                viewPager.setPageMargin(40);
                viewPager.setOffscreenPageLimit(2);
                indicator.setViewPager(viewPager);
            }
        }
    }

    private void setUpAppLinks() {
        if (!TextUtils.isEmpty(scheme.getAndroidAppId())) {
            Button appButton = (Button) findViewById(R.id.btn_android_app);
            appButton.setVisibility(View.VISIBLE);
            appButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    tracker.trackEvent(Category.ButtonPress, Action.LaunchApp);

                    Intent intent = getPackageManager().getLaunchIntentForPackage(scheme.getAndroidAppId());
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        String storeUrl = scheme.getPlayStoreUrl();

                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(storeUrl));
                        startActivity(intent);

                        tracker.trackScreen(Screen.WebView, storeUrl);
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(scheme.getCompanyUrl())) {
            websiteButton.setVisibility(View.VISIBLE);
            websiteButton.setOnClickListener(v -> {
                String url = scheme.getCompanyUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                if (getPackageManager().resolveActivity(intent, 0) != null) {
                    startActivity(intent);
                    tracker.trackScreen(Screen.WebView, url);
                } else {
                    Log.e(TAG, "Error trying to resolve Activity for ACTION_VIEW with URI: " + url);
                }
            });
        } else {
            websiteButton.setVisibility(View.GONE);
        }
    }

    public void showHelpPopup() {
        new UseTill_InfoDialogFragment().show(getSupportFragmentManager(), "");
    }

    public void onCardClick(View view) {
        if (barcodeGenerated || barcodeFileExists) {
            if (barcodeImageView.getDrawable() == null) {
                barcodeImageView.setImageBitmap(largeBarcodeBitmap);
            }
            flipCard();
        } else {
            if (detailCard.getBarcode() != null) {
                barcodeProgress.show();
                flipCard();
            }
        }
    }

    private void flipCard() {
        View rootLayout = topCardLayout.findViewById(R.id.flip);
        View cardFace = topCardLayout.findViewById(R.id.card_view_layout);
        View cardBack = topCardLayout.findViewById(R.id.barcode_view_layout);

        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);
        if (cardFace.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }
        rootLayout.startAnimation(flipAnimation);
    }

    private void loadBarcodes() {
        if (barcodeFileExists) {
            largeBarcodeBitmap = BitmapFactory.decodeFile(barcodeFileName);
            if (barcodeImageLarge != null) {
                barcodeWideProgress.hide();
                barcodeImageLarge.setImageBitmap(BitmapFactory.decodeFile(barcodeFileName));
            } else {
                barcodeImageView.setImageBitmap(BitmapFactory.decodeFile(barcodeFileName));
            }
        } else {
            Observable.create(new Observable.OnSubscribe<byte[]>() {
                @Override
                public void call(Subscriber<? super byte[]> subscriber) {
                    subscriber.onNext(generateBarcode());
                }
            })
                    .compose(applySchedulers())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe((byte[] bytes) -> {
                        if (barcodeImageLarge != null) {
                            Glide.with(CardDetailActivity.this)
                                    .load(bytes)
                                    .asBitmap()
                                    .listener(new RequestListener<byte[], Bitmap>() {
                                        @Override
                                        public boolean onException(Exception e, byte[] model, Target<Bitmap> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, byte[] model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            updateBitmap(resource);
                                            barcodeWideProgress.hide();
                                            return false;
                                        }
                                    })
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(barcodeImageLarge);

                        } else {
                            Glide.with(CardDetailActivity.this)
                                    .load(bytes)
                                    .asBitmap()
                                    .listener(new RequestListener<byte[], Bitmap>() {
                                        @Override
                                        public boolean onException(Exception e, byte[] model, Target<Bitmap> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, byte[] model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            updateBitmap(resource);
                                            barcodeProgress.hide();
                                            return false;
                                        }
                                    })
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(barcodeImageView);
                        }

                    }, throwable -> {
                        throwable.printStackTrace();
                    });
        }
    }

    private void updateBitmap(Bitmap resource) {
        barcodeGenerated = true;
        largeBarcodeBitmap = resource;
        barcodeFileName = getCacheDir() + "/barcodes/" + scheme.getCompany() + ".jpg";
        File newImage = new File(barcodeFileName);
        try {
            FileOutputStream fos = new FileOutputStream(newImage);
            resource.compress(Bitmap.CompressFormat.JPEG, 100, fos); // important for low end devices to save it lossless
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        barcodeFileExists = checkFile(barcodeFileName);
    }

    private byte[] generateBarcode() {
        switch (detailCard.getScheme().getBarcodeType()) {
            case CODE_128: {
                barcodeFormat = BarcodeFormat.CODE_128;
                writer = new Code128Writer();
                break;
            }
            case QR_CODE: {
                barcodeFormat = BarcodeFormat.QR_CODE;
                writer = new QRCodeWriter();
                break;
            }
            case AZTEC: {
                barcodeFormat = BarcodeFormat.AZTEC;
                writer = new AztecWriter();
                break;
            }
            case PDF_417: {
                barcodeFormat = BarcodeFormat.PDF_417;
                writer = new PDF417Writer();
                break;
            }
            case EAN_13: {
                barcodeFormat = BarcodeFormat.EAN_13;
                writer = new EAN13Writer();
                break;
            }
            case DATA_MATRIX: {
                barcodeFormat = BarcodeFormat.DATA_MATRIX;
                writer = new DataMatrixWriter();
                break;
            }
            case ITF: {
                barcodeFormat = BarcodeFormat.ITF;
                writer = new ITFWriter();
                break;
            }
            case CODE_39: {
                barcodeFormat = BarcodeFormat.CODE_39;
                writer = new Code39Writer();
                break;
            }
            default: {
                barcodeFormat = BarcodeFormat.CODE_128;
                writer = new Code128Writer();
                break;
            }
        }

        try {
            if (detailCard.getScheme().getBarcodeType() == BarcodeType.EAN_13 && detailCard.getBarcode().length() != 13) { // this check is an overkill but we'll eliminate it when we redo this activity
                        /*
                        If the barcode data is the incorrect size for its type, then prevent rotation of the screen.
                         */
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                BitMatrix bm = writer.encode(detailCard.getBarcode(), barcodeFormat, 1200, 550);
                largeBarcodeBitmap = Bitmap.createBitmap(1200, 550, Bitmap.Config.ARGB_8888);
                int width = barcodeFormat == BarcodeFormat.PDF_417 ? bm.getWidth() : 1200;
                int height = barcodeFormat == BarcodeFormat.PDF_417 ? bm.getHeight() : 550;
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        largeBarcodeBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                largeBarcodeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, output);

                return output.toByteArray();
            }
        } catch (WriterException | RuntimeException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}