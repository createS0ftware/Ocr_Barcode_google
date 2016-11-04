package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.model.Balance;

/**
 * Created by jmcdonnell on 13/09/2016.
 */

public class SchemeLinkResponse implements Parcelable {

    @SerializedName("balance")
    Balance balance;

    @SerializedName("status")
    Integer status;

    @SerializedName("status_name")
    String statusName;

    public Balance getBalance() {
        return balance;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.balance, flags);
        dest.writeValue(this.status);
        dest.writeString(this.statusName);
    }

    protected SchemeLinkResponse(Parcel in) {
        this.balance = in.readParcelable(Balance.class.getClassLoader());
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.statusName = in.readString();
    }

    public static final Creator<SchemeLinkResponse> CREATOR = new Creator<SchemeLinkResponse>() {
        @Override
        public SchemeLinkResponse createFromParcel(Parcel source) {
            return new SchemeLinkResponse(source);
        }

        @Override
        public SchemeLinkResponse[] newArray(int size) {
            return new SchemeLinkResponse[size];
        }
    };
}
