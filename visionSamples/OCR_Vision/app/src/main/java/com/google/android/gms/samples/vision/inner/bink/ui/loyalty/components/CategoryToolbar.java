package com.google.android.gms.samples.vision.inner.bink.ui.loyalty.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.SchemeCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hansonaboagye on 29/07/16.
 */
public class CategoryToolbar extends RelativeLayout {


    private boolean showing;
    private RecyclerView recyclerView;
    private ToolbarCategoryAdapter toolbarCategoryAdapter;

    public CategoryToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.loyalty_category_toolbar, this, true);
        loadCategories();
    }

    public void setOnCheckChangedListener(CompoundButton.OnCheckedChangeListener checkChangedListener) {
        selectCategoryListener = checkChangedListener;
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        for (int x = 0; x < recyclerView.getLayoutManager().getItemCount(); x++) {
            if (recyclerView.getLayoutManager().getChildAt(x) != null) {
                recyclerView.getLayoutManager().getChildAt(x).setClickable(clickable);
            }
        }
    }

    private void loadCategories() {
        recyclerView = (RecyclerView) findViewById(R.id.category_list);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        toolbarCategoryAdapter = new ToolbarCategoryAdapter();
        recyclerView.setAdapter(toolbarCategoryAdapter);
        toolbarCategoryAdapter.notifyDataSetChanged();
        showing = true;
    }

    public List<Integer> getOptionsSelected() {
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) recyclerView.getChildAt(i);
            if (checkBox.isChecked()) {
                // The 'Other' category is displayed last in the list
                // however it has a category value of 1
                // thus we must set it to one, though it will always be displayed last
                if (i == SchemeCategory.Other.ordinal()) {
                    results.add(1);
                } else {
                    results.add(i + 2);
                }
            }
        }
        return results;
    }

    private CompoundButton.OnCheckedChangeListener selectCategoryListener;

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }


    private class ToolbarCategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryViewHolder(new AppCompatCheckBox(getContext()));
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            SchemeCategory category = SchemeCategory.values()[position];
            holder.checkBox.setText(category.getNameId());
        }

        @Override
        public int getItemCount() {
            return SchemeCategory.values().length;
        }
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView;
            checkBox.setOnCheckedChangeListener(selectCategoryListener);
            checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
        }
    }
}
