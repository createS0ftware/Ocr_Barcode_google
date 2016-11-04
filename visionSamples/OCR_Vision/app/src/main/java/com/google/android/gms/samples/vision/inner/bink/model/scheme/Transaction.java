package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 13/09/2016.
 */

public class Transaction implements Parcelable {

    @SerializedName("date")
    String date;

    @SerializedName("description")
    String description;

    @SerializedName("points")
    String points;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPoints() {
        return points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.description);
        dest.writeString(this.points);
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        this.date = in.readString();
        this.description = in.readString();
        this.points = in.readString();
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
