package com.google.android.gms.samples.vision.inner.bink.scanning;

/**
 * Created by John McDonnell on 24/09/2016.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeView;
import com.loyaltyangels.bink.R;

import java.util.List;

/**
 * Encapsulates BarcodeView, ViewfinderView and status text.
 * <p>
 * To customize the UI, use BarcodeView and ViewfinderView directly.
 */
public class ScannerView extends FrameLayout {

    private BarcodeView barcodeView;
    private ScannerViewFinder viewFinder;

    public ScannerView(Context context) {
        super(context);
        initialize();
    }

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        inflate(getContext(), R.layout.scanner_view, this);

        barcodeView = (BarcodeView) findViewById(R.id.barcode_view);

        viewFinder = (ScannerViewFinder) findViewById(R.id.view_finder);
        viewFinder.setCameraPreview(barcodeView);
    }

    private void initialize() {
        initialize(null);
    }

    public BarcodeView getBarcodeView() {
        return barcodeView;
    }

    public void addPossiblePoints(List<ResultPoint> points) {
        for (ResultPoint point : points) {
            viewFinder.addPossibleResultPoint(point);
        }
    }

}
