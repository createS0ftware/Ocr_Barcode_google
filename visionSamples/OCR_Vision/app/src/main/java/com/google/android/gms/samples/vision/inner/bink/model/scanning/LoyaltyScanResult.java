package com.google.android.gms.samples.vision.inner.bink.model.scanning;

import com.loyaltyangels.bink.model.scheme.Scheme;

/**
 * Created by jmcdonnell on 20/10/2016.
 */

public class LoyaltyScanResult {

    private Scheme scheme;
    private String barcodeResult;

    public LoyaltyScanResult(Scheme scheme, String barcodeResult) {
        this.scheme = scheme;
        this.barcodeResult = barcodeResult;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public String getBarcode() {
        return barcodeResult;
    }
}
