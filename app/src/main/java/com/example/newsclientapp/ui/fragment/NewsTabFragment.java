package com.example.newsclientapp.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.example.newsclientapp.BuildConfig;
import com.example.newsclientapp.R;
import com.example.newsclientapp.core.NewsCategory;
import com.example.newsclientapp.storage.StorageManager;
import com.example.newsclientapp.ui.adapter.ViewPagerAdapter;
import com.example.newsclientapp.ui.view.DraggableGridView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;

import static com.example.newsclientapp.core.ThemeUtils.getAttrColor;

public class NewsTabFragment extends BaseFragment {
    private static final String TAG = "NewsTabFragment";

    @BindView(R.id.tab_function) ImageView mTabFunc;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tab_choose) LinearLayout mTabChoose;
    @BindView(R.id.tab_choose_gridlayout) DraggableGridView mTabGridlayout;

    private HashMap<String, ChooseCardAdapter> category2AdapterMap;

    private ViewPagerAdapter mViewPagerAdapter;

    private float dp2pixel (@SuppressWarnings("SameParameterValue") int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab;
    }

    @Override
    protected void initData(ViewGroup container, Bundle savedInstanceState) {
        // restore  categoryChosenList
        List<String> categoryChosenList = restoreCategoryChosen();

        // init tab choose card according to categoryChosenList
        initChooseCard(categoryChosenList);

        // init tabLayout and viewPager
	    setTabFragment(categoryChosenList);

        //noinspection Convert2Lambda
        mTabGridlayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = ((TextView) view.findViewById(R.id.tab_choose_cardview_text)).getText().toString();
                ChooseCardAdapter cardClicked = category2AdapterMap.get(category);
                if (cardClicked != null) {
                	cardClicked.toggle();
                } else {
                    Log.e(TAG, "Position undefined: " + position);
                }
            }
        });
        mTabChoose.setVisibility(View.GONE);

        //noinspection Convert2Lambda
        mTabFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (mViewPager.getVisibility() == View.VISIBLE) {
                    mTabFunc.setColorFilter(getAttrColor(getContext(), R.attr.colorPrimary));
                    mViewPager.setVisibility(View.GONE);
                    mTabChoose.setVisibility(View.VISIBLE);
                    setTabsEnabled(false);
                } else {
                    setTabsEnabled(true);
                    mTabFunc.setColorFilter(getAttrColor(getContext(), R.attr.mTextPrimary));
                    mViewPager.setVisibility(View.VISIBLE);
                    mTabChoose.setVisibility(View.GONE);
                    List<String> chosenCategoryList = getChosenCategoryList();
                    if (!StorageManager.getInstance().updateTabHistory(chosenCategoryList)) {
                        Log.w(TAG, "updating the tabs config has failed!");
                    }
                    setTabFragment(chosenCategoryList);
                }
            }
        });
    }

    private void initChooseCard(List<String> categoryChosenList) {
    	mTabGridlayout.removeAllViews();
    	category2AdapterMap = new HashMap<>();

        HashSet<String> unchosenCategoryPool = new HashSet<>(NewsCategory.getCategories());

    	// add chosen category according to sequence
    	for (String category: categoryChosenList) {
    	    if (BuildConfig.DEBUG && !NewsCategory.getCategories().contains(category)) {
    	        throw new AssertionError();
            }
            addChooseCard(category, true);
            unchosenCategoryPool.remove(category);
        }

    	for (String category: unchosenCategoryPool) {
            addChooseCard(category, false);
        }
    }

    private void addChooseCard(String category, Boolean chosen) {
        ChooseCardAdapter chooseCardAdapter = category2AdapterMap.get(category);
        if (chooseCardAdapter != null) {
            category2AdapterMap.remove(category);
        }

        View tabChooseCard = LayoutInflater.from(getContext())
                .inflate(R.layout.rv_item_tabchoose, mTabGridlayout, false);

        chooseCardAdapter = new ChooseCardAdapter(tabChooseCard, category, chosen);
        category2AdapterMap.put(category, chooseCardAdapter);
        mTabGridlayout.addView(tabChooseCard);
    }

    private void setTabFragment(List<String> chosenCategoryList) {
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), chosenCategoryList/*, fragmentList*/);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setCurrentItem(0);
            mViewPager.setOffscreenPageLimit(chosenCategoryList.size()-1/*fragmentList.size() - 1*/);
        } else {
            mViewPagerAdapter.setData(chosenCategoryList/*, fragmentList*/);
            mViewPager.setOffscreenPageLimit(chosenCategoryList.size()-1);
        }
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private List<String> getChosenCategoryList() {
        List<String> categoryChosenList = new ArrayList<>();
        int childCount = mTabGridlayout.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            View childView = mTabGridlayout.getChildAt(childIndex);
            if (childView != null) {
                String category = ((TextView) childView.findViewById(R.id.tab_choose_cardview_text)).getText().toString();
                ChooseCardAdapter chooseCardAdapter = category2AdapterMap.get(category);
                if (chooseCardAdapter != null && chooseCardAdapter.isChosen())
                    categoryChosenList.add(chooseCardAdapter.getCategory());
            }
        }
        return categoryChosenList;
    }

    private List<String> restoreCategoryChosen() {
        return StorageManager.getInstance().getTabHistory();
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
        private ImageView mImageMark;
        private ImageView mImageView;
        private TextView mTextView;
        private CardView mCardView;

        private Boolean chosen;
        private String category;

        ChooseCardAdapter(View view, String category, Boolean chosen) {
            mCardView = (CardView) view;
            mImageMark = view.findViewById(R.id.tab_choose_cardview_mark);
            mImageView = view.findViewById(R.id.tab_choose_cardview_image);
            mTextView = view.findViewById(R.id.tab_choose_cardview_text);
            this.chosen = chosen;
            this.category = category;

            mTextView.setText(category);
            setChosen(chosen);
        }

        public Boolean isChosen() {
            return chosen;
        }

        public void setChosen(Boolean chosen) {
            this.chosen = chosen;
            if (chosen) {
            	mImageMark.setVisibility(View.INVISIBLE);
                setBackground(getAttrColor(getContext(), R.attr.mBackground_02dp));
            } else {
                mImageMark.setVisibility(View.VISIBLE);
                mImageMark.setColorFilter(getAttrColor(getContext(), R.attr.colorSecondary));
                setBackground(getAttrColor(getContext(), R.attr.mBackground_02dp));
            }
        }

        public String getCategory() {
            return this.category;
        }

        @SuppressWarnings("UnusedReturnValue")
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