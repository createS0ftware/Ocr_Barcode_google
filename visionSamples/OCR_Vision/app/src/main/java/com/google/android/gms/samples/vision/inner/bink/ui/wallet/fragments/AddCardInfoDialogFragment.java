package com.google.android.gms.samples.vision.inner.bink.ui.wallet.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyaltyangels.bink.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by bb on 04/08/16.
 */
public class AddCardInfoDialogFragment extends CustomDialogFragment {


    public static final int PAYMENT = 1;
    public static final int LOYALTY = 2;
    public static final int SECURITY = 3;

    private static final String TYPE = "type";
    private static final String IMAGES = "images";
    private static final String HEIGHT = "height";

    @Nullable
    @BindView(R.id.pager)
    ViewPager viewPager;

    @Nullable
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    @Nullable
    @BindView(R.id.close)
    ImageButton close;

    @Nullable
    @BindView(R.id.close_security)
    ImageButton closeSecurity;

    @BindView(R.id.hidden_panel)
    ViewGroup hiddenPanel;

    private Animation slideToRight;
    private Animation slideFromLeft;

    private boolean hiddenPanelShown = false;

    public static ArrayList<Integer> loyaltyImages() {
        ArrayList<Integer> helpImages = new ArrayList<Integer>();
        helpImages.add(R.drawable.helployalty1);
        helpImages.add(R.drawable.helployalty2);
        helpImages.add(R.drawable.helployalty3);
        helpImages.add(R.drawable.helployalty4);
        return helpImages;
    }

    public static ArrayList<Integer> paymentImages() {
        ArrayList<Integer> helpImages = new ArrayList<Integer>();
        helpImages.add(R.drawable.helppayment1);
        helpImages.add(R.drawable.helppayment2);
        helpImages.add(R.drawable.helppayment3);
        helpImages.add(R.drawable.helppayment4);
        return helpImages;
    }

    @Override
    protected int getLayoutRes() {
        Integer type = getArguments().getInt(TYPE);
        if (type != null) {
            switch (type) {
                case PAYMENT:
                    return R.layout.help_popup1;
                case LOYALTY:
                    return R.layout.help_popup2;
                case SECURITY: {
                    return R.layout.layout_security_promise;
                }
            }
        }
        return -1;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments().getInt(TYPE) != SECURITY) {
            loadImages();
            TextView helpDesc = (TextView) view.findViewById(R.id.help_desc);
            slideFromLeft = slideFromLeft(helpDesc, hiddenPanel);
            clickToSlide(helpDesc, hiddenPanel, slideFromLeft);
            closeButton(close);
            slideToRight = slideToRight(helpDesc, hiddenPanel);
            hiddenPanelCloseButton(hiddenPanel, slideToRight);
        } else {
            setCancelable(false);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
            closeButton(closeSecurity);
            hiddenPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().getLayoutParams().height = getArguments().getInt(HEIGHT);
        getView().invalidate();
        onBackPressForHiddenPanel();
    }

    private void onBackPressForHiddenPanel() {
        getView().setFocusableInTouchMode(true);
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (hiddenPanelShown) {
                        hiddenPanelShown = false;
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }

    private void closeButton(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void clickToSlide(TextView helpDesc, final ViewGroup hiddenPanel, final Animation slideFromLeft) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                hiddenPanel.startAnimation(slideFromLeft);
                hiddenPanelShown = true;
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        SpannableString ss = new SpannableString(helpDesc.getText());
        final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(clickableSpan, ss.toString().length() - 17, ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(fcs, ss.toString().length() - 17, ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helpDesc.setText(ss);
        helpDesc.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loadImages() {
        ArrayList<Integer> images = getArguments().getIntegerArrayList(IMAGES);
        viewPager.setAdapter(new HelpPagerAdapter(images));
        indicator.setViewPager(viewPager);
    }

    private void hiddenPanelCloseButton(View view, final Animation slideToRight) {
        closeSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenPanel.startAnimation(slideToRight);
                hiddenPanelShown = false;
            }
        });
    }

    @NonNull
    private Animation slideToRight(final TextView helpDesc, final ViewGroup hiddenPanel) {
        final Animation slideToRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_to_right_help);
        slideToRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                helpDesc.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hiddenPanel.setVisibility(View.GONE);
                hiddenPanel.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
        return slideToRight;
    }

    @NonNull
    private Animation slideFromLeft(final TextView helpDesc, final ViewGroup hiddenPanel) {
        final Animation slideFromLeft = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_from_left_help);
        slideFromLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hiddenPanel.setVisibility(View.VISIBLE);
                hiddenPanel.clearAnimation();
                helpDesc.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
        return slideFromLeft;
    }

    public static AddCardInfoDialogFragment newInstance(ArrayList<Integer> images, int type, int height) {
        if (images == null) {
            throw new IllegalArgumentException("AddCardInfoDialogFragment expects a list of images.");
        }
        AddCardInfoDialogFragment fragment = new AddCardInfoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putIntegerArrayList(IMAGES, images);
        args.putInt(HEIGHT, height);
        fragment.setArguments(args);
        return fragment;

    }

    class HelpPagerAdapter extends PagerAdapter {

        private List<Integer> images;

        HelpPagerAdapter(List<Integer> images) {
            super();
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            ImageView imageView = new ImageView(container.getContext());
            imageView.setLayoutParams(params);
            imageView.setImageResource(images.get(position));

            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}