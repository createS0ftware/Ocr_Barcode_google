package com.google.android.gms.samples.vision.inner.bink.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 14/07/16.
 */

public enum BarcodeType {

    @SerializedName("0")CODE_128,
    @SerializedName("1")QR_CODE,
    @SerializedName("2")AZTEC,
    @SerializedName("3")PDF_417,
    @SerializedName("4")EAN_13,
    @SerializedName("5")DATA_MATRIX,
    @SerializedName("6")ITF,
    @SerializedName("7")CODE_39,

}
