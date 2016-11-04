package com.google.android.gms.samples.vision.inner.bink.ui.profile;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.user.User;
import com.loyaltyangels.bink.ui.BaseFragment;
import com.loyaltyangels.bink.util.DateUtils;
import com.trello.rxlifecycle.FragmentEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import rx.Completable;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jmcdonnell on 05/09/2016.
 */

public class ProfileFragment extends BaseFragment {

    private static final int REQUEST_EDIT_PERSONAL_DETAILS = 0;
    private static final int REQUEST_EDIT_ADDRESS = 1;

    @BindView(R.id.header_layout)
    ViewGroup headerLayout;

    @BindView(R.id.personal_details_parent)
    ViewGroup personalDetailsParent;

    @BindView(R.id.address_parent)
    ViewGroup addressParent;

    @BindView(R.id.mobile_parent)
    ViewGroup mobileParent;

    @BindView(R.id.bink_id)
    TextView binkId;

    @BindView(R.id.cards_added)
    TextView cardsAdded;

    @BindView(R.id.personal_details_status)
    TextView personalDetailsStatus;

    @BindView(R.id.personal_details)
    TextView personalDetails;

    @BindView(R.id.address_status)
    TextView addressStatus;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.mobile_status)
    TextView mobileStatus;

    @BindView(R.id.mobile)
    TextView mobile;

    @BindView(R.id.personal_details_layout)
    ViewGroup personalDetailsLayout;

    @BindView(R.id.progress)
    ProgressBar progress;

    @State
    boolean animated;

    Wallet wallet;
    User user;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        Observable<User> userObservable = model.getUser();
        Observable<Wallet> walletObservable = model.getWallet();

        Subscription delayedProgress = Completable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> progress.setVisibility(View.VISIBLE));

        Observable.zip(userObservable, walletObservable, Pair::create)
                .compose(applySchedulersForEvent(FragmentEvent.DESTROY))
                .doOnTerminate(() -> {
                    delayedProgress.unsubscribe();
                    progress.setVisibility(View.GONE);
                })
                .subscribe(result -> {
                    wallet = result.second;
                    user = result.first;
                    showUser();
                }, error -> {
                    error.printStackTrace();
                    Snackbar.make(view, R.string.profile_error, Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.Profile);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @OnClick(R.id.personal_details_layout)
    void onPersonalDetailsClicked() {
        Intent intent = new Intent(getContext(), EditPersonalDetailsActivity.class);
        intent.putExtra(EditPersonalDetailsActivity.EXTRA_USER, user);
        startActivityForResult(intent, REQUEST_EDIT_PERSONAL_DETAILS);
    }

    @OnClick(R.id.address_layout)
    void onAddressClicked() {
        Intent intent = new Intent(getContext(), EditAddressActivity.class);
        intent.putExtra(EditAddressActivity.EXTRA_USER, user);
        startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
    }

    @OnClick(R.id.mobile_layout)
    void onMobileClicked() {
        EditMobileDialogFragment mobileFragment = EditMobileDialogFragment.newInstance(user);
        mobileFragment.setListener(updatedUser -> {
            user = updatedUser;
            showUser();
        });
        mobileFragment.show(getFragmentManager(), null);
        tracker.trackScreen(Screen.EditMobile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_PERSONAL_DETAILS && resultCode == Activity.RESULT_OK) {
            user = data.getParcelableExtra(EditPersonalDetailsActivity.EXTRA_USER);
            showUser();
        } else if (requestCode == REQUEST_EDIT_ADDRESS && resultCode == Activity.RESULT_OK) {
            user = data.getParcelableExtra(EditAddressActivity.EXTRA_USER);
            showUser();
        }
    }

    private void showUser() {
        binkId.setText(user.getEmail());

        if (wallet != null) {
            cardsAdded.setText(getResources().getQuantityString(R.plurals.profile_cards_added, wallet.getWalletSize(), wallet.getWalletSize()));
        }

        // Personal Details
        if (user.hasRequiredPersonalDetails()) {
            personalDetailsStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_tick, 0, 0, 0);
        } else {
            personalDetailsStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_warning, 0, 0, 0);
        }
        personalDetails.setText(buildPersonalDetails(user));

        // Address
        if (user.hasRequiredAddressDetails()) {
            addressStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_tick, 0, 0, 0);
        } else {
            addressStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_warning, 0, 0, 0);
        }
        address.setText(buildAddress(user));

        // Mobile
        if (!TextUtils.isEmpty(user.getPhone())) {
            mobileStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_tick, 0, 0, 0);
            mobile.setText(user.getPhone());
        } else {
            mobileStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_warning, 0, 0, 0);
            mobile.setText(R.string.profile_add_mobile);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !animated) {
            startFancyAnimation();
        } else {
            headerLayout.setVisibility(View.VISIBLE);
            personalDetailsParent.setVisibility(View.VISIBLE);
            addressParent.setVisibility(View.VISIBLE);
            mobileParent.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startFancyAnimation() {
        animated = true;
        headerLayout.setVisibility(View.VISIBLE);

        int headerAnimationDuration = 700;

        Animator animator = ViewAnimationUtils.createCircularReveal(
                headerLayout,
                headerLayout.getLeft(),
                headerLayout.getTop(),
                0,
                headerLayout.getMeasuredWidth() * 2);

        animator.setDuration(headerAnimationDuration);
        animator.start();

        int fadeInDuration = 300;

        Interpolator interpolator = new DecelerateInterpolator();

        personalDetailsParent.setAlpha(0);
        personalDetailsParent.setTranslationY(100);
        personalDetailsParent.setVisibility(View.VISIBLE);
        personalDetailsParent.animate()
                .alpha(1)
                .translationY(0)
                .setInterpolator(interpolator)
                .setStartDelay(600)
                .setDuration(fadeInDuration)
                .start();

        addressParent.setAlpha(0);
        addressParent.setTranslationY(100);
        addressParent.setVisibility(View.VISIBLE);
        addressParent.animate()
                .alpha(1)
                .translationY(0)
                .setInterpolator(interpolator)
                .setDuration(fadeInDuration)
                .setStartDelay(800)
                .start();

        mobileParent.setAlpha(0);
        mobileParent.setTranslationY(100);
        mobileParent.setVisibility(View.VISIBLE);
        mobileParent.animate()
                .alpha(1)
                .translationY(0)
                .setInterpolator(interpolator)
                .setDuration(fadeInDuration)
                .setStartDelay(1000);
    }

    private String buildPersonalDetails(User user) {
        DateFormat dateFormat = DateUtils.getDateOfBirthApiFormat();
        DateFormat displayFormat = DateUtils.getDateOfBirthDisplayFormat();

        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(user.getFirstName())) {
            builder.append(user.getFirstName());

            if (!TextUtils.isEmpty(user.getLastName())) {
                builder.append(' ');
                builder.append(user.getLastName());
            }

            builder.append('\n');
        }

        if (user.getGender() != null) {
            builder.append(getString(user.getGender().nameRes));
            builder.append('\n');
        }

        if (!TextUtils.isEmpty(user.getDateOfBirth())) {
            try {
                Date dob = dateFormat.parse(user.getDateOfBirth());
                builder.append(displayFormat.format(dob));
                builder.append('\n');
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * If we have any fields appended, there will supposedly be a redundant newline at the
         * end as per the above logic.
         */
        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        } else {
            builder.append(getString(R.string.profile_add_personal_details));
        }

        return builder.toString();
    }

    private String buildAddress(User user) {
        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(user.getAddressLine1())) {
            builder.append(user.getAddressLine1());
            builder.append('\n');
        }

        if (!TextUtils.isEmpty(user.getAddressLine2())) {
            builder.append(user.getAddressLine2());
            builder.append('\n');
        }

        if (!TextUtils.isEmpty(user.getCity())) {
            builder.append(user.getCity());
            builder.append('\n');
        }

        if (!TextUtils.isEmpty(user.getPostcode())) {
            builder.append(user.getPostcode());
            builder.append('\n');
        }

        if (!TextUtils.isEmpty(user.getRegion())) {
            builder.append(user.getRegion());
            builder.append('\n');
        }

        if (!TextUtils.isEmpty(user.getCountry())) {
            builder.append(user.getCountry());
            builder.append('\n');
        }

        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        } else {
            builder.append(getString(R.string.profile_add_address));
        }

        return builder.toString();
    }
}
