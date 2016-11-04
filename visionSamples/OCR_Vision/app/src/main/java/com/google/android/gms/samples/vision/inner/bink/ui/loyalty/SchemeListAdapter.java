package com.google.android.gms.samples.vision.inner.bink.ui.loyalty;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lb.recyclerview_fast_scroller.RecyclerViewFastScroller;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeOfferImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hansonaboagye on 27/07/16.
 */
public class SchemeListAdapter extends RecyclerView.Adapter<SchemeListAdapter.LoyaltyViewHolder>
        implements Filterable, RecyclerViewFastScroller.BubbleTextGetter {

    public interface Listener {
        void onSchemeSelected(Scheme scheme);
    }

    private ArrayList<Scheme> schemeList;
    private ArrayList<Scheme> tempSchemeList;
    private Context context;
    private int lastCount = 0;
    private String lastQuery = "";
    private List<Integer> tempCategoryList = new ArrayList<>();
    private Listener listener;

    public SchemeListAdapter(Context context, ArrayList<Scheme> schemes, Listener listener) {
        this.context = context;
        schemeList = schemes;
        tempSchemeList = schemes;
        this.listener = listener;
    }

    @Override
    public LoyaltyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.new_loyalty_card_layout, parent, false);

        return new LoyaltyViewHolder(rootView);
    }

    public void filterCategory(List<Integer> categories) {
        ArrayList<Scheme> filteredModelList = new ArrayList<>();
        tempCategoryList = categories;
        lastCount = categories.size();
        tempSchemeList = schemeList;
        if (categories.size() > 0) {
            for (Integer catID : categories) {
                for (Scheme model : tempSchemeList) {
                    if (model.getCategory() == catID) {
                        filteredModelList.add(model);
                    }
                }
            }
            tempSchemeList = filteredModelList;
        }
        if (lastLength > 0 && !deletedQuery) {
            tempSchemeList = getFilteredResults(lastQuery);
        }

        ((Activity) context).runOnUiThread(() -> {
            Collections.sort(tempSchemeList);
            notifyDataSetChanged();
        });
    }


    @Override
    public void onBindViewHolder(LoyaltyViewHolder holder, int position) {
        Scheme cardScheme = tempSchemeList.get(position);
        String heroImageUrl = "";

        for (SchemeOfferImage schemeOfferImage : cardScheme.getImages()) {
            if (schemeOfferImage.getImageType() == ImageType.ICON) {
                heroImageUrl = schemeOfferImage.getImageUrl();
                break;
            }
        }
        holder.tagline_textView.setText(cardScheme.getCompany());
        holder.itemView.setTag(cardScheme);

        Glide.with(context)
                .load(heroImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.heroImageView);

        holder.itemView.setOnClickListener(view -> {
            if (!((SchemesListActivity) context).isCardInWallet(cardScheme.getId())) {
                listener.onSchemeSelected(cardScheme);
            } else {
                new AlertDialog.Builder(context)
                        .setMessage(String.format(context.getResources().getString(R.string.card_detection_scheme_exists), cardScheme.getCompany()))
                        .setTitle(context.getResources().getString(R.string.card_detection_scheme_exists_title))
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        if (tempSchemeList == null) {
            return 0;
        }
        return tempSchemeList.size();
    }

    boolean deletedQuery = false;
    ArrayList<Scheme> filteredResults;
    int lastLength = 0;

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if (charSequence.length() == 0) {
                    filteredResults = schemeList;
                } else {
                    filteredResults = getFilteredResults(charSequence.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                lastQuery = charSequence.toString();
                return results;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tempSchemeList = (ArrayList<Scheme>) filterResults.values;
                if (tempSchemeList == null) {
                    tempSchemeList = new ArrayList<>();
                }
                notifyDataSetChanged();
            }
        };
    }

    private ArrayList<Scheme> getFilteredResults(String query) {
        query = query.toLowerCase();

        if (query.length() < lastLength) {
            deletedQuery = true;
            tempSchemeList = schemeList;
            if (lastCount > 0) {
                filterCategory(tempCategoryList);
            }
        } else {
            deletedQuery = false;
        }
        lastLength = query.length();
        ArrayList<Scheme> filteredModelList = new ArrayList<>();
        for (Scheme model : tempSchemeList) {
            final String text = model.getCompany().toLowerCase();
            if (text.startsWith(query)) {
                filteredModelList.add(model);
            }
        }
        if (filteredModelList.size() == 0) {
            for (Scheme model : tempSchemeList) {
                final String text = model.getCompany().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }

        return filteredModelList;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        String character = tempSchemeList.get(pos).getCompany().substring(0, 1);
        return character;
    }

    public void reset() {
        lastQuery = "";
        lastLength = 0;
        tempSchemeList = schemeList;
        deletedQuery = true;
        if (lastCount > 0) {
            filterCategory(tempCategoryList);
        }
        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // No sort necessary here
                notifyDataSetChanged();
            }
        });
    }


    class LoyaltyViewHolder extends RecyclerView.ViewHolder {
        ImageView heroImageView;
        TextView tagline_textView;

        public LoyaltyViewHolder(View itemView) {
            super(itemView);
            heroImageView = (ImageView) itemView.findViewById(R.id.hero_imageView);
            tagline_textView = (TextView) itemView.findViewById(R.id.tagline_textView);
        }


    }
}
