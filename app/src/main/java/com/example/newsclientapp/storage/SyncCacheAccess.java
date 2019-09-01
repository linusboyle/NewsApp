/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import android.util.Log;

import com.example.newsclientapp.network.NewsEntity;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.concurrent.LinkedBlockingQueue;

public class SyncCacheAccess implements Runnable {
	private static final String TAG = "SyncCacheAccess";

	public enum CacheAction {
		CACHE_WRITE,
		// CACHE_READ,
		CACHE_DELETE,
		CACHE_FAVORITE_ADD,
		CACHE_FAVORITE_REMOVE,
	}

	static class CacheActionPair {
		CacheAction action;
		NewsEntity newsEntity;

		CacheActionPair (CacheAction action, NewsEntity newsEntity) {
			this.action = action;
			this.newsEntity = newsEntity;
		}

		CacheAction getAction () {
			return action;
		}

		NewsEntity getNewsEntity () {
			return newsEntity;
		}
	}

	private LinkedBlockingQueue<CacheActionPair> queue;
	private File cache_root;
	private File favorite_root;

	SyncCacheAccess (File cache_root, File favorite_root) {
		this.cache_root = cache_root;
		this.favorite_root = favorite_root;
		this.queue = new LinkedBlockingQueue<>();
	}

	boolean addAction(CacheActionPair cacheActionPair) {
		try {
			queue.put(cacheActionPair);
			return true;
		} catch (InterruptedException e) {
			Log.w(TAG, "addAction failed by interruption" + e.getMessage());
		}
		return false;
	}

	private void doAction(CacheActionPair cacheActionPair) {
		Log.i(TAG, "Start a new action");
		CacheAction cacheAction = cacheActionPair.getAction();
		NewsEntity newsEntity = cacheActionPair.getNewsEntity();

		try {
			switch (cacheAction) {
				case CACHE_WRITE: {
					File target = new File(cache_root, newsEntity.getNewsID());
					PrintStream printStream = new PrintStream(target);
					ObjectOutputStream objectOutputStream =
							new ObjectOutputStream(printStream);
					objectOutputStream.writeObject(newsEntity);
					objectOutputStream.close();
					printStream.close();
					break;
				}
				case CACHE_DELETE: {
					File target = new File(cache_root, newsEntity.getNewsID());
					if (target.exists())
						if (!target.delete())
							throw new IOException("Delete failed");
					break;
				}
				case CACHE_FAVORITE_ADD: {
					File target = new File(favorite_root, newsEntity.getNewsID());
					if (!target.createNewFile())
						throw new IOException("Create favorite file failed");
					break;
				}
				case CACHE_FAVORITE_REMOVE: {
					File target = new File(favorite_root, newsEntity.getNewsID());
					if (target.exists())
						if (!target.delete())
							throw new IOException("Delete favorite file failed");
					break;
				}
			}
		} catch (IOException e) {
			Log.w(TAG, "exception in queue action:" + e.getMessage());
		}
	}

	@Override
	public void run () {
		Log.i(TAG, "Thread start");
		try {
			while (true) {
				CacheActionPair cacheActionPair = queue.take();
				doAction(cacheActionPair);
			}
		} catch (InterruptedException e) {
			Log.i(TAG, "Interrupted" + e.getMessage());
		}
	}
}
