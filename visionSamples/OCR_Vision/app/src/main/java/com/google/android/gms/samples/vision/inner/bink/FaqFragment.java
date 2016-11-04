package com.google.android.gms.samples.vision.inner.bink;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by akumar on 06/01/16.
 */
public class FaqFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setting a rootview
        View rootView = inflater.inflate(R.layout.faq, container, false);


        WebView browser = (WebView) rootView.findViewById(R.id.webview);
        browser.setVisibility(View.VISIBLE);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        browser.loadUrl("https://www.chingrewards.com/frequently-asked-questions");


    return rootView;
    }

}
