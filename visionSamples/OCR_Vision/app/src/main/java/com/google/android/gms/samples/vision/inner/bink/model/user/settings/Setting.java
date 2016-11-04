package com.google.android.gms.samples.vision.inner.bink.model.user.settings;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 18/08/2016.
 */

public class Setting implements Comparable<Setting> {

    public static String SLUG_BINK_MARKETING = "marketing-bink";
    public static String SLUG_EXTERNAL_MARKETING = "marketing-external";

    public enum ValueType {
        @SerializedName("boolean")
        Boolean
    }

    @SerializedName("value_type")
    ValueType valueType;

    @SerializedName("is_user_defined")
    Boolean isUserDefined;

    @SerializedName("label")
    String label;

    @SerializedName("scheme")
    String scheme;

    @SerializedName("value")
    SettingsOption settingsOption;

    @SerializedName("slug")
    String slug;

    @SerializedName("category")
    String category;

    public ValueType getValueType() {
        return valueType;
    }

    public Boolean getUserDefined() {
        return isUserDefined;
    }

    public String getLabel() {
        return label;
    }

    public String getScheme() {
        return scheme;
    }

    public SettingsOption getSettingsOption() {
        return settingsOption;
    }

    public String getSlug() {
        return slug;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public int compareTo(@NonNull Setting another) {
        return category.compareTo(another.category);
    }
}
