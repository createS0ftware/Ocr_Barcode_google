package com.google.android.gms.samples.vision.inner.bink.ui.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.model.user.Gender;
import com.loyaltyangels.bink.model.user.User;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.util.DateUtils;
import com.loyaltyangels.bink.util.TintUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import rx.Completable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jmcdonnell on 05/09/2016.
 */

public class EditPersonalDetailsActivity extends BaseActivity {

    private static final String TAG = EditPersonalDetailsActivity.class.getSimpleName();

    public static final String EXTRA_USER = "user";

    @BindView(R.id.user_layout)
    ViewGroup userLayout;

    @BindView(R.id.first_name)
    EditText firstName;

    @BindView(R.id.last_name)
    EditText lastName;

    @BindView(R.id.gender_spinner)
    Spinner genderSpinner;

    @BindView(R.id.date_of_birth)
    TextView dateOfBirth;

    @BindView(R.id.save)
    Button save;

    @State
    User user;

    private DatePickerDialog dateOfBirthDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_edit_personal_details);
        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);

        Drawable edit = ContextCompat.getDrawable(this, R.drawable.ic_edit_white_18dp);
        edit = TintUtils.tintDrawable(this, edit, R.color.colorAccent);
        dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(null, null, edit, null);

        if (user == null) {
            user = getIntent().getParcelableExtra(EXTRA_USER);
        }

        showUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.trackScreen(Screen.EditDetails);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            saveProfileAndFinish();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.save)
    void onSave() {
        if (isConnected()) {
            saveProfileAndFinish();
        } else {
            showConnectionError();
        }
    }

    @OnClick(R.id.date_of_birth)
    void onDateOfBirthClicked() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(lastName.getWindowToken(), 0);

        dateOfBirthDialog.show();
    }

    private void showUser() {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());

        ArrayList<String> genders = new ArrayList<>();
        int genderPosition = 0;

        for (int i = 0; i < Gender.values().length; i++) {
            Gender gender = Gender.values()[i];
            genders.add(getString(gender.nameRes));

            if (gender.equals(user.getGender())) {
                genderPosition = i;
            }
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.gender_list_item, genders);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(genderPosition);

        showDob();
    }

    private void showDob() {
        Date dob = null;

        if (!TextUtils.isEmpty(user.getDateOfBirth())) {
            try {
                dob = DateUtils.getDateOfBirthApiFormat().parse(user.getDateOfBirth());
                String dobString = DateUtils.getDateOfBirthDisplayFormat().format(dob);
                dateOfBirth.setText(dobString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            dateOfBirth.setText(R.string.edit_personal_details_set_dob);
        }

        if (dob == null) {
            dob = new Date();
        }

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(dob.getTime());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateOfBirthDialog = new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(Calendar.YEAR, selectedYear);
            calendar.set(Calendar.MONTH, selectedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

            String newDob = DateUtils.getDateOfBirthApiFormat().format(calendar.getTime());
            user.setDateOfBirth(newDob);

            showDob();
        }, year, month, day);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, -18);
        dateOfBirthDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
    }

    private void saveProfileAndFinish() {
        if (user == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(firstName.getWindowToken(), 0);

        Gender gender = Gender.genderForString(this, genderSpinner.getSelectedItem().toString());

        User update = new User();
        update.setFirstName(firstName.getText().toString());
        update.setLastName(lastName.getText().toString());
        update.setGender(gender);
        update.setDateOfBirth(user.getDateOfBirth());

        ProgressDialog progressDialog = new ProgressDialog(this, R.style.AlertDialogStyle);
        progressDialog.setMessage(getString(R.string.api_saving));

        Subscription delayedProgress = Completable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progressDialog::show);

        save.setEnabled(false);

        model.updateUser(update)
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    delayedProgress.unsubscribe();
                    progressDialog.hide();
                })
                .subscribe(updated -> {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_USER, updated);

                    setResult(RESULT_OK, data);
                    finish();
                }, error -> {
                    error.printStackTrace();

                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle(R.string.edit_personal_details_error_message)
                            .setMessage(R.string.edit_personal_details_error_message)
                            .setPositiveButton(R.string.alert_ok, null)
                            .show();

                    save.setEnabled(true);
                });
    }
}
