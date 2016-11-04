package com.google.android.gms.samples.vision.inner.bink.ui.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.util.TintUtils;

/**
 * Created by hansonaboagye on 27/07/16.
 */
public class BinkSearchView extends SearchView {

    Context context;

    private ImageView mCloseButton;
    private ImageView mSearchButton;

    public BinkSearchView(Context context) {
        super(context);
        this.context = context;
    }


    public void applyStyling() {
        setMaxWidth(Integer.MAX_VALUE);
        Context tempContext = context;
        if (!OnQueryTextListener.class.isInstance(tempContext)) {
            // check if handset is not triggering the support library
            if (android.view.ContextThemeWrapper.class.isInstance(context)) {
                tempContext = ((android.view.ContextThemeWrapper) context).getBaseContext();
            } else {
                tempContext = ((ContextThemeWrapper) context).getBaseContext();
            }
        }

        setOnQueryTextListener((OnQueryTextListener) tempContext);
        setPadding(10, 0, 0, 0);

        TextView searchText = (TextView) findViewById(R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        searchText.setHint(getResources().getString(R.string.loyalty_search_hint));
        searchText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        mCloseButton = (ImageView) findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        mSearchButton = (ImageView) findViewById(android.support.v7.appcompat.R.id.search_button);

        TintUtils.tintDrawable(context, mCloseButton.getDrawable(), R.color.colorAccent);
        TintUtils.tintDrawable(context, mSearchButton.getDrawable(), R.color.colorAccent);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}
