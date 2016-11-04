package com.google.android.gms.samples.vision.inner.bink.scanning;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.SourceData;
import com.loyaltyangels.bink.ui.BaseFragment;

import java.util.List;

/**
 * Created by John McDonnell on 24/09/2016.
 */
public abstract class BaseScanFragment extends BaseFragment implements BarcodeCallback {

    private ScanProcessor processor;
    private Rect previewFramingRect;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBarcodeView().getCameraSettings().setContinuousFocusEnabled(true);
        getBarcodeView().decodeContinuous(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBarcodeView().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getBarcodeView().pause();
        previewFramingRect = null;
    }

    @Override
    public void onDestroyView() {
        getBarcodeView().stopDecoding();
        super.onDestroyView();
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        if (processor != null) {
            processor.processBarcode(result.getResult());
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {

    }

    @Override
    public void barcodeResultFailed(SourceData sourceData) {
        if (processor != null) {
            sourceData.setCropRect(getBarcodeView().getPreviewFramingRect());
            processor.processSourceData(sourceData);
            if (previewFramingRect == null) {
                previewFramingRect = getBarcodeView().getPreviewFramingRect();
            }
        }
    }

    protected abstract BarcodeView getBarcodeView();

    public void setProcessor(ScanProcessor processor) {
        this.processor = processor;
    }
}
