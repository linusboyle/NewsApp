/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.newsclientapp.ui.view.FavoriteView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FavoritePresenter {
	private FavoriteView _view;
	private static final String TAG = "FavoritePresenter";

	public FavoritePresenter (FavoriteView _view) {
		this._view = _view;
	}

	@SuppressLint("CheckResult")
	public void requestFavorite(Context context) {
		//noinspection ResultOfMethodCallIgnored
		StorageManager.getInstance().getAllFavorite(context)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(storageResponse -> _view.onFavoriteResponsed(storageResponse),
						throwable -> {
							Log.w(TAG, "request error");
							_view.onFavoriteFailed(throwable.getMessage());
						});
	}
}
