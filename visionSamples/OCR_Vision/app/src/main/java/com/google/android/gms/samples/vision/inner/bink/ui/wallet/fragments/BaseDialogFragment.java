package com.google.android.gms.samples.vision.inner.bink.ui.wallet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.analytics.Tracker;
import com.loyaltyangels.bink.config.AppConfig;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.module.ActivityComponent;
import com.loyaltyangels.bink.module.DaggerActivityComponent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.subjects.BehaviorSubject;

/**
 * Created by jmcdonnell on 12/09/16
 */
public class BaseDialogFragment extends DialogFragment {

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected Model model;

    @Inject
    protected Tracker tracker;

    private ActivityComponent activityComponent;
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (getLayoutRes() != -1) {
            View view = inflater.inflate(getLayoutRes(), container, false);
            ButterKnife.bind(this, view);
            return view;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
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
}
