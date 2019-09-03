/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.core;

import android.app.Application;

import com.example.newsclientapp.storage.StorageManager;

public class NewsClientApp extends Application {
	@Override
	public void onCreate () {
		super.onCreate();
		// exception handler
		ExceptionHandler.getInstance().init(this);

		// storage
		StorageManager.getInstance().init(this);
	}
}
