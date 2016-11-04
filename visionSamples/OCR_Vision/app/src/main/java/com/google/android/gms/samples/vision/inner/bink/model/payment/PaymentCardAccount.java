package com.google.android.gms.samples.vision.inner.bink.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.model.common.Account;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.scheme.SchemeOfferImage;

import java.util.ArrayList;

/**
 * Created by jm on 14/07/16.
 */

public class PaymentCardAccount extends Account implements Parcelable {

    @SerializedName("images")
    ArrayList<SchemeOfferImage> images;

    @SerializedName("name_on_card")
    String nameOnCard;

    @SerializedName("expiry_month")
    String expiryMonth;

    @SerializedName("expiry_year")
    String expiryYear;

    @SerializedName("currency_code")
    String currencyCode;

    @SerializedName("country")
    String country;

    @SerializedName("pan_start")
    String panStart;

    @SerializedName("pan_end")
    String panEnd;

    @SerializedName("user")
    Integer user;

    @SerializedName("payment_card")
    PaymentCardType paymentCardType;

    @SerializedName("token")
    String token;

    @SerializedName("fingerprint")
    String fingerprint;

    @SerializedName("issuer")
    String issuer;

    public ArrayList<SchemeOfferImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<SchemeOfferImage> images) {
        this.images = images;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPanStart() {
        return panStart;
    }

    public void setPanStart(String panStart) {
        this.panStart = panStart;
    }

    public String getPanEnd() {
        return panEnd;
    }

    public void setPanEnd(String panEnd) {
        this.panEnd = panEnd;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public PaymentCardType getPaymentCardType() {
        return paymentCardType;
    }

    public void setPaymentCardType(PaymentCardType paymentCardType) {
        this.paymentCardType = paymentCardType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuer() {
        return issuer;
    }

    public SchemeOfferImage findImage(ImageType imageType) {
        if (images != null) {
            for (SchemeOfferImage image : images) {
                if (imageType == image.getImageType()) {
                    return image;
                }
            }
        }

        return null;
    }

    @Override
    public String getSearchField() {
        return getPaymentCardType().name();
    }

    @Override
    public String getType() {
        return "payment_card";
    }

    public PaymentCardAccount() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.images);
        dest.writeString(this.nameOnCard);
        dest.writeString(this.expiryMonth);
        dest.writeString(this.expiryYear);
        dest.writeString(this.currencyCode);
        dest.writeString(this.country);
        dest.writeString(this.panStart);
        dest.writeString(this.panEnd);
        dest.writeValue(this.user);
        dest.writeInt(this.paymentCardType == null ? -1 : this.paymentCardType.ordinal());
        dest.writeString(this.token);
        dest.writeString(this.fingerprint);
        dest.writeString(this.issuer);
    }

    protected PaymentCardAccount(Parcel in) {
        super(in);
        this.images = in.createTypedArrayList(SchemeOfferImage.CREATOR);
        this.nameOnCard = in.readString();
        this.expiryMonth = in.readString();
        this.expiryYear = in.readString();
        this.currencyCode = in.readString();
        this.country = in.readString();
        this.panStart = in.readString();
        this.panEnd = in.readString();
        this.user = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpPaymentCardType = in.readInt();
        this.paymentCardType = tmpPaymentCardType == -1 ? null : PaymentCardType.values()[tmpPaymentCardType];
        this.token = in.readString();
        this.fingerprint = in.readString();
        this.issuer = in.readString();
    }

    public static final Creator<PaymentCardAccount> CREATOR = new Creator<PaymentCardAccount>() {
        @Override
        public PaymentCardAccount createFromParcel(Parcel source) {
            return new PaymentCardAccount(source);
        }

        @Override
        public PaymentCardAccount[] newArray(int size) {
            return new PaymentCardAccount[size];
        }
    };
}
