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
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

class FileStorePair {
	File file;
	StorageEntity entity;

	FileStorePair (File file, StorageEntity entity) {
		this.file = file;
		this.entity = entity;
	}
}

public class FileStorer extends AsyncTask<FileStorePair, Void, Void> {

	private static final String TAG = "FileStorer";

	FileStorer () { }

	@Override
	protected Void doInBackground (FileStorePair... pairs) {
		FileStorePair pair = pairs[0];
		try {
			PrintStream printStream = new PrintStream(pair.file);
			ObjectOutputStream objectOutputStream =
					new ObjectOutputStream(printStream);
			objectOutputStream.writeObject(pair.entity);
			objectOutputStream.close();
			printStream.close();
		} catch (IOException e) {
			Log.e(TAG, "cache miss: " + e.getMessage());
		}

		return null;
	}

}
