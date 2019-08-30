package com.example.newsclientapp.ui.fragment;

import java.util.HashMap;

public final class FragmentFactory {
    public enum FragmentEnum {
        NEWS_TAB_FRAGMENT,
        CACHE_FRAGMENT
    }

    private static HashMap<FragmentEnum, Class<? extends BaseFragment>> fragmentClassMap;

    static {
        fragmentClassMap = new HashMap<>();
        fragmentClassMap.put(FragmentEnum.NEWS_TAB_FRAGMENT, NewsTabFragment.class);
        fragmentClassMap.put(FragmentEnum.CACHE_FRAGMENT, CacheFragment.class);
    }

    public static Class<? extends BaseFragment> getFragmentClass(FragmentEnum fIndex) {
        // return null if not exists
        return fragmentClassMap.get(fIndex);
    }

    public static BaseFragment getFragmentInstance(FragmentEnum fIndex) throws Exception {
        // no check
        return fragmentClassMap.get(fIndex).getConstructor().newInstance();
    }
}
