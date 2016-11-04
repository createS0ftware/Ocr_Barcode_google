package com.google.android.gms.samples.vision.inner.bink.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.loyaltyangels.bink.R;

public class TintUtils {
    public static void tintAllIcons(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);
            tintMenuItemIcon(color, item);
            tintShareIconIfPresent(color, item);
        }
    }

    public static void tintMenuItemIcon(@ColorInt int color, MenuItem item) {
        final Drawable drawable = item.getIcon();
        if (drawable != null) {
            Drawable tinted = tintDrawable(drawable, color);
            item.setIcon(tinted);

        }
    }

    private static void tintShareIconIfPresent(@ColorInt int color, MenuItem item) {
        if (item.getActionView() != null) {
            final View actionView = item.getActionView();
            final View expandActivitiesButton = actionView.findViewById(R.id.expand_activities_button);
            if (expandActivitiesButton != null) {
                final ImageView image = (ImageView) expandActivitiesButton.findViewById(R.id.image);
                if (image != null) {
                    Drawable tinted = tintDrawable(image.getDrawable(), color);
                    image.setImageDrawable(tinted);
                }
            }
        }
    }

    public static Drawable tintDrawable(Context context, Drawable drawable, @ColorRes int color) {
        int colorValue = ContextCompat.getColor(context, color);
        return tintDrawable(drawable, colorValue);
    }

    public static Drawable tintDrawable(Drawable drawable, @ColorInt int color) {
        final Drawable wrapped = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(wrapped, color);
        return wrapped;
    }
}