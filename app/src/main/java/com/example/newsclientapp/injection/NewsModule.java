/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.injection;

import com.example.newsclientapp.network.NewsService;
import com.example.newsclientapp.network.RetrofitClient;
import com.example.newsclientapp.ui.view.NewsView;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsModule {
	private NewsView _view;

	public NewsModule(NewsView view) {
		_view = view;
	}

	@Provides
	public NewsView provideNewsView() {
		return _view;
	}

	@Provides
	public NewsService provideNewsService() {
		return RetrofitClient.getNewsService();
	}
}
