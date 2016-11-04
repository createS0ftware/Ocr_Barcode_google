package com.google.android.gms.samples.vision.inner.bink.getaddress;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by jmcdonnell on 09/09/2016.
 */

public class AddressData implements Parcelable {

    private String firstLine;
    private String secondLine;
    private String city;
    private String region;

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstLine);
        dest.writeString(this.secondLine);
        dest.writeString(this.city);
        dest.writeString(this.region);
    }

    public AddressData() {
    }

    protected AddressData(Parcel in) {
        this.firstLine = in.readString();
        this.secondLine = in.readString();
        this.city = in.readString();
        this.region = in.readString();
    }

    public static final Parcelable.Creator<AddressData> CREATOR = new Parcelable.Creator<AddressData>() {
        @Override
        public AddressData createFromParcel(Parcel source) {
            return new AddressData(source);
        }

        @Override
        public AddressData[] newArray(int size) {
            return new AddressData[size];
        }
    };

    public static class TypeAdapter implements JsonDeserializer<AddressData> {

        private static int POSITION_FIRST_LINE = 0;
        private static int POSITION_SECOND_LINE = 1;
        private static int POSITION_CITY = 5;
        private static int POSITION_REGION = 6;

        @Override
        public AddressData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String addressString = json.getAsString();
            String[] components = TextUtils.split(addressString, ", ");

            AddressData model = new AddressData();
            model.setFirstLine(components[POSITION_FIRST_LINE]);
            model.setSecondLine(components[POSITION_SECOND_LINE]);
            model.setRegion(components[POSITION_REGION]);
            model.setCity(components[POSITION_CITY]);

            return model;
        }
    }
}
