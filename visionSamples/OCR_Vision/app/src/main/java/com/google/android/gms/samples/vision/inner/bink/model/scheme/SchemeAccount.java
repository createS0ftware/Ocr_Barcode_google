package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.model.Balance;
import com.loyaltyangels.bink.model.common.Account;
import com.loyaltyangels.bink.model.common.ImageType;

import java.util.ArrayList;

/**
 * Created by jm on 14/07/16.
 */

public class SchemeAccount extends Account implements Parcelable {

    public enum Status {
        @SerializedName("1")
        ACTIVE,

        @SerializedName("10")
        WALLET_ONLY,

        @SerializedName("403")
        USER_ACTION_REQUIRED,

        @SerializedName("532")
        TRIPPED_CAPTCHA,

        @SerializedName("530")
        SYSTEM_ACTION_REQUIRED,

        @SerializedName("900")
        JOIN


    }

    @SerializedName("status_name")
    String statusName;
    @SerializedName("card_label")
    String cardLabel;
    @SerializedName("scheme") Scheme scheme;
    @SerializedName("images")
    ArrayList<SchemeOfferImage> schemeOfferImages;
    @SerializedName("status") Status status;
    @SerializedName("barcode")
    String barcode;
    @SerializedName("balance") Balance balance;


    public String getStatusName() {
        return statusName;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public ArrayList<SchemeOfferImage> getImages() {
        return schemeOfferImages;
    }

    public @NonNull
    ArrayList<SchemeOfferImage> getGenericOfferImages() {
        ArrayList<SchemeOfferImage> genericOfferImages = new ArrayList<>();

        for (SchemeOfferImage image : schemeOfferImages) {
            if (image.getImageType() == ImageType.OFFER) {
                genericOfferImages.add(image);
            }
        }

        return genericOfferImages;
    }

    public @NonNull
    ArrayList<SchemeOfferImage> getPersonalisedOfferImages() {
        ArrayList<SchemeOfferImage> personalisedOfferImages = new ArrayList<>();

        for (SchemeOfferImage image : schemeOfferImages) {
            if (image.getImageType() == ImageType.PERSONAL_OFFER) {
                personalisedOfferImages.add(image);
            }
        }

        return personalisedOfferImages;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getBarcode() {
        return barcode;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public SchemeOfferImage findImage(ImageType type) {
        if (schemeOfferImages != null) {
            for (SchemeOfferImage image : schemeOfferImages) {
                if (image.getImageType() == type) {
                    return image;
                }
            }
        }

        return null;
    }

    @Override
    public String getSearchField() {
        return getScheme().getCompany();
    }

    @Override
    public String getType() {
        return "scheme";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.statusName);
        dest.writeString(this.cardLabel);
        dest.writeParcelable(this.scheme, flags);
        dest.writeTypedList(this.schemeOfferImages);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.barcode);
        dest.writeParcelable(this.balance, flags);
    }

    protected SchemeAccount(Parcel in) {
        super(in);
        this.statusName = in.readString();
        this.cardLabel = in.readString();
        this.scheme = in.readParcelable(Scheme.class.getClassLoader());
        this.schemeOfferImages = in.createTypedArrayList(SchemeOfferImage.CREATOR);
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        this.barcode = in.readString();
        this.balance = in.readParcelable(Balance.class.getClassLoader());
    }

    public static final Creator<SchemeAccount> CREATOR = new Creator<SchemeAccount>() {
        @Override
        public SchemeAccount createFromParcel(Parcel source) {
            return new SchemeAccount(source);
        }

        @Override
        public SchemeAccount[] newArray(int size) {
            return new SchemeAccount[size];
        }
    };
}
