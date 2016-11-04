package com.google.android.gms.samples.vision.inner.bink.scanning;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.Result;
import com.journeyapps.barcodescanner.SourceData;
import com.loyaltyangels.bink.model.Model;
import com.loyaltyangels.bink.model.scanning.LoyaltyScanResult;
import com.loyaltyangels.bink.model.scheme.IdentifySchemePayload;
import com.loyaltyangels.bink.model.scheme.IdentifySchemeResult;
import com.loyaltyangels.bink.model.scheme.Scheme;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Transforms a barcode or Bitmap into {@link LoyaltyScanResult}
 * <p>
 * Created by John McDonnell on 24/09/2016.
 */
public class IdentifySchemeProcessor implements ScanProcessor {

    private static final String TAG = IdentifySchemeProcessor.class.getSimpleName();

    private ArrayList<Scheme> schemes;
    private Model apiClient;

    private PublishSubject<SourceData> sourceDataSubject;
    private PublishSubject<String> barcodeResultSubject;

    public IdentifySchemeProcessor(Model apiClient) {
        this.apiClient = apiClient;

        sourceDataSubject = PublishSubject.create();
        barcodeResultSubject = PublishSubject.create();
    }

    public void setSchemes(ArrayList<Scheme> schemes) {
        this.schemes = schemes;
    }

    @Override
    public void processSourceData(SourceData sourceData) {
        if (sourceDataSubject.hasObservers() && schemes != null) {
            sourceDataSubject.onNext(sourceData);
        }
    }

    @Override
    public void processBarcode(Result result) {
        if (barcodeResultSubject.hasObservers() && schemes != null) {
            barcodeResultSubject.onNext(result.getText());
        }
    }

    @NonNull
    private Scheme findScheme(String id) throws InvalidSchemeIdException {
        for (Scheme scheme : schemes) {
            if (id.equals(scheme.getId())) {
                return scheme;
            }
        }

        throw new InvalidSchemeIdException();
    }

    public Observable<Scheme> schemeResults() {
        return sourceDataSubject
                .throttleLast(1, TimeUnit.SECONDS)
                .compose(identifySchemeFromSourceData())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(response -> {
                    try {
                        Log.d(TAG, "Scheme identified from image: " + response.getSchemeId());
                        Scheme scheme = findScheme(response.getSchemeId());
                        Log.d(TAG, "Scheme name: " + scheme.getName());
                        return Observable.just(scheme);
                    } catch (InvalidSchemeIdException e) {
                        return Observable.error(e);
                    }
                });
    }

    public Observable<LoyaltyScanResult> barcodeResults() {
        return barcodeResultSubject
                .subscribeOn(Schedulers.io())
                .filter(barcode -> !barcode.startsWith("http"))
                .compose(identifySchemeFromBarcode());
    }

    private Observable.Transformer<SourceData, IdentifySchemeResult> identifySchemeFromSourceData() {
        return observable -> observable.flatMap(sourceData -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            sourceData.getBitmap().compress(Bitmap.CompressFormat.JPEG, 80, out);

            try {
                IdentifySchemePayload payload = IdentifySchemePayload.create(out.toByteArray());
                return apiClient.identifySchemes(payload);

            } catch (IOException e) {
                return Observable.error(e);
            }
        });
    }

    private Observable.Transformer<String, LoyaltyScanResult> identifySchemeFromBarcode() {
        return observable -> observable.flatMap(barcode -> {
            for (Scheme scheme : schemes) {
                String identifier = scheme.getIdentifier();

                if (TextUtils.isEmpty(identifier)) {
                    continue;
                }

                Pattern pattern = Pattern.compile(identifier);
                Matcher matcher = pattern.matcher(barcode);

                if (matcher.find()) {
                    Log.d(TAG, "Scheme found for barcode: " + scheme.getName());
                    return Observable.just(new LoyaltyScanResult(scheme, barcode));
                }
            }

            Log.d(TAG, "No scheme found for barcode: " + barcode);

            return Observable.error(new UnidentifiedBarcodeException());
        });
    }

}
