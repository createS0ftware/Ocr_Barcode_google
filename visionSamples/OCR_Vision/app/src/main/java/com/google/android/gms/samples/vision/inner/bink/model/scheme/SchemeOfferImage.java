package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.model.common.Image;

/**
 * Created by jm on 14/07/16.
 */

public class SchemeOfferImage extends Image implements Parcelable {

    @SerializedName("description")
    String description;
    @SerializedName("url")
    String url;
    @SerializedName("strap_line")
    String strapLine;

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }


    public String getStrapLine() {
        return strapLine;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.strapLine);
    }

    protected SchemeOfferImage(Parcel in) {
        super(in);
        this.description = in.readString();
        this.url = in.readString();
        this.strapLine = in.readString();
    }

    public static final Creator<SchemeOfferImage> CREATOR = new Creator<SchemeOfferImage>() {
        @Override
        public SchemeOfferImage createFromParcel(Parcel source) {
            return new SchemeOfferImage(source);
        }

        @Override
        public SchemeOfferImage[] newArray(int size) {
            return new SchemeOfferImage[size];
        }
    };
}
