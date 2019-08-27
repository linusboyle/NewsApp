/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.network;

import android.annotation.SuppressLint;

import android.util.Log;
import android.widget.Toast;
import com.example.newsclientapp.ui.view.NewsView;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewsPresenter implements Presenter {
	private NewsView _view;
	private NewsService _service;

	@Inject
	public NewsPresenter(NewsView view, NewsService service) {
		_view = view;
		_service = service;
	}

	@SuppressLint("CheckResult")
	@Override
	public void requestNews (int size, String startDate, String endDate, String words, String categories) {
		_service.getNews(size, startDate, endDate, words, categories)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.compose(_view.bindToLife())
				.subscribe(newsResponse -> {
							System.exit(1);
							_view.onNewsResponsed(newsResponse);

						},
						throwable -> _view.onFailed(throwable.getMessage()));
	}
}
