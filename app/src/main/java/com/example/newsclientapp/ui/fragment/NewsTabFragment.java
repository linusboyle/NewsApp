package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;
import android.view.ViewGroup;

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
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

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
    }
}
