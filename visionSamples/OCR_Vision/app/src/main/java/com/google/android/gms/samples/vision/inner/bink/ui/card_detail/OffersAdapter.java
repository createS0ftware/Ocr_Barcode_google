package com.google.android.gms.samples.vision.inner.bink.ui.card_detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loyaltyangels.bink.App;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.analytics.Screen;
import com.loyaltyangels.bink.analytics.Tracker;
import com.loyaltyangels.bink.model.scheme.SchemeOfferImage;

import java.util.List;

/**
 * Created by hansonaboagye on 17/08/16.
 */
public class OffersAdapter extends PagerAdapter {

        List<SchemeOfferImage> offers;
        private Context mContext;

        public OffersAdapter(Context context, List<SchemeOfferImage> images) {
            mContext = context;
            offers = images;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.offers_view, collection, false);

            ImageView offerIm = (ImageView) layout.findViewById(R.id.imageView14);
            SchemeOfferImage offer = offers.get(position);

            Glide.with(mContext)
                    .load(offer.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(offerIm);

            layout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //v.getId() will give you the image id
                    try {
                        String offerUrl = offer.getUrl();

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(offerUrl));
                        mContext.startActivity(i);

                        App app = (App) mContext.getApplicationContext();
                        Tracker tracker = app.getAppComponent().tracker();
                        tracker.trackScreen(Screen.WebView, offerUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return offers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

}
