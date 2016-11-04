package com.google.android.gms.samples.vision.inner.bink.getaddress;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by jmcdonnell on 09/09/2016.
 */

public class GetAddressResponse {

    @SerializedName("Addresses")
    ArrayList<AddressData> addressComponents;

    public ArrayList<AddressData> getAddressComponents() {
        return addressComponents;
    }

}
