package com.google.android.gms.samples.vision.inner.bink.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bb on 03/08/16.
 */
public class Order {
    @SerializedName("id")
    String id;
    @SerializedName("order") int order;
    @SerializedName("type")
    String type;

    public static Order fromAccount(Account account) {
        Order order = new Order();
        order.id = account.getId();
        order.order = account.getOrder();
        order.type = account.getType();
        return order;
    }

    private Order() {
    }

    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public String getType() {
        return type;
    }
}
