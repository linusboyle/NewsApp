package com.example.newsclientapp.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newsclientapp.R;
import com.example.newsclientapp.core.NewsCategory;
import com.example.newsclientapp.ui.adapter.ViewPagerAdapter;
import com.example.newsclientapp.ui.view.DraggableGridView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class NewsTabFragment extends BaseFragment {
    private static final int PRIMARY_COLOR = 0xFFBB86FC;
    private static final int TEXT_COLOR = 0xFFCECECE;
    private static final int GROUND_PRIMARY_COLOR = 0xFF1F1B24;
    private static final int GROUND_02dp_COLOR = 0xFF242424;

    @BindView(R.id.tab_function) ImageView mTabFunc;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tab_choose_scroll) LinearLayout mTabChoose;
    @BindView(R.id.tab_choose_gridlayout) DraggableGridView mTabGridlayout;

    private List<String> categoryChosen;
    private HashMap<String, Boolean> categoryChosenMap;
    private List<ChooseCardAdapter> chooseCard;

    float dp2pixel(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab;
    }

    @Override
    protected void initData(ViewGroup container, Bundle savedInstanceState) {
        // init tab layout
    	List<Fragment> fragmentList = getFragmentList();
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), categoryChosen, fragmentList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mTabLayout.setupWithViewPager(mViewPager);

        // init tab choose card
        initChooseCard();
        mTabChoose.setVisibility(View.GONE);

        mTabFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // TODO update chosen list
                if (mViewPager.getVisibility() == View.VISIBLE) {
                    mTabFunc.setColorFilter(PRIMARY_COLOR);
                    mViewPager.setVisibility(View.GONE);
                    mTabChoose.setVisibility(View.VISIBLE);
                    setTabsEnabled(false);
                } else {
                    mTabFunc.setColorFilter(TEXT_COLOR);
                    mViewPager.setVisibility(View.VISIBLE);
                    mTabChoose.setVisibility(View.GONE);
                    setTabsEnabled(true);
                }
            }
        });
    }

    private void initChooseCard() {
    	mTabGridlayout.removeAllViews();
    	chooseCard = new ArrayList<>();

    	for (String category: NewsCategory.getCategories()) {
    	    addChooseCard(category, categoryChosenMap.get(category));
        }
    }

    private void addChooseCard(String category, Boolean chosen) {
        View tabChooseCard = LayoutInflater.from(getContext())
                .inflate(R.layout.rv_item_tabchoose, mTabGridlayout, false);

        chooseCard.add(new ChooseCardAdapter(tabChooseCard, category, chosen));
        mTabGridlayout.addView(tabChooseCard);
    }

    private List<Fragment> getFragmentList() {
        if (categoryChosen == null)
        	initCategoryChosen();

        List<Fragment> fragmentList = new ArrayList<>();
        for (String category : categoryChosen)
            fragmentList.add(NewsFragment.newNewsFragment(category));

        return fragmentList;
    }

    private void initCategoryChosen() {
        // TODO: restore
        categoryChosen = NewsCategory.getCategories();

        categoryChosenMap = new HashMap<>();
        for (String category: NewsCategory.getCategories())
            categoryChosenMap.put(category, false);
        for (String category: categoryChosen)
            categoryChosenMap.put(category, true);
    }

    private ViewGroup getTabViewGroup() {
        ViewGroup viewGroup = null;
        if (mTabLayout.getChildCount() > 0) {
            View view = mTabLayout.getChildAt(0);
            if (view instanceof ViewGroup)
                viewGroup = (ViewGroup) view;
        }
        return viewGroup;
    }

    private void setTabsEnabled(boolean enable) {
        ViewGroup viewGroup = getTabViewGroup();
        if (viewGroup != null)
            for (int childIndex = 0; childIndex < viewGroup.getChildCount(); childIndex++) {
                View tabView = viewGroup.getChildAt(childIndex);
                if (tabView != null)
                    tabView.setEnabled(enable);
            }
    }

    private View getTabView(int position) {
        View tabView = null;
        ViewGroup viewGroup = getTabViewGroup();
        if (viewGroup != null && viewGroup.getChildCount() > position)
        	tabView = viewGroup.getChildAt(position);
        return tabView;
    }

    private class ChooseCardAdapter {
        private ImageView mImageView;
        private TextView mTextView;
        private CardView mCardView;

        private Boolean chosen;
        private String category;

        ChooseCardAdapter(View view, String category, Boolean chosen) {
            mCardView = (CardView) view;
            mImageView = view.findViewById(R.id.tab_choose_cardview_image);
            mTextView = view.findViewById(R.id.tab_choose_cardview_text);
            this.chosen = chosen;
            this.category = category;

            mTextView.setText(category);
            setChosen(true);

	        // mCardView.setOnClickListener(new View.OnClickListener() {
	        // 	@Override
            //     public void onClick(View view) {
            //         String category = getCategory();
            //         categoryChosenMap.put(category, toggle());
            //     }
            // });
        }

        public Boolean isChosen() {
            return chosen;
        }

        public void setChosen(Boolean chosen) {
            this.chosen = chosen;
            if (chosen) {
                mTextView.setTextColor(PRIMARY_COLOR);
                mImageView.setColorFilter(PRIMARY_COLOR);
                // mCardView.setBackgroundColor(GROUND_PRIMARY_COLOR);
                setBackground(GROUND_02dp_COLOR);
            } else {
                mTextView.setTextColor(TEXT_COLOR);
                mImageView.setColorFilter(TEXT_COLOR);
                setBackground(GROUND_02dp_COLOR);
            }
        }

        public String getCategory() {
            return this.category;
        }

        public Boolean toggle() {
        	setChosen(!chosen);
        	return chosen;
        }

        public void setBackground(int color) {
            GradientDrawable gdDefault = new GradientDrawable();
            gdDefault.setColor(color);
            gdDefault.setCornerRadius(dp2pixel(4));
            mCardView.setBackground(gdDefault);
        }
    }
}

