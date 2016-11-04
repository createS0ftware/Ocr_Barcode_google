package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.loyaltyangels.bink.model.BarcodeType;
import com.loyaltyangels.bink.model.common.ImageType;

import java.util.ArrayList;

/**
 * Created by jm on 14/07/16
 */

public class Scheme implements Parcelable, Comparable<Scheme> {

    @SerializedName("id")
    String id;
    @SerializedName("colour")
    String colour;
    @SerializedName("point_name")
    String pointName;
    @SerializedName("tier") Tier tier;
    @SerializedName("has_points") boolean hasPoints;
    @SerializedName("forgotten_password_url")
    String forgottenPasswordUrl;
    @SerializedName("url")
    String url;
    @SerializedName("company_url")
    String companyUrl;
    @SerializedName("company")
    String company;
    @SerializedName("name")
    String name;
    @SerializedName("identifier")
    String identifier;
    @SerializedName("join_url")
    String joinUrl;
    @SerializedName("join_t_and_c")
    String joinTerms;
    @SerializedName("link_account_text")
    String linkAccountText;
    @SerializedName("slug")
    String slug;
    @SerializedName("category") int category;
    @SerializedName("barcode_type") BarcodeType barcodeType;
    @SerializedName("has_transactions") boolean hasTransactions;
    @SerializedName("link_questions")
    ArrayList<Question> linkQuestions;
    @SerializedName("manual_question") Question manualQuestion;
    @SerializedName("scan_question") Question scanQuestion;
    @SerializedName("images")
    ArrayList<SchemeOfferImage> images;
    @SerializedName("play_store_url")
    String playStoreUrl;
    @SerializedName("android_app_id")
    String androidAppId;
    @SerializedName("scan_message")
    String scanMessage;

    public String getId() {
        return id;
    }

    public String getColour() {
        return colour;
    }

    public String getPointName() {
        return pointName;
    }

    public Tier getTier() {
        return tier;
    }

    public boolean hasPoints() {
        return hasPoints;
    }

    public String getForgottenPasswordUrl() {
        return forgottenPasswordUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public String getJoinTerms() {
        return joinTerms;
    }

    public String getLinkAccountText() {
        return linkAccountText;
    }

    public String getSlug() {
        return slug;
    }

    public int getCategory() { return category; }

    public BarcodeType getBarcodeType() {
        return barcodeType;
    }

    public boolean hasTransactions() {
        return hasTransactions;
    }

    public ArrayList<Question> getLinkQuestions() {
        return linkQuestions;
    }

    public Question getManualQuestion() {
        return manualQuestion;
    }

    public Question getScanQuestion() {
        return scanQuestion;
    }

    public String getPlayStoreUrl() { return playStoreUrl; }

    public String getAndroidAppId() { return androidAppId; }

    public ArrayList<SchemeOfferImage> getImages() {
        return images;
    }

    public String getScanMessage() {
        return scanMessage;
    }

    public SchemeOfferImage findImageByType(@NonNull ImageType type) {
        if (images != null) {
            for (SchemeOfferImage image : images) {
                if (image.getImageType() == type) {
                    return image;
                }
            }
        }

        return null;
    }

    @Override
    public int compareTo(Scheme scheme) {
        return getCompany().compareToIgnoreCase(scheme.getCompany());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.colour);
        dest.writeString(this.pointName);
        dest.writeInt(this.tier == null ? -1 : this.tier.ordinal());
        dest.writeByte(this.hasPoints ? (byte) 1 : (byte) 0);
        dest.writeString(this.forgottenPasswordUrl);
        dest.writeString(this.url);
        dest.writeString(this.companyUrl);
        dest.writeString(this.company);
        dest.writeString(this.name);
        dest.writeString(this.identifier);
        dest.writeString(this.joinUrl);
        dest.writeString(this.joinTerms);
        dest.writeString(this.linkAccountText);
        dest.writeString(this.slug);
        dest.writeInt(this.category);
        dest.writeInt(this.barcodeType == null ? -1 : this.barcodeType.ordinal());
        dest.writeByte(this.hasTransactions ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.linkQuestions);
        dest.writeParcelable(this.manualQuestion, flags);
        dest.writeParcelable(this.scanQuestion, flags);
        dest.writeTypedList(this.images);
        dest.writeString(this.playStoreUrl);
        dest.writeString(this.androidAppId);
        dest.writeString(this.scanMessage);
    }

    protected Scheme(Parcel in) {
        this.id = in.readString();
        this.colour = in.readString();
        this.pointName = in.readString();
        int tmpTier = in.readInt();
        this.tier = tmpTier == -1 ? null : Tier.values()[tmpTier];
        this.hasPoints = in.readByte() != 0;
        this.forgottenPasswordUrl = in.readString();
        this.url = in.readString();
        this.companyUrl = in.readString();
        this.company = in.readString();
        this.name = in.readString();
        this.identifier = in.readString();
        this.joinUrl = in.readString();
        this.joinTerms = in.readString();
        this.linkAccountText = in.readString();
        this.slug = in.readString();
        this.category = in.readInt();
        int tmpBarcodeType = in.readInt();
        this.barcodeType = tmpBarcodeType == -1 ? null : BarcodeType.values()[tmpBarcodeType];
        this.hasTransactions = in.readByte() != 0;
        this.linkQuestions = in.createTypedArrayList(Question.CREATOR);
        this.manualQuestion = in.readParcelable(Question.class.getClassLoader());
        this.scanQuestion = in.readParcelable(Question.class.getClassLoader());
        this.images = in.createTypedArrayList(SchemeOfferImage.CREATOR);
        this.playStoreUrl = in.readString();
        this.androidAppId = in.readString();
        this.scanMessage = in.readString();
    }

    public static final Creator<Scheme> CREATOR = new Creator<Scheme>() {
        @Override
        public Scheme createFromParcel(Parcel source) {
            return new Scheme(source);
        }

        @Override
        public Scheme[] newArray(int size) {
            return new Scheme[size];
        }
    };
}
