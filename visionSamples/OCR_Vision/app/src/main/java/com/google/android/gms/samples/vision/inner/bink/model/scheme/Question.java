package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 18/07/16.
 */

public class Question implements Parcelable {

    @SerializedName("id")
    String id;

    @SerializedName("order")
    int order;

    @SerializedName("type")
    QuestionType type;

    @SerializedName("label")
    String label;

    @SerializedName("third_party_identifier")
    boolean thirdPartyIdentifier;


    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public QuestionType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public boolean isThirdPartyIdentifier() {
        return thirdPartyIdentifier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.order);
        dest.writeSerializable(this.type);
        dest.writeString(this.label);
        dest.writeByte(this.thirdPartyIdentifier ? (byte) 1 : (byte) 0);
    }

    protected Question(Parcel in) {
        this.id = in.readString();
        this.order = in.readInt();
        this.type = (QuestionType) in.readSerializable();
        this.label = in.readString();
        this.thirdPartyIdentifier = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
