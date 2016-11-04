package com.google.android.gms.samples.vision.inner.bink.model.user.settings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 18/08/2016.
 */
public enum SettingsOption {
    @SerializedName("1")
    Enabled("1"),

    @SerializedName("0")
    Disabled("0");

    String value;

    SettingsOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SettingsOption fromValue(String value) {
        for (SettingsOption option : SettingsOption.values()) {
            if (value.equals(option.getValue())) {
                return option;
            }
        }

        return null;
    }
}
