package com.google.android.gms.samples.vision.inner.bink.model.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 14/07/16.
 */

public class Image implements Parcelable {

    @SerializedName("id")
    String id;
    @SerializedName("image_type_code") ImageType imageTypeCode;
    @SerializedName("call_to_action")
    String callToAction;
    @SerializedName("order") int order;
    @SerializedName("image")
    String image;

    public String getId() {
        return id;
    }

    public ImageType getImageType() {
        return imageTypeCode;
    }

    public String getCallToAction() {
        return callToAction;
    }

    public int getOrder() {
        return order;
    }

    public String getImageUrl() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.imageTypeCode == null ? -1 : this.imageTypeCode.ordinal());
        dest.writeString(this.callToAction);
        dest.writeInt(this.order);
        dest.writeString(this.image);
    }

    protected Image(Parcel in) {
        this.id = in.readString();
        int tmpImageTypeCode = in.readInt();
        this.imageTypeCode = tmpImageTypeCode == -1 ? null : ImageType.values()[tmpImageTypeCode];
        this.callToAction = in.readString();
        this.order = in.readInt();
        this.image = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
