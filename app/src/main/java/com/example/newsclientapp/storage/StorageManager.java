/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import android.content.Context;
import android.util.Log;


import com.example.newsclientapp.network.NewsEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StorageManager {
	private static final String CACHE_DIR = "cache";
	private static final String FAVORITE_DIR= "favorite";
	private static final String TAG = "StorageManager";
	private static StorageManager instance;

	private ArrayList<String> favorites = new ArrayList<>();
	private ArrayList<String> caches = new ArrayList<>();

	private StorageManager() { }

	public static StorageManager getInstance() {
		if (instance == null)
			instance = new StorageManager();
		return instance;
	}

	public void init(Context context) {
		initCacheIndex(context);
		initFavoriteIndex(context);
	}

	private void initIndex(Context context, String dir, ArrayList<String> target) {
		File rootDir = context.getDir(dir, Context.MODE_PRIVATE);
		Observable.just(rootDir)
				.flatMap(file -> Observable.fromArray(file.listFiles()))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<File>() {
					@Override
					public void onSubscribe (Disposable d) { }

					@Override
					public void onNext (File file) {
						target.add(file.getName());
					}

					@Override
					public void onError (Throwable e) {
						Log.w(TAG, "Query " + dir + " failed: " + e.getMessage());
					}

					@Override
					public void onComplete () { }
				});
	}

	private void initCacheIndex(Context context) {
		initIndex(context, CACHE_DIR, caches);
	}

	private void initFavoriteIndex(Context context) {
		initIndex(context, FAVORITE_DIR, favorites);
	}

	public ArrayList<String> getCachesList() {
		return caches;
	}

	public ArrayList<String> getFavoritesList() {
		return favorites;
	}

	public void getCache(Context context, String newsId, onCacheGotListener listener) {
		File cache = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		File target = new File(cache, newsId);
		FileLoader<StorageEntity> fileLoader = new FileLoader<>(obj -> listener.onCacheGot(obj));
		fileLoader.execute(target);
	}

	public Observable<StorageResponse> getAllCache(Context context) {
		File cache = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		return Observable.just(cache)
				.map(file -> {
					File [] files = file.listFiles();
					List<StorageEntity> retval = new ArrayList<>();
					for (File f : files) {
						FileInputStream fileInputStream = new FileInputStream(f);
						ObjectInputStream objectInputStream  = new ObjectInputStream(fileInputStream);
						retval.add((StorageEntity)objectInputStream.readObject());
					}
					return new StorageResponse(retval);
				});
	}

	public void addCache(Context context, NewsEntity news) {
		File cache = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		File target = new File(cache, news.getNewsID());
		if (!target.exists())
			caches.add(news.getNewsID());
		StorageEntity storageEntity =
				new StorageEntity(news, favorites.contains(news.getNewsID()));
		new FileStorer().execute(new FileStorePair(target, storageEntity));
	}

	public boolean setFavorite(Context context, String newsID) {
		File favorite = context.getDir(FAVORITE_DIR, Context.MODE_PRIVATE);
		File target = new File(favorite, newsID);

		try {
			if (target.createNewFile()) {
				favorites.add(newsID);
				getCache(context, newsID, storageEntity ->
				{
					if (storageEntity != null)
						addCache(context, storageEntity.getNews());
				});
			}
		} catch (IOException e) {
			Log.e(TAG, "create new file failed: " + e.getMessage());
			return false;
		}

		return true;
	}

	public boolean unsetFavorite(Context context, String newsID) {
		File favorite = context.getDir(FAVORITE_DIR, Context.MODE_PRIVATE);
		File target = new File(favorite, newsID);

		if (target.delete()) {
			favorites.remove(newsID);
			getCache(context, newsID, storageEntity -> {
				if (storageEntity != null) {
					addCache(context, storageEntity.getNews());
				}
			});
			return true;
		} else {
			return false;
		}
	}
}
