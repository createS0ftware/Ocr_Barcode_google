package com.google.android.gms.samples.vision.inner.bink.ui.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.getaddress.AddressData;
import com.loyaltyangels.bink.getaddress.GetAddressApi;
import com.loyaltyangels.bink.model.user.User;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.util.TintUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import rx.Completable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jmcdonnell on 09/09/2016.
 */

public class EditAddressActivity extends BaseActivity {

    public static final String EXTRA_USER = "user";
    private static final Pattern POSTCODE_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z]?[0-9][A-Za-z0-9]? ?[0-9][A-Za-z]{2}$");

    @BindView(R.id.addresses)
    RecyclerView addresses;

    @BindView(R.id.postcode_search_layout)
    ViewGroup postcodeSearchLayout;

    @BindView(R.id.first_line)
    EditText firstLine;

    @BindView(R.id.second_line)
    EditText secondLine;

    @BindView(R.id.postcode)
    EditText postcode;

    @BindView(R.id.region)
    EditText region;

    @BindView(R.id.city)
    EditText city;

    @BindView(R.id.country)
    EditText country;

    @BindView(R.id.save)
    Button save;

    @BindView(R.id.enter_manually)
    Button enterManually;

    @BindView(R.id.lookup)
    TextView lookup;

    @BindView(R.id.first_line_layout)
    ViewGroup firstLineLayout;

    @BindView(R.id.second_line_layout)
    ViewGroup secondLineLayout;

    @BindView(R.id.city_layout)
    ViewGroup cityLayout;

    @BindView(R.id.region_layout)
    ViewGroup regionLayout;

    @BindView(R.id.country_layout)
    ViewGroup countryLayout;

    @BindView(R.id.postcode_progress_layout)
    ViewGroup progressLayout;

    @State
    User user;

    @State
    ArrayList<AddressData> addressComponents;

    @Inject
    GetAddressApi getAddressApi;

    MenuItem saveMenuItem;
    AddressListAdapter addressAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        getActivityComponent().inject(this);

        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (user == null) {
            user = getIntent().getParcelableExtra(EXTRA_USER);
        }

        RxTextView.afterTextChangeEvents(postcode)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    if (isPostcodeSearchEnabled()) {
                        String postcode = event.editable().toString().trim();
                        if (POSTCODE_PATTERN.matcher(postcode).matches()) {
                            searchPostcode(postcode);
                        }
                    }
                });

        addressAdapter = new AddressListAdapter();
        addresses.setLayoutManager(new LinearLayoutManager(this));
        addresses.setAdapter(addressAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.EditAddress);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_address, menu);
        saveMenuItem = menu.findItem(R.id.save);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.save) {
            saveAddressAndFinish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.enter_manually)
    void onEnterManuallyClicked() {
        showEnterManuallyLayout();
    }

    @OnClick(R.id.save)
    void onSaveClicked() {
        saveAddressAndFinish();
    }

    private boolean isPostcodeSearchEnabled() {
        return firstLineLayout.getVisibility() == View.GONE;
    }

    private void searchPostcode(String postcode) {
        if (enterManually.getVisibility() != View.VISIBLE) {
            progressLayout.setVisibility(View.VISIBLE);
        }

        getAddressApi.searchPostcode(postcode)
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    progressLayout.setVisibility(View.GONE);
                    enterManually.setVisibility(View.VISIBLE);
                })
                .subscribe(response -> {
                    addressComponents = response.getAddressComponents();
                    addressAdapter.notifyDataSetChanged();
                    hideKeyboard();
                }, error -> {
                    error.printStackTrace();
                });
    }

    private void showEnterManuallyLayout() {
        lookup.setVisibility(View.GONE);
        enterManually.setVisibility(View.GONE);
        addresses.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        firstLineLayout.setVisibility(View.VISIBLE);
        secondLineLayout.setVisibility(View.VISIBLE);
        cityLayout.setVisibility(View.VISIBLE);
        regionLayout.setVisibility(View.VISIBLE);
        saveMenuItem.setVisible(true);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(enterManually.getWindowToken(), 0);
    }

    private void saveAddressAndFinish() {
        /**
         * TODO Validate.
         */

        User update = new User();
        update.setAddressLine1(firstLine.getText().toString());
        update.setAddressLine2(secondLine.getText().toString());
        update.setPostcode(postcode.getText().toString());
        update.setCity(city.getText().toString());
        update.setRegion(region.getText().toString());
        update.setCountry(country.getText().toString());

        ProgressDialog progress = new ProgressDialog(this, R.style.AlertDialogStyle);
        progress.setMessage(getString(R.string.api_saving));
        progress.setCancelable(false);

        Subscription delayedProgress = Completable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress::show);

        saveMenuItem.setEnabled(false);
        save.setEnabled(false);

        model.updateUser(update)
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    delayedProgress.unsubscribe();
                    progress.hide();
                })
                .subscribe(updated -> {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_USER, updated);

                    setResult(RESULT_OK, data);
                    finish();
                }, error -> {
                    error.printStackTrace();
                    saveMenuItem.setEnabled(true);
                    save.setEnabled(true);

                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage(R.string.edit_address_save_error)
                            .setPositiveButton(R.string.alert_ok, null)
                            .show();
                });
    }

    private class AddressListAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_postcode_result, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AddressData components = addressComponents.get(position);

            StringBuilder builder = new StringBuilder();

            if (!TextUtils.isEmpty(components.getFirstLine())) {
                builder.append(components.getFirstLine());
                builder.append(", ");
            }

            if (!TextUtils.isEmpty(components.getSecondLine())) {
                builder.append(components.getSecondLine());
                builder.append(", ");
            }

            if (!TextUtils.isEmpty(components.getCity())) {
                builder.append(components.getCity());
                builder.append(", ");
            }

            if (!TextUtils.isEmpty(components.getRegion())) {
                builder.append(components.getRegion());
                builder.append(", ");
            }

            builder.delete(builder.lastIndexOf(", "), builder.length());

            holder.address.setText(builder.toString());

            holder.itemView.setOnClickListener(view -> {
                firstLine.setText(components.getFirstLine());
                secondLine.setText(components.getSecondLine());
                city.setText(components.getCity());
                region.setText(components.getRegion());

                /**
                 * Hardcoded - GetAddress.io does not provide the country.
                 */
                country.setText("United Kingdom");

                showEnterManuallyLayout();
            });
        }

        @Override
        public int getItemCount() {
            return addressComponents != null ? addressComponents.size() : 0;
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.address)
        TextView address;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
