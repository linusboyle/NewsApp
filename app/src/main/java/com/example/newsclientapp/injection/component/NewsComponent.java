/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.injection.component;

import com.example.newsclientapp.injection.module.NewsModule;
import com.example.newsclientapp.ui.fragment.NewsFragment;
import com.example.newsclientapp.ui.fragment.RecommendFragment;
import com.example.newsclientapp.ui.fragment.SearchResultFragment;

import dagger.Component;

@Component(modules = NewsModule.class)
public interface NewsComponent {
	void inject(NewsFragment fragment);

	void inject(SearchResultFragment fragment);

	void inject(RecommendFragment fragment);
}
