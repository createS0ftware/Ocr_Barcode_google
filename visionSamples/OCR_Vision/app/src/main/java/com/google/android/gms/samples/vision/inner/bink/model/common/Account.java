package com.google.android.gms.samples.vision.inner.bink.model.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 14/07/16.
 */

public abstract class Account implements Parcelable, Comparable<Account> {



    @SerializedName("id")
    String id;
    @SerializedName("order") int order;

    public Account() {}

    protected Account(Parcel in) {
        this.id = in.readString();
        this.order = in.readInt();
    }

    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.order);
    }

    @Override
    public int compareTo(@NonNull Account account) {
        return order - account.order;
    }

    public abstract Image findImage(ImageType imageType);

    /**
     *  This can return a string array in the future
     * @return
     */
    public abstract String getSearchField();

    public abstract String getType();
}
