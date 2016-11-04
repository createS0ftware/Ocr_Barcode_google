package com.google.android.gms.samples.vision.inner.bink.util;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by bb on 11/07/16.
 */
public class KeyboardUtils {

    public static void hideKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    /**
     * Hide keyboard.
     *
     * <pre>
     * <code>KeyboardUtils.hideKeyboard(getActivity(), searchField);</code>
     * </pre>
     *
     * @param context
     * @param field
     */
    public static void hideKeyboard(Context context, EditText field) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
    }
    /**
     * Show keyboard with a 100ms delay.
     *
     * <pre>
     * <code>KeyboardUtils.showDelayedKeyboard(getActivity(), searchField);</code>
     * </pre>
     *
     * @param context
     * @param view
     */
    public static void showDelayedKeyboard (Context context, View view) {
        showDelayedKeyboard(context, view, 100);
    }


    /**
     * Show keyboard with a custom delay.
     *
     * <pre>
     * <code>KeyboardUtils.showDelayedKeyboard(getActivity(), searchField, 500);</code>
     * </pre>
     *
     * @param context
     * @param view
     * @param delay
     */
    public static void showDelayedKeyboard (final Context context, final View view, final int delay) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }

        }.execute();
    }

}
