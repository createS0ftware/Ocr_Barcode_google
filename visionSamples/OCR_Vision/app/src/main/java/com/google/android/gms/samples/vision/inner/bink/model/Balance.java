package com.google.android.gms.samples.vision.inner.bink.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by jm on 14/07/16.
 */

public class Balance implements Parcelable {

    @SerializedName("id")
    String id;
    @SerializedName("scheme_account_id")
    String schemeAccountId;
    @SerializedName("value") double value;
    @SerializedName("value_label")
    String valueLabel;
    @SerializedName("points") double points;
    @SerializedName("points_label")
    String pointsLabel;
    @SerializedName("is_stale") boolean isStale;
    @SerializedName("user_id")
    String userId;
    @SerializedName("balance")
    String balance;

    public String getId() {
        return id;
    }

    public String getSchemeAccountId() {
        return schemeAccountId;
    }

    public double getValue() {
        return value;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public double getPoints() {
        return points;
    }

    public String getPointsLabel() {
        return pointsLabel;
    }

    public boolean isStale() {
        return isStale;
    }

    public String getUserId() {
        return userId;
    }

    public String getBalance() {
        return balance;
    }

    public static Balance createNewBalance(JSONObject balanceObject) {
        Balance resBalance = new GsonBuilder().create().fromJson(balanceObject.toString(), Balance.class);
        // This is a bit of an overkill but it is safe
        if (resBalance.balance == null)
        {
            if (resBalance.value > 0D)
            {
                resBalance.balance = new Double(resBalance.value).toString();
            }
        }
        return resBalance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.schemeAccountId);
        dest.writeDouble(this.value);
        dest.writeString(this.valueLabel);
        dest.writeDouble(this.points);
        dest.writeString(this.pointsLabel);
        dest.writeByte(this.isStale ? (byte) 1 : (byte) 0);
        dest.writeString(this.userId);
        dest.writeString(this.balance);
    }

    protected Balance(Parcel in) {
        this.id = in.readString();
        this.schemeAccountId = in.readString();
        this.value = in.readDouble();
        this.valueLabel = in.readString();
        this.points = in.readDouble();
        this.pointsLabel = in.readString();
        this.isStale = in.readByte() != 0;
        this.userId = in.readString();
        this.balance = in.readString();
    }

    public static final Creator<Balance> CREATOR = new Creator<Balance>() {
        @Override
        public Balance createFromParcel(Parcel source) {
            return new Balance(source);
        }

        @Override
        public Balance[] newArray(int size) {
            return new Balance[size];
        }
    };
}
