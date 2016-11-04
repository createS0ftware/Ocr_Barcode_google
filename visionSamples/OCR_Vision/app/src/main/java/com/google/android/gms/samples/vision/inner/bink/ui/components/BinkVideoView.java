package com.google.android.gms.samples.vision.inner.bink.ui.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by hansonaboagye on 11/10/2016.
 */

public class BinkVideoView extends VideoView {

    private int wVideo;
    private int hVideo;

    public BinkVideoView(Context context) {
        super(context);
    }

    public BinkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoAspect(int w, int h) {
        wVideo = w;
        hVideo = h;
        measure(w, h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (wVideo != 0 && hVideo != 0)
                setMeasuredDimension(wVideo, hVideo);
        }
    }
}
