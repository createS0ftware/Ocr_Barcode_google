package com.google.android.gms.samples.vision.inner.bink.model.user;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.R;

/**
 * Created by jmcdonnell on 05/09/2016.
 */

public enum Gender {

    @SerializedName("other")
    Other(R.string.gender_other),

    @SerializedName("male")
    Male(R.string.gender_male),

    @SerializedName("female")
    Female(R.string.gender_female);

    @StringRes
    public final int nameRes;

    Gender(@StringRes int nameRes) {
        this.nameRes = nameRes;
    }

    @Nullable
    public static Gender genderForString(Context context, String genderString) {
        for (Gender gender : values()) {
            if (TextUtils.equals(context.getString(gender.nameRes), genderString)) {
                return gender;
            }
        }

        return null;
    }
}
