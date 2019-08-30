/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.injection.module;

import com.example.newsclientapp.ui.view.StorageView;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
	private StorageView _view;

	public StorageModule(StorageView view) {
		_view = view;
	}

	@Provides
	public StorageView providesStorageView() {
		return _view;
	}
}
