package com.google.android.gms.samples.vision.inner.bink.ui.wallet.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.crashlytics.android.Crashlytics;
import com.loyaltyangels.bink.ApptentiveUtils;
import com.loyaltyangels.bink.BuildConfig;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.ScanCardIntents;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.common.BinkIntents;
import com.loyaltyangels.bink.model.common.Account;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.ui.card_detail.CardDetailActivity;
import com.loyaltyangels.bink.ui.card_detail.PayDetailActivity;
import com.loyaltyangels.bink.ui.wallet.AddPaymentCardManuallyActivity;
import com.loyaltyangels.bink.ui.wallet.AddSchemeAccountActivity;
import com.loyaltyangels.bink.ui.wallet.WalletItemAnimator;
import com.loyaltyangels.bink.ui.wallet.adapter.WalletAdapter;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.card.payment.CardDetectionActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class WalletFragment extends BaseFragment {

    private static final String TAG = WalletFragment.class.getSimpleName();

    private static final int REQUEST_SCAN_PAYMENT_CARD = 0;
    private static final int REQUEST_ADD_SCHEME_ACCOUNT = 1;
    private static final int REQUEST_SCHEME_ACCOUNT_DETAIL = 2;
    private static final int REQUEST_PAYMENT_CARD_DETAIL = 3;

    public interface Listener {
        void onResetSearch();
    }

    @BindView(R.id.wallet_view)
    RecyclerView walletRecyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.empty_wallet)
    ViewGroup emptyWallet;

    @BindView(R.id.add_payment_card_layout)
    ViewGroup addPaymentCardLayout;

    @BindView(R.id.add_loyalty_card_layout)
    ViewGroup addLoyaltyCardLayout;

    @BindView(R.id.logo)
    View logo;

    @BindColor(R.color.pink)
    int pink;

    private WalletAdapter walletAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<Scheme> schemes;
    private Subscription orderSubscription;
    private Listener listener;
    private int cachedQueryLength;

    private WalletAdapter.Listener walletAdapterListener = new WalletAdapter.Listener() {
        @Override
        public void onSchemeClicked(SchemeAccount schemeAccount, Scheme scheme) {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra(CardDetailActivity.EXTRA_SCHEME_ACCOUNT, schemeAccount);
            intent.putExtra(CardDetailActivity.EXTRA_SCHEME, scheme);
            startActivityForResult(intent, REQUEST_SCHEME_ACCOUNT_DETAIL);
        }

        @Override
        public void onPaymentCardClicked(PaymentCardAccount paymentCardAccount) {
            Intent intent = new Intent(getActivity(), PayDetailActivity.class);
            intent.putExtra(PayDetailActivity.EXTRA_PAYMENT_CARD_ACCOUNT, paymentCardAccount);
            startActivityForResult(intent, REQUEST_PAYMENT_CARD_DETAIL);
        }

        @Override
        public void onUpdateOrders(List<Account> accounts) {
            updateOrders(accounts);
        }

        @Override
        public void onHideEmptyState() {
            Log.d(TAG, "onHideEmptyState() called");
            if (emptyWallet.getVisibility() != View.GONE) {
                hideEmptyWalletLayout();
                addLoyaltyCardLayout.setVisibility(View.GONE);
                addPaymentCardLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onShowWalletEmpty() {
            Log.d(TAG, "onShowWalletEmpty() called");
            if (emptyWallet.getVisibility() != View.VISIBLE ||
                    addLoyaltyCardLayout.getVisibility() != View.VISIBLE || addPaymentCardLayout.getVisibility() != View.VISIBLE) {
                showEmptyWalletLayout();
                addLoyaltyCardLayout.setVisibility(View.VISIBLE);
                addPaymentCardLayout.setVisibility(View.VISIBLE);
                logo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onShowNoLoyaltyCards() {
            Log.d(TAG, "onShowNoLoyaltyCards() called");
            if (addLoyaltyCardLayout.getVisibility() != View.VISIBLE || addPaymentCardLayout.getVisibility() != View.GONE) {
                showEmptyWalletLayout();
                addLoyaltyCardLayout.setVisibility(View.VISIBLE);
                addPaymentCardLayout.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
            }
        }

        @Override
        public void onShowNoPaymentCards() {
            Log.d(TAG, "onShowNoPaymentCards() called");
            if (addLoyaltyCardLayout.getVisibility() != View.GONE || addPaymentCardLayout.getVisibility() != View.VISIBLE) {
                showEmptyWalletLayout();
                addLoyaltyCardLayout.setVisibility(View.GONE);
                addPaymentCardLayout.setVisibility(View.VISIBLE);
                logo.setVisibility(View.GONE);
            }
        }
    };

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_wallet;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        walletRecyclerView.setLayoutManager(layoutManager);
        walletRecyclerView.setItemAnimator(new WalletItemAnimator());

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        refreshLayout.setOnRefreshListener(() -> {
            if (isConnected()) {
                refreshLayout.setRefreshing(true);
                refreshWallet();
            } else {
                ((BaseActivity) getActivity()).showConnectionError();
            }
        });

        getActivityComponent().inject(this);
        Glide.get(getContext()).setMemoryCategory(MemoryCategory.HIGH);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.show();

        loadInitialData();
        walletAdapter = new WalletAdapter(appConfig);

        walletRecyclerView.setAdapter(walletAdapter);

        walletAdapter.setListener(walletAdapterListener);
    }

    private void loadInitialData() {
        Observable<Wallet> walletObservable = model.getWallet();
        Observable<ArrayList<Scheme>> schemesObservable = model.getSchemes();

        Observable.zip(walletObservable, schemesObservable, Pair::create)
                .compose(applySchedulersForEvent(FragmentEvent.DESTROY_VIEW))
                .doOnTerminate(() -> progressDialog.dismiss())
                .subscribe(data -> {
                    progressDialog.hide();
                    Wallet wallet = data.first;
                    schemes = data.second;

                    walletAdapter.setWallet(wallet);
                    walletAdapter.setSchemes(schemes);
                    walletAdapter.notifyDataSetChanged();
                }, error -> {
                    progressDialog.hide();
                    showLoadError();
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Wallet);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_SCHEME_ACCOUNT && resultCode == Activity.RESULT_OK) {
            ApptentiveUtils.addLoyaltyScheme(getContext());
            SchemeAccount schemeAccount = data.getParcelableExtra(BinkIntents.EXTRA_SCHEME_ACCOUNT);
            if (schemeAccount != null) {
                updateAccount(schemeAccount, true);
            }
        } else {

            switch (requestCode) {
                case REQUEST_SCAN_PAYMENT_CARD:
                case REQUEST_ADD_SCHEME_ACCOUNT:
                    if (resultCode == AddPaymentCardManuallyActivity.PAYMENT_CARD_ADDED ||
                            resultCode == CardDetectionActivity.RESULT_CODE_ADDED_CARD) {
                        ApptentiveUtils.addPaymentCard(getContext());

                        PaymentCardAccount paymentCard = data.getParcelableExtra(BinkIntents.EXTRA_PAYMENT_CARD_ACCOUNT);
                        if (paymentCard != null) {
                            updateAccount(paymentCard, true);
                        }
                    } else {
                        ApptentiveUtils.addLoyaltyScheme(getContext());

                        /**
                         * Temporary fix to prevent cases where data is returned null. The underlying problem has not
                         * been found.
                         */
                        if (data != null) {
                            SchemeAccount schemeAccount = data.getParcelableExtra(BinkIntents.EXTRA_SCHEME_ACCOUNT);
                            if (schemeAccount != null) {
                                updateAccount(schemeAccount, true);
                            }
                        } else {
                            if (!BuildConfig.DEBUG) {
                                Crashlytics.log(Log.ERROR, TAG, "data is null. requestCode: " + requestCode + ", resultCode: " + resultCode);
                            }

                            refreshWallet();
                        }
                    }
                    break;
                case REQUEST_PAYMENT_CARD_DETAIL:
                    resetSearch();
                    if (resultCode == PayDetailActivity.RESULT_PAYMENT_CARD_DELETED) {
                        String deletedId = data.getStringExtra(PayDetailActivity.EXTRA_DELETED_ACCOUNT_ID);
                        deleteAccount(deletedId, true);
                    }
                    break;
                case REQUEST_SCHEME_ACCOUNT_DETAIL:
                    resetSearch();
                    if (resultCode == CardDetailActivity.RESULT_CARD_DELETED) {
                        String deletedId = data.getStringExtra(CardDetailActivity.EXTRA_DELETED_SCHEME_ACCOUNT_ID);
                        deleteAccount(deletedId, true);

                    } else if (resultCode == CardDetailActivity.RESULT_LOGGED_IN ||
                            resultCode == AddSchemeAccountActivity.RESULT_CARD_SELECTED ||
                            resultCode == CardDetectionActivity.RESULT_CODE_ADDED_CARD) {
                        if (isConnected()) {
                            refreshWallet();
                        }
                    }
                    break;
            }
        }
    }

    @OnTouch(R.id.wallet_view)
    boolean onWalletTouched(MotionEvent event) {
        if (emptyWallet.getVisibility() == View.VISIBLE) {
            RecyclerView.ViewHolder firstItem = walletRecyclerView.findViewHolderForAdapterPosition(0);
            if (firstItem != null) {
                float eventTop = event.getY();

                if (eventTop < firstItem.itemView.getTop()) {
                    emptyWallet.dispatchTouchEvent(event);
                }
            } else {
                emptyWallet.dispatchTouchEvent(event);
            }
        } else if (walletAdapter.getItemCount() == 0) {
            emptyWallet.dispatchTouchEvent(event);
        }

        return false;
    }

    @OnClick(R.id.add_payment_card_layout)
    void onAddPaymentClicked() {
        Intent intent = ScanCardIntents.scanPaymentCard(getContext());
        startActivityForResult(intent, REQUEST_SCAN_PAYMENT_CARD);
    }

    @OnClick(R.id.add_loyalty_card_layout)
    void onAddLoyaltyClicked() {
        showAddSchemeAccount();
    }

    @OnClick(R.id.fab)
    void onAddCardClicked() {
        if (((BaseActivity) getActivity()).isConnected()) {
            AddCardsDialogFragment dialog = AddCardsDialogFragment.newInstance();
            dialog.setListener(new AddCardsDialogFragment.Listener() {
                @Override
                public void onAddLoyaltyCard() {
                    showAddSchemeAccount();
                }

                @Override
                public void onAddPaymentCard() {
                    Intent intent = ScanCardIntents.scanPaymentCard(getContext());
                    startActivityForResult(intent, REQUEST_SCAN_PAYMENT_CARD);
                }
            });
            dialog.show(getFragmentManager(), "");

            tracker.trackScreen(Screen.CardChoice);
        } else {
            ((BaseActivity) getActivity()).showConnectionError();
        }
    }

    @OnClick(R.id.loyalty_info)
    void onLoyaltyInfoClicked() {
        AddCardInfoDialogFragment.newInstance(
                AddCardInfoDialogFragment.loyaltyImages(),
                AddCardInfoDialogFragment.LOYALTY,
                getView().getHeight())
                .show(getFragmentManager(), "");
    }

    @OnClick(R.id.payment_info)
    void onPaymentInfoClicked() {
        AddCardInfoDialogFragment.newInstance(
                AddCardInfoDialogFragment.paymentImages(),
                AddCardInfoDialogFragment.PAYMENT,
                getView().getHeight())
                .show(getFragmentManager(), "");
    }

    private void showAddSchemeAccount() {
        Intent intent = new Intent(getContext(), AddSchemeAccountActivity.class);
        startActivityForResult(intent, REQUEST_ADD_SCHEME_ACCOUNT);
    }

    private void deleteAccount(String accountId, boolean delay) {
        long delayTime = delay ? 350 : 0;

        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    walletAdapter.deleteAccountItem(accountId);
                });
    }

    private void updateAccount(Account account, boolean delay) {
        long delayTime = delay ? 350 : 0;

        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    walletAdapter.updateAccountItem(account);
                    walletRecyclerView.smoothScrollToPosition(Math.max(0, account.getOrder() - 1));
                });
    }

    private void refreshWallet() {
        if (cachedQueryLength > 0) {
            resetSearch();
        }

        model.refreshWallet()
                .compose(applySchedulersForEvent(FragmentEvent.DESTROY))
                .doOnTerminate(() -> refreshLayout.setRefreshing(false))
                .subscribe(wallet -> {
                    walletAdapter.setWallet(wallet);
                    walletAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Wallet Refreshed - " + wallet.getWalletSize() + " cards");
                }, Throwable::printStackTrace);

        if (schemes == null) {
            model.getSchemes()
                    .compose(applySchedulersForEvent(FragmentEvent.DESTROY))
                    .subscribe(schemes -> {
                        this.schemes = schemes;
                        walletAdapter.setSchemes(schemes);
                        walletAdapter.notifyDataSetChanged();
                    }, error -> {
                        showLoadError();
                    });
        }
    }

    private void updateOrders(@NonNull List<Account> accounts) {
        if (orderSubscription != null) {
            orderSubscription.unsubscribe();
        }

        orderSubscription = model.updateOrdersFromAccounts(accounts)
                .delaySubscription(1, TimeUnit.SECONDS)
                .compose(applySchedulersForEvent(FragmentEvent.DESTROY))
                .subscribe(result -> {
                    Log.e(TAG, "Order updated");
                }, error -> {
                    showLoadError();
                    Log.e(TAG, error.getMessage());
                });
    }


    private void showEmptyWalletLayout() {
        emptyWallet.setVisibility(View.VISIBLE);
        emptyWallet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: Showing empty wallet with height: " + emptyWallet.getHeight());
                emptyWallet.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                walletAdapter.setTopMarginForFirstItem(emptyWallet.getHeight());
                walletAdapter.notifyDataSetChanged();
            }
        });
    }

    private void hideEmptyWalletLayout() {
        emptyWallet.setVisibility(View.GONE);
        walletAdapter.setTopMarginForFirstItem(0);
        walletAdapter.notifyDataSetChanged();
    }

    private void showLoadError() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.wallet_loading_error)
                .setPositiveButton(R.string.alert_ok, null)
                .show();
    }

    public void resetSearch() {
        walletAdapter.reset();
        cachedQueryLength = 0;
        listener.onResetSearch();
    }

    public void filter(String queryText) {
        if (queryText.length() > 0) {
            cachedQueryLength = queryText.length();
            walletAdapter.getFilter().filter(queryText);
        } else {
            if (cachedQueryLength > 0) {
                walletAdapter.reset();
            }
        }
    }

    public void refreshWalletDisplay() {
        walletAdapter.notifyDataSetChanged();
    }
}
