package com.google.android.gms.samples.vision.inner.bink.model.scheme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by hansonaboagye on 01/09/16.
 */
public class AddSchemePayload {

    private String schemeId;
    private String question;
    private String answer;
    private int order;

    public AddSchemePayload(String schemeId, String question, String answer) {
        this.schemeId = schemeId;
        this.question = question;
        this.answer = answer;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public static class TypeAdapter implements JsonSerializer<AddSchemePayload> {

        @Override
        public JsonElement serialize(AddSchemePayload src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("scheme", src.schemeId);
            json.addProperty("order", src.order);
            json.addProperty(src.question, src.answer);
            return json;
        }
    }
}
