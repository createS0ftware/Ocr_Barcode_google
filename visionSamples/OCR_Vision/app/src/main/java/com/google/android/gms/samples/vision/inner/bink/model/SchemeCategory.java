package com.google.android.gms.samples.vision.inner.bink.model;

import com.loyaltyangels.bink.R;

/**
 * Created by jmcdonnell on 08/09/2016.
 */
public enum SchemeCategory {
    Travel(R.string.scheme_category_travel),
    Finance(R.string.scheme_category_finance),
    Household(R.string.scheme_category_household),
    Social(R.string.scheme_category_social),
    FoodAndDrink(R.string.scheme_category_food),
    Other(R.string.scheme_category_other);

    int id;

    SchemeCategory(int id) {
        this.id = id;
    }

    public int getNameId() {
        return id;
    }
}
