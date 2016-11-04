package com.google.android.gms.samples.vision.inner.bink.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by jmcdonnell on 08/09/2016.
 */

public final class DateUtils {

    private static final SimpleDateFormat DOB_API_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DOB_DISPLAY_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

    private DateUtils() {
    }

    public static DateFormat getDateOfBirthApiFormat() {
        return DOB_API_FORMAT;
    }

    public static SimpleDateFormat getDateOfBirthDisplayFormat() {
        return DOB_DISPLAY_FORMAT;
    }
}
