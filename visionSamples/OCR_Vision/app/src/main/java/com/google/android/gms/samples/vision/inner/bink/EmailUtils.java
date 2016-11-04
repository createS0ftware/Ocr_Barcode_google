package com.google.android.gms.samples.vision.inner.bink;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bb on 04/07/16.
 */
public class EmailUtils {
    /*
    For further information:
    http://stackoverflow.com/questions/18463848/how-to-tell-if-a-random-string-is-an-email-address-or-something-else
     */
    public static boolean checkifEmail(String email){
        // TextUtils.isEmpty() is superior to email.isEmpty() as it checks for null as well
        return !TextUtils.isEmpty(email) && checkImpl3(email);
    }

    private static boolean checkImpl(String email){
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        return mat.matches();
    }

    private static boolean checkImpl3(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
