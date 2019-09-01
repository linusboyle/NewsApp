/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.newsclientapp.ui.fragment.NewsFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private List<String> mTitleList;
	// private List<Fragment> mFragmentList;

	public ViewPagerAdapter(FragmentManager fm, List<String> titleList/*, List<Fragment> fragmentList*/) {
		super(fm);
		this.mTitleList = titleList;
		// this.mFragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		return NewsFragment.newNewsFragment(mTitleList.get(position));
	}

	@Override
	public int getCount() {
		return mTitleList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitleList.get(position);
	}

	@Override
	public int getItemPosition(Object item) {
		// NewsFragment newsFragment = (NewsFragment) item;
		// String category = newsFragment.getCategory();
		// int position = mTitleList.indexOf(category);

		// if (position >= 0)
		// 	return position;
		// else
		// 	return POSITION_NONE;
		return POSITION_NONE;
	}

	public void setData(List<String> titleList/*, List<Fragment> fragmentList*/) {
		this.mTitleList = titleList;
		notifyDataSetChanged();
		// this.mFragmentList = fragmentList;
	}

}
