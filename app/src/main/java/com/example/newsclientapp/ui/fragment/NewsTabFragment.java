package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newsclientapp.R;
import com.example.newsclientapp.core.NewsCategory;
import com.example.newsclientapp.ui.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NewsTabFragment extends BaseFragment {
    private static final int PRIMARY_COLOR = 0xFFBB86FC;
    private static final int TEXT_COLOR = 0xFFCECECE;

    @BindView(R.id.tab_function) ImageView mTabFunc;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tab_choose_scroll) NestedScrollView mTabChoose;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab;
    }

    @Override
    protected void initData(ViewGroup container, Bundle savedInstanceState) {
        List<String> categoryList = NewsCategory.getCategories();
        List<Fragment> fragmentList = new ArrayList<>();
        for (String category : categoryList) {
            fragmentList.add(NewsFragment.newNewsFragment(category));
        }
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), categoryList, fragmentList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabChoose.setVisibility(View.GONE);

        mTabFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // TODO
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
}

