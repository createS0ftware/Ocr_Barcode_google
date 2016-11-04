package com.google.android.gms.samples.vision.inner.bink.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.ApptentiveUtils;
import com.loyaltyangels.bink.DeviceRootedActivity;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.ShareFragment;
import com.loyaltyangels.bink.analytics.Category;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.ui.components.BinkSearchView;
import com.loyaltyangels.bink.ui.profile.ProfileFragment;
import com.loyaltyangels.bink.ui.settings.SettingsFragment;
import com.loyaltyangels.bink.ui.splash.SplashActivity;
import com.loyaltyangels.bink.ui.wallet.fragments.WalletFragment;
import com.loyaltyangels.bink.util.TintUtils;

public class MainActivity extends BaseActivity implements
        SearchView.OnQueryTextListener {

    private static final int MENU_CLOSE_DELAY = 300;

    private static final String FRAGMENT_WALLET = "wallet";
    private static final String FRAGMENT_SHARE = "share";
    private static final String FRAGMENT_SETTINGS = "settings";
    private static final String FRAGMENT_PROFILE = "profile";

    private WalletFragment walletFragment;
    private SettingsFragment settingsFragment;
    private ShareFragment shareFragment;
    private ProfileFragment profileFragment;

    private MenuItem searchItem;
    private MenuItem toggleItem;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView toolbarText;

    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        if (((App) getApplication()).isDeviceRooted()) {
            Intent intent = new Intent(this, DeviceRootedActivity.class);
            startActivity(intent);
            finish();
        } else if (apiConfig.getUserToken() != null) {
            setContentView(R.layout.activity_main);

            ApptentiveUtils.appOpen(getApplicationContext());

            walletFragment = (WalletFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_WALLET);

            shareFragment = (ShareFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_SHARE);

            settingsFragment = (SettingsFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_SETTINGS);

            profileFragment = (ProfileFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_PROFILE);

            if (walletFragment == null) {
                walletFragment = WalletFragment.newInstance();
            }

            if (shareFragment == null) {
                shareFragment = new ShareFragment();
            }

            if (settingsFragment == null) {
                settingsFragment = new SettingsFragment();
            }

            if (profileFragment == null) {
                profileFragment = new ProfileFragment();
            }

            setUpViews();
        } else {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showMenuItem(MenuItem menuItem, boolean delayShowingMenuItem) {
        int delay = delayShowingMenuItem ? MENU_CLOSE_DELAY : 0;

        new Handler().postDelayed(() -> {
            if (menuItem.getItemId() == R.id.nav_wallet) {
                menuItem.setChecked(true);

                toggleItem.setVisible(true);
                searchItem.setVisible(true);
                toolbarText.setText(R.string.title_wallet);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, walletFragment, FRAGMENT_WALLET)
                        .commit();
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                menuItem.setChecked(true);
                toggleItem.setVisible(false);
                searchItem.setVisible(false);
                toolbarText.setText(R.string.title_profile);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerView, profileFragment, FRAGMENT_PROFILE)
                        .commit();

            } else if (menuItem.getItemId() == R.id.nav_share) {
                menuItem.setChecked(true);
                toggleItem.setVisible(false);
                searchItem.setVisible(false);
                toolbarText.setText(R.string.title_share);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerView, shareFragment, FRAGMENT_SHARE)
                        .commit();

            } else if (menuItem.getItemId() == R.id.nav_settings) {
                menuItem.setChecked(true);

                toolbarText.setText(R.string.title_settings);
                toggleItem.setVisible(false);
                searchItem.setVisible(false);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerView, settingsFragment, FRAGMENT_SETTINGS)
                        .commit();

            } else if (menuItem.getItemId() == R.id.nav_faq) {
                String url = getString(R.string.url_faq);;

                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .build();

                customTabsIntent.launchUrl(this, Uri.parse(url));

                tracker.trackScreen(Screen.WebView, url);
            }


        }, delay);
    }

    public void updateValueToggleIcon() {
        if (appConfig.shouldShowMonetaryValue()) {
            toggleItem.setIcon(R.drawable.toggle_pounds);
        } else {
            toggleItem.setIcon(R.drawable.toggle_points);
        }

        TintUtils.tintMenuItemIcon(ContextCompat.getColor(this, R.color.colorAccent), toggleItem);

        if (walletFragment != null) {
            walletFragment.refreshWalletDisplay();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else if (!((BinkSearchView) searchItem.getActionView()).isIconified()) {
            closeSearch();
            walletFragment.resetSearch();
        } else {
            Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.containerView);

            if (!(topFragment instanceof WalletFragment)) {
                showMenuItem(navigationView.getMenu().findItem(R.id.nav_wallet), false);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.bink.wallet/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.bink.wallet/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        client.disconnect();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onUnitToggle() {
        appConfig
                .setShowMonetaryValue(!appConfig.shouldShowMonetaryValue());

        updateValueToggleIcon();

        tracker.trackEvent(Category.ButtonPress, com.loyaltyangels.bink.analytics.Action.PointsToggle);
    }

    private void setUpViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.menuView);
        toolbarText = (TextView) findViewById(R.id.toolbar_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Drawable menuIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu_white_24dp);
        menuIcon = DrawableCompat.wrap(menuIcon);
        DrawableCompat.setTint(menuIcon, ContextCompat.getColor(this, R.color.pink));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(menuIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, walletFragment)
                .commitAllowingStateLoss();

        navigationView.getMenu().findItem(R.id.nav_wallet)
                .setChecked(true);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            showMenuItem(menuItem, true);
            return false;
        });

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }
        });

        walletFragment.setListener(() -> closeSearch());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        toggleItem = menu.findItem(R.id.toggleItem);
        toggleItem.setOnMenuItemClickListener(menuItem -> {
            onUnitToggle();
            return false;
        });

        updateValueToggleIcon();

        searchItem = menu.findItem(R.id.action_search);

        BinkSearchView searchView = (BinkSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.applyStyling();
        searchView.setOnCloseListener(() -> {
            closeSearch();
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        walletFragment.filter(newText);
        return false;
    }

    private void closeSearch() {
        toolbar.collapseActionView();
        supportInvalidateOptionsMenu();
    }
}