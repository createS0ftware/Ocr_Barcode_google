package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmcdonnell on 22/09/2016.
 */

public enum QuestionType {
    @SerializedName("username")
    Username("username"),

    @SerializedName("email")
    Email("email"),

    @SerializedName("card_number")
    CardNumber("card_number"),

    @SerializedName("barcode")
    Barcode("barcode"),

    @SerializedName("password")
    Password("password"),

    @SerializedName("place_of_birth")
    PlaceOfBirth("place_of_birth"),

    @SerializedName("postcode")
    Postcode("postcode"),

    @SerializedName("memorable_date")
    MemorableDate("memorable_date"),

    @SerializedName("date_of_birth")
    DateOfBirth("date_of_birth"),

    @SerializedName("pin")
    Pin("pin"),

    @SerializedName("last_name")
    LastName("last_name"),

    @SerializedName("favourite_place")
    FavouritePlace("favourite_place");

    String type;

    QuestionType(String type) {
        this.type = type;
    }

    public String getName() {
        return type;
    }
}
