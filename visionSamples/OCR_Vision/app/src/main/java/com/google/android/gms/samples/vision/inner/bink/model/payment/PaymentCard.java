package com.google.android.gms.samples.vision.inner.bink.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 14/07/16.
 */

public class PaymentCard implements Parcelable {

    public enum System {
        @SerializedName("amex")AMEX,
        @SerializedName("visa")VISA,
        @SerializedName("mastercard")MASTERCARD
    }

    @SerializedName("id")
    String id;
    @SerializedName("name")
    String name;
    @SerializedName("slug")
    String slug;
    @SerializedName("url")
    String url;
    @SerializedName("image")
    String image;
    @SerializedName("scan_message")
    String scanMessage;
    @SerializedName("input_label")
    String inputLabel;
    @SerializedName("is_active") boolean isActive;
    @SerializedName("system") System system;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getScanMessage() {
        return scanMessage;
    }

    public String getInputLabel() {
        return inputLabel;
    }

    public boolean isActive() {
        return isActive;
    }

    public System getSystem() {
        return system;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.slug);
        dest.writeString(this.url);
        dest.writeString(this.image);
        dest.writeString(this.scanMessage);
        dest.writeString(this.inputLabel);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeInt(this.system == null ? -1 : this.system.ordinal());
    }

    protected PaymentCard(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.slug = in.readString();
        this.url = in.readString();
        this.image = in.readString();
        this.scanMessage = in.readString();
        this.inputLabel = in.readString();
        this.isActive = in.readByte() != 0;
        int tmpSystem = in.readInt();
        this.system = tmpSystem == -1 ? null : System.values()[tmpSystem];
    }

    public static final Parcelable.Creator<PaymentCard> CREATOR = new Parcelable.Creator<PaymentCard>() {
        @Override
        public PaymentCard createFromParcel(Parcel source) {
            return new PaymentCard(source);
        }

        @Override
        public PaymentCard[] newArray(int size) {
            return new PaymentCard[size];
        }
    };
}
