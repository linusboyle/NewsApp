/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.FragmentTransaction;

import com.example.newsclientapp.R;

import butterknife.BindView;

public class SearchFragment extends BaseFragment {

	@BindView(R.id.search_bar)
	SearchView mView;

	@Override
	protected int getLayoutId () {
		return R.layout.fragment_search;
	}

	@Override
	protected void initData (ViewGroup container, Bundle savedInstanceState) {
		mView.setQueryRefinementEnabled(true);
		mView.setIconifiedByDefault(false);
		mView.setSubmitButtonEnabled(true);

		mView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit (String s) {
				changeFragment(s);
				return false;
			}

			@Override
			public boolean onQueryTextChange (String s) {
				return false;
			}
		});
	}

	// call this only for the first time
	private void changeFragment (String keyword) {
		SearchResultFragment searchResultFragment = SearchResultFragment.newSearchResultFragment(keyword);
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.search_frame, searchResultFragment);
		fragmentTransaction.commit();
	}
}
