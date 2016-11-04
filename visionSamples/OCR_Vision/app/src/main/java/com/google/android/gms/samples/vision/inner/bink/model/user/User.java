package com.google.android.gms.samples.vision.inner.bink.model.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 12/07/16.
 */

public class User implements Parcelable {

    @SerializedName("first_name")
    String firstName;
    @SerializedName("last_name")
    String lastName;
    @SerializedName("email")
    String email;
    @SerializedName("address_line_1")
    String addressLine1;
    @SerializedName("address_line_2")
    String addressLine2;
    @SerializedName("city")
    String city;
    @SerializedName("region")
    String region;
    @SerializedName("postcode")
    String postcode;
    @SerializedName("phone")
    String phone;
    @SerializedName("date_of_birth")
    String dateOfBirth;
    @SerializedName("country")
    String country;
    @SerializedName("gender") Gender gender;
    @SerializedName("referral_code")
    String referralCode;

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public boolean hasRequiredPersonalDetails() {
        return !TextUtils.isEmpty(firstName)
                && !TextUtils.isEmpty(lastName);
    }

    public boolean hasRequiredAddressDetails() {
        return !TextUtils.isEmpty(postcode)
                && !TextUtils.isEmpty(addressLine1)
                && !TextUtils.isEmpty(city);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.addressLine1);
        dest.writeString(this.addressLine2);
        dest.writeString(this.city);
        dest.writeString(this.region);
        dest.writeString(this.postcode);
        dest.writeString(this.phone);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.country);
        dest.writeInt(this.gender == null ? -1 : this.gender.ordinal());
        dest.writeString(this.referralCode);
    }

    protected User(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.addressLine1 = in.readString();
        this.addressLine2 = in.readString();
        this.city = in.readString();
        this.region = in.readString();
        this.postcode = in.readString();
        this.phone = in.readString();
        this.dateOfBirth = in.readString();
        this.country = in.readString();
        int tmpGender = in.readInt();
        this.gender = tmpGender == -1 ? null : Gender.values()[tmpGender];
        this.referralCode = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
