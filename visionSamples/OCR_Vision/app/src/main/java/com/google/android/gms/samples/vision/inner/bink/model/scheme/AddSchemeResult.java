package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hansonaboagye on 01/09/16.
 */
public class AddSchemeResult implements Parcelable {

    @SerializedName("id")
    String id;

    @SerializedName("scheme")
    String schemeId;

    @SerializedName("order")
    Integer order;

    public String getId() {
        return id;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public Integer getOrder() {
        return order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.schemeId);
        dest.writeValue(this.order);
    }

    public AddSchemeResult() {
    }

    protected AddSchemeResult(Parcel in) {
        this.id = in.readString();
        this.schemeId = in.readString();
        this.order = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<AddSchemeResult> CREATOR = new Parcelable.Creator<AddSchemeResult>() {
        @Override
        public AddSchemeResult createFromParcel(Parcel source) {
            return new AddSchemeResult(source);
        }

        @Override
        public AddSchemeResult[] newArray(int size) {
            return new AddSchemeResult[size];
        }
    };
}
