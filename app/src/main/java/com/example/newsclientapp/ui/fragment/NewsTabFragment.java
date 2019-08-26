package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newsclientapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<String> titleList = Arrays.asList("社会", "国内", "国际", "体育", "NBA", "足球", "科技",
                "创业", "苹果", "军事", "移动", "旅游", "健康", "奇闻", "VR", "IT");
        List<String> categoryList = Arrays.asList("social", "guonei", "world", "tiyu", "nba",
                "football", "keji", "startup", "apple", "military", "mobile", "travel", "health",
                "qiwen", "vr", "it");
        List<Fragment> fragmentList = new ArrayList<>();
        // for (String category : categoryList) {
        //     fragmentList.add(NewsFragment.newNewsFragment(category));
        // }
        // mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), titleList, fragmentList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
