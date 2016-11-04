package com.google.android.gms.samples.vision.inner.bink.ui.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.user.settings.Setting;
import com.loyaltyangels.bink.model.user.settings.SettingsOption;
import com.loyaltyangels.bink.model.user.settings.SettingsUpdate;
import com.loyaltyangels.bink.ui.BaseActivity;
import com.loyaltyangels.bink.ui.SectionedRecyclerViewAdapter;
import com.loyaltyangels.bink.util.TintUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;


public class PreferencesActivity extends BaseActivity {

    private static final String TAG = PreferencesActivity.class.getSimpleName();

    @BindView(R.id.settings)
    RecyclerView settingsView;

    private ArrayList<String> sections;
    private ArrayList<Setting> settings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        TintUtils.tintDrawable(this, toolbar.getNavigationIcon(), R.color.colorAccent);

        settingsView.setLayoutManager(new LinearLayoutManager(this));
        settingsView.setAdapter(new SettingsAdapter());

        ProgressDialog progressDialog = new ProgressDialog(this, R.style.AlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.api_loading));

        Subscription delayedProgress = Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(applySchedulers())
                .subscribe(l -> progressDialog.show());

        model.getUserSettings()
                .compose(applySchedulers())
                .doOnTerminate(() -> {
                    delayedProgress.unsubscribe();
                    progressDialog.dismiss();
                })
                .subscribe(this::updateSettings, error -> {
                    error.printStackTrace();

                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.preferences_error)
                            .setPositiveButton(R.string.alert_ok, null)
                            .show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateSettings(ArrayList<Setting> newSettings) {
        Collections.sort(newSettings);

        sections = new ArrayList<>();
        settings = newSettings;

        for (Setting setting : settings) {
            if (!sections.contains(setting.getCategory())) {
                sections.add(setting.getCategory());
            }
        }

        settingsView.getAdapter().notifyDataSetChanged();
    }

    private class SettingsAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

        private LayoutInflater inflater;

        public SettingsAdapter() {
            super();
            inflater = LayoutInflater.from(PreferencesActivity.this);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_HEADER) {
                return new SectionHolder(inflater.inflate(R.layout.layout_user_settings_section, parent, false));
            } else if (viewType == VIEW_TYPE_ITEM) {
                return new SettingHolder(inflater.inflate(R.layout.layout_user_settings_item, parent, false));
            }

            return null;
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
            SectionHolder sectionHolder = (SectionHolder) holder;
            String category = sections.get(section);
            sectionHolder.label.setText(category);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition, int absolutePosition) {
            SettingHolder settingHolder = (SettingHolder) holder;
            Setting setting = settings.get(absolutePosition);

            boolean checked = SettingsOption.Enabled.equals(setting.getSettingsOption());
            settingHolder.toggle.setChecked(checked);
            settingHolder.label.setText(setting.getLabel());

            settingHolder.itemView.setOnClickListener(v -> {
                settingHolder.toggle.setChecked(!settingHolder.toggle.isChecked());
            });

            settingHolder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SettingsOption option = isChecked ? SettingsOption.Enabled : SettingsOption.Disabled;

                SettingsUpdate settingsUpdate = new SettingsUpdate();
                settingsUpdate.setOption(setting.getSlug(), option);

                model.updateSettings(settingsUpdate)
                        .compose(applySchedulers())
                        .subscribe(result -> {
                            Log.d(TAG, "Updated setting for " + setting.getSlug());
                        }, Throwable::printStackTrace);
            });
        }

        @Override
        public int getSectionCount() {
            return sections != null ? sections.size() : 0;
        }

        @Override
        public int getItemCount(int sectionIndex) {
            if (sections == null || settings == null) {
                return 0;
            }
            int sectionCount = 0;

            String section = sections.get(sectionIndex);

            for (Setting setting : settings) {
                if (section.equals(setting.getCategory())) {
                    sectionCount++;
                }
            }

            return sectionCount;
        }
    }

    static class SectionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.label)
        TextView label;

        public SectionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class SettingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.label)
        TextView label;

        @BindView(R.id.toggle)
        SwitchCompat toggle;

        public SettingHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

