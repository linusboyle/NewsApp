/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FileLoader<T> extends AsyncTask<File, Void, T> {

	private static final String TAG = "FileLoader";
	private FileLoadFunc<T> func;

	FileLoader (FileLoadFunc<T> func) {
		this.func = func;
	}

	@Override
	protected T doInBackground (File... files) {
		File file = files[0];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream  = new ObjectInputStream(fileInputStream);
			T retval = (T) objectInputStream.readObject();
			fileInputStream.close();
			objectInputStream.close();
			return retval;
		} catch (IOException e) {
			Log.e(TAG, "cache miss: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "cache corrupted: " + e.getMessage());
		}

		return null;
	}

	@Override
	protected void onPostExecute(T result) {
		func.onLoad(result);
	}
}
