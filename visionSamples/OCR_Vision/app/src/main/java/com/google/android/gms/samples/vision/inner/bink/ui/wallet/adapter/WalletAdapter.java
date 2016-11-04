package com.google.android.gms.samples.vision.inner.bink.ui.wallet.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.config.AppConfig;
import com.loyaltyangels.bink.model.Balance;
import com.loyaltyangels.bink.model.common.Account;
import com.loyaltyangels.bink.model.common.Image;
import com.loyaltyangels.bink.model.common.ImageType;
import com.loyaltyangels.bink.model.common.Wallet;
import com.loyaltyangels.bink.model.payment.PaymentCardAccount;
import com.loyaltyangels.bink.model.scheme.Scheme;
import com.loyaltyangels.bink.model.scheme.SchemeAccount;
import com.loyaltyangels.bink.model.scheme.Tier;
import com.loyaltyangels.bink.util.UiUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by hansonaboagye on 03/08/16.
 */
public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = WalletAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_SCHEME_ACCOUNT = 0;
    private static final int VIEW_TYPE_PAYMENT_ACCOUNT = 1;

    public interface Listener {
        void onUpdateOrders(List<Account> accounts);

        void onSchemeClicked(SchemeAccount schemeAccount, Scheme scheme);

        void onPaymentCardClicked(PaymentCardAccount paymentCardAccount);

        void onHideEmptyState();

        void onShowWalletEmpty();

        void onShowNoLoyaltyCards();

        void onShowNoPaymentCards();
    }

    private AppConfig appConfig;
    private ArrayList<Account> tempAccountsList;
    private ArrayList<Account> accountsList;
    private ArrayList<Account> filteredResults;
    private ArrayList<Scheme> schemes;
    private String lastQuery = "";
    private Listener listener;
    private int lastLength;
    private @Px
    int topMarginForFirstItem;


    private ItemTouchHelperAdapter touchHelperAdapter = new ItemTouchHelperAdapter() {

        @Override
        public void onItemMove(int fromPosition, int toPosition, RecyclerView.ViewHolder fromHolder, RecyclerView.ViewHolder targetHolder) {
            Collections.swap(tempAccountsList, fromPosition, toPosition);

            updateOrders();

            notifyItemMoved(fromPosition, toPosition);

            if (fromPosition == 0 || toPosition == 0) {
                updateMarginsForItem(fromHolder);
                updateMarginsForItem(targetHolder);
            }
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return TextUtils.isEmpty(lastQuery); // No Dragging if we are searching
        }
    };

    public WalletAdapter(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void setWallet(Wallet wallet) {
        accountsList = new ArrayList<>();
        accountsList.addAll(wallet.getSchemeAccounts());
        accountsList.addAll(wallet.getPaymentCardAccounts());

        Collections.sort(accountsList);

        tempAccountsList = accountsList;

        notifyEmptyStates();
    }

    public void updateAccountItem(@NonNull Account update) {
        int updateIndex = -1;

        for (int i = 0; i < tempAccountsList.size(); i++) {
            Account existing = tempAccountsList.get(i);
            if (update.getId().equals(existing.getId())) {
                tempAccountsList.set(i, update);
                updateIndex = i;
            }
        }

        if (updateIndex != -1) {
            notifyItemChanged(updateIndex);
        } else {
            tempAccountsList.add(update);
            notifyItemInserted(tempAccountsList.size());
        }

        notifyEmptyStates();
        updateOrders();
    }

    public void deleteAccountItem(String accountId) {
        for (int i = 0; i < tempAccountsList.size(); i++) {
            if (accountId.equals(tempAccountsList.get(i).getId())) {
                tempAccountsList.remove(i);
                notifyItemRemoved(i);
                notifyEmptyStates();

                if (i == 0) {
                    notifyItemChanged(0);
                }

                updateOrders();
                break;
            }
        }
    }

    private void notifyEmptyStates() {
        boolean paymentCardExists = false;
        boolean loyaltyCardExists = false;

        for (Account account : tempAccountsList) {
            if (account instanceof PaymentCardAccount) {
                paymentCardExists = true;
            } else if (account instanceof SchemeAccount) {
                loyaltyCardExists = true;
            }

            if (paymentCardExists && loyaltyCardExists) {
                break;
            }
        }

        if (paymentCardExists && loyaltyCardExists) {
            listener.onHideEmptyState();
        } else if (!paymentCardExists && !loyaltyCardExists) {
            listener.onShowWalletEmpty();
        } else if (!paymentCardExists) {
            listener.onShowNoPaymentCards();
        } else {
            listener.onShowNoLoyaltyCards();
        }
    }

    private void updateOrders() {
        for (int i = 0; i < tempAccountsList.size(); i++) {
            tempAccountsList.get(i).setOrder(i);
        }

        accountsList = tempAccountsList;
        listener.onUpdateOrders(Collections.unmodifiableList(tempAccountsList));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setSchemes(ArrayList<Scheme> schemes) {
        this.schemes = schemes;
    }

    public void setTopMarginForFirstItem(@Px int marginTop) {
        topMarginForFirstItem = marginTop;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SCHEME_ACCOUNT) {
            return new WalletCardViewHolder(layoutInflater.inflate(R.layout.card, parent, false));
        } else {
            return new PaymentCardViewHolder(layoutInflater.inflate(R.layout.layout_payment_card_item, parent, false));
        }
    }

    private void updateMarginsForItem(RecyclerView.ViewHolder holder) {
        Context context = holder.itemView.getContext();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        float width = size.x;

        int height = (int) (width * 0.66);
        holder.itemView.getLayoutParams().height = height;

        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (holder.getAdapterPosition() >= 1) {
            //this is the percentage of height that should be hidden
            Integer marginTop = (int) (height * 0.74);
            params.setMargins(0, -marginTop, 0, 0);
        } else {
            params.setMargins(0, topMarginForFirstItem, 0, 0);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: position = [" + position + "]");
        Context context = holder.itemView.getContext();

        Account account = tempAccountsList.get(position);
        Image image = account.findImage(ImageType.HERO);
        Drawable placeholder;

        updateMarginsForItem(holder);

        if (holder instanceof WalletCardViewHolder) {
            WalletCardViewHolder schemeHolder = (WalletCardViewHolder) holder;

            SchemeAccount schemeAccount = (SchemeAccount) account;
            Scheme scheme = schemeAccount.getScheme();

            for (Scheme s : schemes) {
                if (s.getId().equals(scheme.getId())) {
                    scheme = s;
                }
            }

            schemeHolder.points.setVisibility(View.VISIBLE);
            schemeHolder.binkShow.setVisibility(View.VISIBLE);

            placeholder = new ColorDrawable(Color.parseColor(scheme.getColour()));
            schemeHolder.account.setTextColor(UiUtil.getColorForSchemeLabel(context, scheme));

            final Scheme immutableScheme = scheme;
            schemeHolder.itemView.setOnClickListener(view ->
                    listener.onSchemeClicked(schemeAccount, immutableScheme));

            SchemeAccount.Status status = schemeAccount.getStatus();
            Balance balance = schemeAccount.getBalance();


            if (status == SchemeAccount.Status.JOIN) {
                schemeHolder.points.setText(R.string.join_prompt);
                schemeHolder.points.setTextColor(Color.WHITE);
                schemeHolder.points.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.pinkcurve));
                schemeHolder.binkShow.setVisibility(View.VISIBLE);
                schemeHolder.binkShow.setImageDrawable(context.getResources().getDrawable(R.drawable.join_action));
            } else {

                schemeHolder.points.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.curve));
                schemeHolder.points.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                if (scheme.getTier() == Tier.TIER_1) {
//                            this is a partner you can bink with
                    schemeHolder.binkShow.setVisibility(View.VISIBLE);
                    schemeHolder.binkShow.setImageDrawable(context.getResources().getDrawable(R.drawable.pinktick));
                } else if (scheme.getTier() == Tier.TIER_2) {
                    //this is a show partner
                    schemeHolder.binkShow.setVisibility(View.VISIBLE);
                    schemeHolder.binkShow.setImageDrawable(context.getResources().getDrawable(R.drawable.barcode));
                } else {
                    schemeHolder.binkShow.setVisibility(View.GONE);
                }
                if (status != SchemeAccount.Status.ACTIVE) {
                    //check if member or if we have an agent for this card
                    //if its false means has_points is false which means we have no agent
                    if (scheme.hasPoints()) {
                        schemeHolder.points.setText(R.string.login_prompt);
                    } else {
                        schemeHolder.points.setText("");
                    }

                } else {
                    String points = UiUtil.formatSchemePointsLabel(appConfig, balance, scheme);
                    schemeHolder.points.setText(points);
                }
            }

            schemeHolder.cardBg.setImageBitmap(null);
            schemeHolder.card.setCardBackgroundColor(Color.parseColor(scheme.getColour()));
            schemeHolder.account.setVisibility(View.VISIBLE);
            schemeHolder.account.setText(scheme.getCompany());

            if (image != null) {
                Glide.with(context)
                        .load(image.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                schemeHolder.cardBg.setImageDrawable(placeholder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                schemeHolder.account.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(schemeHolder.cardBg);
            }

        } else {
            PaymentCardViewHolder paymentHolder = (PaymentCardViewHolder) holder;
            PaymentCardAccount paymentCardAccount = (PaymentCardAccount) account;

            paymentHolder.paymentCardView.setPaymentCard(paymentCardAccount);

            paymentHolder.itemView.setOnClickListener(view -> {
                listener.onPaymentCardClicked(paymentCardAccount);
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        Account account = tempAccountsList.get(position);
        if (account instanceof PaymentCardAccount) {
            return VIEW_TYPE_PAYMENT_ACCOUNT;
        } else {
            return VIEW_TYPE_SCHEME_ACCOUNT;
        }
    }

    @Override
    public int getItemCount() {
        if (tempAccountsList == null || schemes == null) {
            return 0;
        }

        return tempAccountsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if (charSequence.length() == 0) {
                    filteredResults = tempAccountsList;
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
                tempAccountsList = (ArrayList<Account>) filterResults.values;
                if (tempAccountsList == null) {
                    tempAccountsList = new ArrayList<>();
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(touchHelperAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private ArrayList<Account> getFilteredResults(String query) {
        query = query.toLowerCase();

        if (query.length() < lastLength) {
            tempAccountsList = accountsList;
        }

        lastLength = query.length();
        ArrayList<Account> filteredModelList = new ArrayList<>();
        for (Account model : tempAccountsList) {
            final String text = model.getSearchField().toLowerCase();
            if (text.startsWith(query) && !filteredModelList.contains(model)) {
                filteredModelList.add(model);
            }
        }
        // check the start of each word
        // String pattern = "\" + query + "\w+";
        if (filteredModelList.size() == 0) {
            for (Account model : tempAccountsList) {
                final String text = model.getSearchField().toLowerCase();
                if (text.contains(query) && !filteredModelList.contains(model)) {
                    filteredModelList.add(model);
                }
            }
        }

        return filteredModelList;
    }

    public void reset() {
        lastQuery = "";
        lastLength = 0;
        tempAccountsList = accountsList;

        new Handler().post(this::notifyDataSetChanged);
    }

}
