package com.google.android.gms.samples.vision.inner.bink.ui.wallet.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jmcdonnell on 12/09/2016.
 * <p>
 * Used for legacy dialog fragments. Removes the background and sets its own width.
 * This will be removed in a future version when the design changes.
 */

public class CustomDialogFragment extends BaseDialogFragment {

    @Override
    public void onResume() {
        super.onResume();
        setWidth(0.95f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

    private void setWidth(float modifier) {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        params.width = Float.valueOf(dm.widthPixels * modifier).intValue();
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

}
