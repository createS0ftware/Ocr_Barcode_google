package com.google.android.gms.samples.vision.inner.bink.ui.loyalty;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lb.recyclerview_fast_scroller.RecyclerViewFastScroller;
import com.loyaltyangels.bink.BuildConfig;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.components.BinkSearchView;
import com.loyaltyangels.bink.ui.loyalty.components.CategoryToolbar;
import com.loyaltyangels.bink.util.TintUtils;

import java.util.Collections;

import butterknife.BindView;
import rx.functions.Action1;


public class SchemesListActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = SchemesListActivity.class.getSimpleName();
    private static final int REQUEST_CARD_ADDED = 31;

    public static String EXTRA_SELECTED_SCHEME = "scheme";

    private SchemeListAdapter schemeListAdapter;

    @BindView(R.id.loyalty_recyclerView)
    RecyclerView newLoyaltyrecyclerView;

    @BindView(R.id.category_toolbar_layout)
    CategoryToolbar categoryBar;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.loading_progress)
    ContentLoadingProgressBar contentLoadingProgressBar;

    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;

    Wallet wallet;

    MenuItem filterMenuItem;
    Boolean animationEnded = true;

    AnimatorSet slideSetFilter;
    int categoryBarHeight;
    BinkSearchView searchView;

    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemes_list);

        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
        model.getWallet()
                .compose(applySchedulers())
                .doOnNext(data -> {
                    wallet = data;
                })
                .doOnCompleted(() -> {
                    Log.e(TAG, "GOT WALLET");

                })
                .subscribe();

        categoryBarHeight = categoryBar.getMeasuredHeight();

        categoryBar.setOnCheckChangedListener((compoundButton, b) -> {
                    if (schemeListAdapter != null) {
                        schemeListAdapter.filterCategory(categoryBar.getOptionsSelected());
                    } else {
                        onNoSchemesError();
                    }
                }
        );

        categoryBar.setClickable(false);

        if (schemeListAdapter != null) {
            newLoyaltyrecyclerView.setAdapter(schemeListAdapter);
            schemeListAdapter.notifyDataSetChanged();
        }

        contentLoadingProgressBar.show();
        model.getSchemes()
                .compose(applySchedulers())
                .subscribe(schemes -> {
                    Collections.sort(schemes);
                    schemeListAdapter = new SchemeListAdapter(this, schemes, scheme -> {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_SELECTED_SCHEME, scheme);
                        setResult(RESULT_OK, data);
                        finish();
                    });
                    initUI();
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        if (!BuildConfig.DEBUG) {
                            Crashlytics.log(throwable.getMessage());
                        }
                        if (!isConnected()) {
                            showConnectionError();
                        } else {
                            showErrorDialog(getString(R.string.schemes_list_error_message));
                        }
                    }
                });


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void onNoSchemesError() {
        fastScroller.setVisibility(View.GONE);
        // using post delayed to look better in terms of UX
        new Handler().postDelayed(() -> {
            hideFilter();
        }, 1000);
    }

    private void initUI()
    {

        contentLoadingProgressBar.hide();
        newLoyaltyrecyclerView.setAdapter(schemeListAdapter);
        schemeListAdapter.notifyDataSetChanged();
        fastScroller.setVisibility(View.VISIBLE);

        newLoyaltyrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                if (firstVisibleItemPosition != 0) {
                    if (firstVisibleItemPosition == -1)
                        fastScroller.setVisibility(View.GONE);
                    return;
                }
                final int lastVisibleItemPosition = findLastVisibleItemPosition();
                int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                //if all items are shown, hide the fast-scroller
                fastScroller.setVisibility(schemeListAdapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
            }
        });
        fastScroller.setRecyclerView(newLoyaltyrecyclerView);
        fastScroller.setViewsToUse(R.layout.rv_fast_scroller_layout, R.id.fastscroller_bubble, R.id.fastscroller_handle);

        categoryBar.setClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_scheme_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (BinkSearchView) MenuItemCompat.getActionView(searchItem);
        TintUtils.tintMenuItemIcon(getResources().getColor(R.color.colorAccent), searchItem);

        searchView.applyStyling();

        filterMenuItem = menu.findItem(R.id.filter_item);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(() -> {
                if (schemeListAdapter != null) {
                    schemeListAdapter.reset();
                }
                return false;
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD_ADDED) {
            if (resultCode != Activity.RESULT_CANCELED) {
                setResult(resultCode, data);
                finish();
            }
        }

    }

    public boolean isCardInWallet(String targetID) {
        for (SchemeAccount schemeAccount : wallet.getSchemeAccounts()) {
            if (schemeAccount.getScheme().getId().equals(targetID)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.filter_item) {
            if (slideSetFilter != null) {
                slideSetFilter.cancel();
            }
            if (categoryBar.isShowing()) {
                synchronized (filterMenuItem) {
                    if (animationEnded) {
                        filterMenuItem.setEnabled(false);
                        hideFilter();
                    }
                }
            } else {
                synchronized (filterMenuItem) {
                    if (animationEnded) {
                        filterMenuItem.setEnabled(false);
                        showFilter();
                    }
                }
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showFilter() {
        ValueAnimator heightAnimator = ValueAnimator.ofInt(appBarLayout.getHeight(), categoryBarHeight);
        heightAnimator.setDuration(400);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer targetHeight = (Integer) valueAnimator.getAnimatedValue();
                appBarLayout.getLayoutParams().height = targetHeight;
                appBarLayout.requestLayout();
            }
        });

        slideSetFilter = new AnimatorSet();
        slideSetFilter.play(heightAnimator);
        slideSetFilter.setInterpolator(new AccelerateDecelerateInterpolator());
        slideSetFilter.start();
        categoryBar.animate()
                .setDuration(200)
                .alphaBy(1f)
                .setListener(slideAnimationListener)
                .start();
        categoryBar.setShowing(true);
    }


    Animator.AnimatorListener slideAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            animationEnded = false;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            animationEnded = true;
            filterMenuItem.setEnabled(true);
            categoryBar.setClickable(true);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private void hideFilter() {
        if (categoryBarHeight == 0) {
            categoryBarHeight = toolbar.getHeight() + categoryBar.getMeasuredHeight();
        }
        ValueAnimator heightAnimator = ValueAnimator.ofInt(appBarLayout.getHeight(), toolbar.getHeight());
        heightAnimator.setDuration(400);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer targetHeight = (Integer) valueAnimator.getAnimatedValue();
                appBarLayout.getLayoutParams().height = targetHeight;
                appBarLayout.requestLayout();
            }
        });
        slideSetFilter = new AnimatorSet();
        slideSetFilter.play(heightAnimator);
        slideSetFilter.setInterpolator(new AccelerateDecelerateInterpolator());
        slideSetFilter.start();
        categoryBar.animate()
                .setDuration(200)
                .setListener(slideAnimationListener)
                .alphaBy(-1f)
                .start();
        categoryBar.setShowing(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    int lastLength = 0;

    @Override
    public boolean onQueryTextChange(String newText) {
       if (schemeListAdapter != null){
            if (newText.length() > 0) {
                lastLength = newText.length();
                schemeListAdapter.getFilter().filter(newText);
            } else {
                if (lastLength > 0) {
                    schemeListAdapter.reset();
                }
            }
        } else {
            showErrorDialog(getString(R.string.schemes_list_error_message));
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "AddLoyaltyCard Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.bink.wallet/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

        tracker.trackScreen(Screen.SchemesList);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "AddLoyaltyCard Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.bink.wallet/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
