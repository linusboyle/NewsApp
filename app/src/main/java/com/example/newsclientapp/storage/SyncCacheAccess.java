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

	public enum CacheActionEnum {
		CACHE_WRITE,
		CACHE_DELETE,
		CACHE_FAVORITE_ADD,
		CACHE_FAVORITE_REMOVE,
		STRING_WIRTE,
		OBJECT_WRITE,
	}

	private class CacheAction {
		CacheActionEnum action;
		NewsEntity newsEntity;
		Object object;
		File file;

		CacheAction () {}
	}

	private LinkedBlockingQueue<CacheAction> queue;
	private File cache_root;
	private File favorite_root;

	SyncCacheAccess (File cache_root, File favorite_root) {
		this.cache_root = cache_root;
		this.favorite_root = favorite_root;
		this.queue = new LinkedBlockingQueue<>();
	}

	boolean addCache(NewsEntity newsEntity) {
		CacheAction cacheAction = new CacheAction();
		cacheAction.newsEntity = newsEntity;
		cacheAction.action = CacheActionEnum.CACHE_WRITE;
		return addAction(cacheAction);
	}

	boolean removeCache(NewsEntity newsEntity) {
		CacheAction cacheAction = new CacheAction();
		cacheAction.newsEntity = newsEntity;
		cacheAction.action = CacheActionEnum.CACHE_DELETE;
		return addAction(cacheAction);
	}

	boolean setFavorite(NewsEntity newsEntity) {
		CacheAction cacheAction = new CacheAction();
		cacheAction.newsEntity = newsEntity;
		cacheAction.action = CacheActionEnum.CACHE_FAVORITE_ADD;
		return addAction(cacheAction);
	}

	boolean unsetFavorite(NewsEntity newsEntity) {
		CacheAction cacheAction = new CacheAction();
		cacheAction.newsEntity = newsEntity;
		cacheAction.action = CacheActionEnum.CACHE_FAVORITE_REMOVE;
		return addAction(cacheAction);
	}

	boolean writeObject(File target, Object object) {
		CacheAction cacheAction = new CacheAction();
		cacheAction.object = object;
		cacheAction.file = target;
		cacheAction.action = CacheActionEnum.OBJECT_WRITE;
		return addAction(cacheAction);
	}

	boolean writeString(File target, String string) {
		CacheAction cacheAction = new CacheAction();
		cacheAction.object = string;
		cacheAction.file = target;
		cacheAction.action = CacheActionEnum.STRING_WIRTE;
		return addAction(cacheAction);
	}

	private boolean addAction(CacheAction cacheAction) {
		try {
			queue.put(cacheAction);
			return true;
		} catch (InterruptedException e) {
			Log.w(TAG, "addAction failed by interruption" + e.getMessage());
		}
		return false;
	}

	private void doAction(CacheAction cacheAction) {
		Log.i(TAG, "Start a new action");
		CacheActionEnum cacheActionEnum = cacheAction.action;

		try {
			switch (cacheActionEnum) {
				case CACHE_WRITE: {
					NewsEntity newsEntity = cacheAction.newsEntity;
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
					NewsEntity newsEntity = cacheAction.newsEntity;
					File target = new File(cache_root, newsEntity.getNewsID());
					if (target.exists())
						if (!target.delete())
							throw new IOException("Delete failed");
					break;
				}
				case CACHE_FAVORITE_ADD: {
					NewsEntity newsEntity = cacheAction.newsEntity;
					File target = new File(favorite_root, newsEntity.getNewsID());
					if (!target.createNewFile())
						throw new IOException("Create favorite file failed");
					break;
				}
				case CACHE_FAVORITE_REMOVE: {
					NewsEntity newsEntity = cacheAction.newsEntity;
					File target = new File(favorite_root, newsEntity.getNewsID());
					if (target.exists())
						if (!target.delete())
							throw new IOException("Delete favorite file failed");
					break;
				}
				case OBJECT_WRITE: {
					Object object = cacheAction.object;
					File target = cacheAction.file;
					PrintStream printStream = new PrintStream(target);
					ObjectOutputStream objectOutputStream =
							new ObjectOutputStream(printStream);
					objectOutputStream.writeObject(object);
					objectOutputStream.close();
					printStream.close();
					break;
				}
				case STRING_WIRTE: {
					String string = (String)cacheAction.object;
					assert string != null;
					File target = cacheAction.file;
					PrintStream printStream = new PrintStream(target);
					printStream.println(string);
					printStream.close();
					break;
				}
			}
		} catch (IOException e) {
			Log.w(TAG, "IOException in queue action:" + e.getMessage());
		}
	}

	@Override
	public void run () {
		Log.i(TAG, "Thread start");
		try {
			while (true) {
				CacheAction cacheAction = queue.take();
				doAction(cacheAction);
			}
		} catch (InterruptedException e) {
			Log.i(TAG, "Interrupted" + e.getMessage());
		} catch (Exception e) {
			Log.i(TAG, "Exception thrown inside message thread:" + e.getMessage());
		}
	}
}
