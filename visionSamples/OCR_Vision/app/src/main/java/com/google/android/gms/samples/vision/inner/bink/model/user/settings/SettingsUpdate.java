package com.google.android.gms.samples.vision.inner.bink.model.user.settings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmcdonnell on 17/08/2016.
 */

public class SettingsUpdate implements Parcelable {

    private HashMap<String, SettingsOption> update;

    public void setOption(String slug, SettingsOption option) {
        update.put(slug, option);
    }

    public static class TypeAdapter implements JsonSerializer<SettingsUpdate>, JsonDeserializer<SettingsUpdate> {
        @Override
        public JsonElement serialize(SettingsUpdate src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            for (String slug : src.update.keySet()) {
                object.add(slug, new JsonPrimitive(src.update.get(slug).getValue()));
            }

            return object;
        }

        @Override
        public SettingsUpdate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            HashMap<String, SettingsOption> update = new HashMap<>();

            JsonObject object = json.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                SettingsOption option = SettingsOption.fromValue(entry.getValue().getAsString());
                update.put(entry.getKey(), option);
            }

            SettingsUpdate settingsUpdate = new SettingsUpdate();
            settingsUpdate.update = update;

            return settingsUpdate;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.update);
    }

    public SettingsUpdate() {
        update = new HashMap<>();
    }

    protected SettingsUpdate(Parcel in) {
        this.update = (HashMap<String, SettingsOption>) in.readSerializable();
    }

    public static final Parcelable.Creator<SettingsUpdate> CREATOR = new Parcelable.Creator<SettingsUpdate>() {
        @Override
        public SettingsUpdate createFromParcel(Parcel source) {
            return new SettingsUpdate(source);
        }

        @Override
        public SettingsUpdate[] newArray(int size) {
            return new SettingsUpdate[size];
        }
    };
}
