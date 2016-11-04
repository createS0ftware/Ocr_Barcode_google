package com.google.android.gms.samples.vision.inner.bink.scanning;

import com.google.zxing.Result;
import com.journeyapps.barcodescanner.SourceData;

/**
 * Created by John McDonnell on 24/09/2016.
 */

public interface ScanProcessor {
    void processSourceData(SourceData sourceData);

    void processBarcode(Result result);
}
