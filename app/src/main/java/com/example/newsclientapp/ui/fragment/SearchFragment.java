/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.newsclientapp.R;
import com.example.newsclientapp.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements SearchView.OnQueryTextListener {

	@BindView(R.id.search_bar)
	SearchView mView;

	private final static String TAG = "SearchFragment";

	private final ArrayList<String> suggestionsArray = new ArrayList<>();
	private List<String> historyArray;
	private SuggestAdapter suggestAdapter;

	@Override
	protected int getLayoutId () {
		return R.layout.fragment_search;
	}

	@Override
	protected void initData (ViewGroup container, Bundle savedInstanceState) {
		historyArray = StorageManager.getInstance().getSearchHistorySync();
		mView.setQueryRefinementEnabled(true);
		mView.setIconifiedByDefault(false);
		mView.setSubmitButtonEnabled(true);

		try {
			AutoCompleteTextView autoCompleteTextView = mView.findViewById(androidx.appcompat.R.id.search_src_text);
			autoCompleteTextView.setThreshold(0);
		} catch (Exception e) {
			Log.e(TAG, "Autocomplete hack failed: " + e.getMessage());
		}

		suggestAdapter = new SuggestAdapter(getContext(), getCursor(suggestionsArray), suggestionsArray);
		mView.setSuggestionsAdapter(suggestAdapter);
		mView.setOnQueryTextListener(this);
		suggestAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onQueryTextSubmit (String s) {
		mView.clearFocus();
		changeFragment(s);
		if (!historyArray.contains(s)) {
			historyArray.add(s);
			if (!StorageManager.getInstance().updateSearchHistory(historyArray))
				Log.w(TAG, "update search history has failed");
		}

		suggestionsArray.clear();
		MatrixCursor matrixCursor = getCursor(suggestionsArray);
		suggestAdapter = new SuggestAdapter(getContext(), matrixCursor, suggestionsArray);
		mView.setSuggestionsAdapter(suggestAdapter);
		suggestAdapter.notifyDataSetChanged();

		return false;
	}

	@Override
	public boolean onQueryTextChange (String s) {
		suggestionsArray.clear();

		for (int i = 0; i < historyArray.size(); i++) {
			if (historyArray.get(i).contains(s)) {
				suggestionsArray.add(historyArray.get(i));
			}
		}

		final MatrixCursor matrixCursor = getCursor(suggestionsArray);
		suggestAdapter = new SuggestAdapter(getContext(), matrixCursor, suggestionsArray);
		mView.setSuggestionsAdapter(suggestAdapter);
		suggestAdapter.notifyDataSetChanged();

		return true;
	}

	private void changeFragment (String keyword) {
		SearchResultFragment searchResultFragment = SearchResultFragment.newSearchResultFragment(keyword);
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.search_frame, searchResultFragment);
		fragmentTransaction.commit();
	}

	private class SuggestAdapter extends CursorAdapter implements View.OnClickListener {

		private final static String TAG = "SuggestAdapter";
		private final ArrayList<String> mObjects;
		private final LayoutInflater mInflater;
		private TextView tvSearchTerm;

		SuggestAdapter (final Context ctx, final Cursor cursor, final ArrayList<String> mObjects) {
			super(ctx, cursor, 0);

			this.mObjects = mObjects;
			this.mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View newView(final Context ctx, final Cursor cursor, final ViewGroup parent) {
			final View view = mInflater.inflate(R.layout.list_item_search, parent, false);

			tvSearchTerm = view.findViewById(R.id.tvSearchTerm);

			return view;
		}

		@Override
		public void bindView(final View view, final Context ctx, final Cursor cursor) {

			tvSearchTerm = view.findViewById(R.id.tvSearchTerm);

			final int position = cursor.getPosition();

			if (cursorInBounds(position)) {
				final String term = mObjects.get(position);
				tvSearchTerm.setText(term);

				view.setTag(position);
				view.setOnClickListener(this);
			} else {
				Log.e(TAG, "cursor out of bound in bindView");
			}
		}

		private boolean cursorInBounds(final int position) {
			return position < mObjects.size();
		}

		@Override
		public void onClick(final View view) {
			final int position = (Integer)view.getTag();
			if (cursorInBounds(position)) {
				final String selected = mObjects.get(position);
				SearchFragment.this.mView.setQuery(selected, true);
			} else {
				Log.e(TAG, "cursor out of bound in onclick");
			}
		}
	}

	private final static String COLUMN_ID = "_id";
	private static final String COLUMN_TERM = "term";
	private static final String DEFAULT = "default";

	private MatrixCursor getCursor(final ArrayList<String> suggestions) {

		final String[] columns = new String[] { COLUMN_ID, COLUMN_TERM };
		final Object[] object = new Object[] { 0, DEFAULT };

		final MatrixCursor matrixCursor = new MatrixCursor(columns);

		for (int i = 0; i < suggestions.size(); i++) {
			object[0] = i;
			object[1] = suggestions.get(i);

			matrixCursor.addRow(object);
		}

		return matrixCursor;
	}
}
