package com.google.android.gms.samples.vision.inner.bink.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Tracker;
import com.loyaltyangels.bink.api.config.ApiConfig;
import com.loyaltyangels.bink.config.AppConfig;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.module.ActivityComponent;
import com.loyaltyangels.bink.module.DaggerActivityComponent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by jm on 18/07/16.
 */

public class BaseFragment extends Fragment {


    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Inject
    protected ApiConfig apiConfig;

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected Model model;

    @Inject
    protected OkHttpClient okHttpClient;

    @Inject
    protected Tracker tracker;

    private ActivityComponent activityComponent;

    @LayoutRes
    protected int getLayoutRes() {
        return -1;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
        getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);

        if (getLayoutRes() != -1) {
            View view = inflater.inflate(getLayoutRes(), container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }


    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    public <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(FragmentEvent.STOP));
    }

    public <T> Observable.Transformer<T, T> applySchedulersForEvent(FragmentEvent event) {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(event));
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    protected ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .appComponent(((App) getActivity().getApplication()).getAppComponent())
                    .build();
        }

        return activityComponent;
    }

    public boolean isConnected() {
        return ((BaseActivity) getActivity()).isConnected();
    }

    public void showConnectionError() {
        new AlertDialog.Builder(getContext())
                .setMessage(getResources().getString(R.string.api_connection_failed))
                .setTitle(getResources().getString(R.string.api_connection_failure_dialog_title))
                .setPositiveButton(getResources().getString(R.string.alert_retry), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

}
