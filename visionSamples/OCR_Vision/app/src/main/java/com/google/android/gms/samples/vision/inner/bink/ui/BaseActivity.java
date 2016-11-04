package com.google.android.gms.samples.vision.inner.bink.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;

import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Tracker;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.config.AppConfig;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.module.ActivityComponent;
import com.loyaltyangels.bink.module.DaggerActivityComponent;
import com.loyaltyangels.bink.util.TintUtils;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by jm on 19/07/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Inject
    protected Model model;

    @Inject
    protected ApiConfig apiConfig;

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected Tracker tracker;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    private ActivityComponent activityComponent;

    private boolean isConnected;

    private NetworkReceiver networkReceiver;
    private IntentFilter connectedIntent;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        injectDependencies();
        networkReceiver = new NetworkReceiver();
        networkReceiver.setContext(this);
        connectedIntent = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        isConnected = networkReceiver.isNetworkAvailable(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TintUtils.tintAllIcons(menu, ContextCompat.getColor(this, R.color.colorAccent));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void injectDependencies() {
        getActivityComponent();
        activityComponent.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    public void onResume() {
        lifecycleSubject.onNext(ActivityEvent.RESUME);
        registerReceiver(networkReceiver, connectedIntent);
        super.onResume();
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        unregisterReceiver(networkReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.STOP));
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    protected ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .appComponent(((App) getApplication()).getAppComponent())
                    .build();
        }

        return activityComponent;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * This method is needed to add functionality to the RETRY function of the 'No Internet' Dialog.
     * An implementation in each activity would allow it to retry the main action - such as
     * refreshing the wallet, add or deleting a card or other actions
     */
    public void retryLastAction()
    {

    }

    public void showConnectionError()
    {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.api_connection_failed))
                .setTitle(getResources().getString(R.string.api_connection_failure_dialog_title))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.alert_retry), (dialogInterface, i) -> {
                    retryLastAction();
                    dialogInterface.dismiss();
                })
                .show();
    }

    public void showErrorDialog(String contentText) {
        new AlertDialog.Builder(BaseActivity.this, R.style.AlertDialogStyle)
                .setTitle("Error")
                .setMessage(contentText)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_ok, null)
                .show();
    }

    class NetworkReceiver extends BroadcastReceiver {

        private Context baseContext;

        public NetworkReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (baseContext != null) {
                ((BaseActivity) baseContext).setConnected(isNetworkAvailable(context));
            }
        }

        public void setContext(Context context) {
            this.baseContext = context;
        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
        }
    }
}
