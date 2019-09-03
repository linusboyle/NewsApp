/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import android.content.Context;
import android.util.Log;

import com.example.newsclientapp.core.NewsCategory;
import com.example.newsclientapp.core.ThemeUtils;
import com.example.newsclientapp.listener.OnNewsGotListener;
import com.example.newsclientapp.network.NewsEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StorageManager {
	private static final String CACHE_DIR = "cache";
	private static final String FAVORITE_DIR= "favorite";
	private static final String CONFIGURATION_DIR = "configuration";
	private static final String SEARCH_HISTORY = "search_history";
	private static final String TAB_HISTORY = "tab_history";
	private static final String THEME_HISTORY = "theme_history";
	private static final String TAG = "StorageManager";
	private static StorageManager instance;

	private HashSet<String> favorites = new HashSet<>();
	private HashSet<String> caches = new HashSet<>();

	private SyncCacheAccess syncCacheAccess;
	@SuppressWarnings("FieldCanBeLocal")
	private Thread _thread;
	private File configuration_dir;

	private StorageManager() { }

	public static StorageManager getInstance() {
		if (instance == null)
			instance = new StorageManager();
		return instance;
	}

	public void init(Context context) {
		File cacheDir = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		File favoriteDir = context.getDir(FAVORITE_DIR, Context.MODE_PRIVATE);
		configuration_dir = context.getDir(CONFIGURATION_DIR, Context.MODE_PRIVATE);

		syncCacheAccess = new SyncCacheAccess(cacheDir, favoriteDir);
		_thread = new Thread(syncCacheAccess);
		_thread.start();
		initCacheIndex(context);
		initFavoriteIndex(context);
	}

	private void initIndex(Context context, String dir, HashSet<String> target) {
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
					public void onComplete () {
						Log.i(TAG, "StorageManager initialized");
					}
				});
	}

	private void initCacheIndex(Context context) {
		initIndex(context, CACHE_DIR, caches);
	}

	private void initFavoriteIndex(Context context) {
		initIndex(context, FAVORITE_DIR, favorites);
	}

	public HashSet<String> getCachesList() {
		return caches;
	}

	public HashSet<String> getFavoritesList() {
		return favorites;
	}

	public void getCache(Context context, String newsId, OnNewsGotListener listener) {
		File cache = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		File target = new File(cache, newsId);
		FileLoader<NewsEntity> fileLoader = new FileLoader<>(listener::onNewsGot);
		fileLoader.execute(target);
	}

	public Observable<StorageResponse> getAllCache(Context context) {
		File cache = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		return Observable.just(cache)
				.map(file -> {
					File [] files = file.listFiles();
					List<NewsEntity> retval = new ArrayList<>();
					assert files != null;
					for (File f : files) {
						FileInputStream fileInputStream = new FileInputStream(f);
						ObjectInputStream objectInputStream  = new ObjectInputStream(fileInputStream);
						retval.add((NewsEntity) objectInputStream.readObject());
						objectInputStream.close();
						fileInputStream.close();
					}
					return new StorageResponse(retval);
				});
	}

	public Observable<StorageResponse> getAllFavorite(Context context) {
		File cache = context.getDir(CACHE_DIR, Context.MODE_PRIVATE);
		return Observable.just(cache)
				.map(file -> {
					List<NewsEntity>  retval = new ArrayList<>();
					for (String id : favorites) {
						File newsCache = new File(cache, id);
						if (newsCache.exists()) {
							FileInputStream fileInputStream = new FileInputStream(newsCache);
							ObjectInputStream objectInputStream  = new ObjectInputStream(fileInputStream);
							retval.add((NewsEntity) objectInputStream.readObject());
							objectInputStream.close();
							fileInputStream.close();
						}
					}
					return new StorageResponse(retval);
				});
	}

	// check the returned object if you call this function
	public<T> Observable<T> getObject(File target) {
		return Observable.just(target).map(file -> {
			if (file.exists()) {
				FileInputStream fileInputStream = new FileInputStream(file);
				ObjectInputStream objectInputStream  = new ObjectInputStream(fileInputStream);
				T retval = (T)objectInputStream.readObject();
				objectInputStream.close();
				fileInputStream.close();
				return retval;
			} else {
				return null;
			}
		});
	}

	public boolean addCache(NewsEntity newsEntity) {
		caches.add(newsEntity.getNewsID());
		return syncCacheAccess.addCache(newsEntity);
	}

	public boolean setFavorite(NewsEntity newsEntity) {
		// favorite must be cached
		addCache(newsEntity);
		favorites.add(newsEntity.getNewsID());
		return syncCacheAccess.setFavorite(newsEntity);
	}

	public boolean unsetFavorite(NewsEntity newsEntity) {
		// no need to remove or add cache here.
		favorites.remove(newsEntity.getNewsID());
		return syncCacheAccess.unsetFavorite(newsEntity);
	}

	public boolean writeObject(File target, Object object) {
		return syncCacheAccess.writeObject(target, object);
	}

	public boolean removeCache(NewsEntity newsEntity) {
		caches.remove(newsEntity.getNewsID());
		return syncCacheAccess.removeCache(newsEntity);
	}

	public boolean updateSearchHistory(List<String> searchHistory) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : searchHistory) {
			stringBuilder.append(s);
			stringBuilder.append('\n');
		}
		File search_his_file = new File(configuration_dir, SEARCH_HISTORY);
		return syncCacheAccess.writeString(search_his_file, stringBuilder.toString());
	}

	public boolean updateTabHistory(List<String> tabs) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : tabs) {
			stringBuilder.append(s);
			stringBuilder.append('\n');
		}
		File tab_his_file = new File(configuration_dir, TAB_HISTORY);
		return syncCacheAccess.writeString(tab_his_file, stringBuilder.toString());
	}

	public boolean updateThemeHistory(ThemeUtils.AppTheme theme) {
		String string = theme == ThemeUtils.AppTheme.DARK ? "Dark" : "Light";
		File theme_his_file = new File(configuration_dir, THEME_HISTORY);
		return syncCacheAccess.writeString(theme_his_file, string);
	}

	public ThemeUtils.AppTheme getThemeHistorySync() {
		try {
			File theme_his_file = new File(configuration_dir, THEME_HISTORY);
			Scanner sc = new Scanner(theme_his_file);
			if (sc.hasNextLine()) {
				String string = sc.nextLine();
				if (string.trim().equals("Dark"))
					return ThemeUtils.AppTheme.DARK;
				else
					return ThemeUtils.AppTheme.LIGHT;
			}
		} catch (FileNotFoundException ignored) { }
		return ThemeUtils.AppTheme.DARK;
	}

	public List<String> getSearchHistorySync () {
		try {
			ArrayList<String> retval = new ArrayList<>();
			File search_his_file = new File(configuration_dir, SEARCH_HISTORY);
			Scanner sc = new Scanner(search_his_file);
			while (sc.hasNextLine()) {
				String nextline = sc.nextLine();
				if (!nextline.trim().equals(""))
					retval.add(nextline);
			}

			return retval;
		} catch (FileNotFoundException e) {
			// it's the first time running this app, no search history:
			return new ArrayList<>();
		}
	}

	public List<String> getTabHistorySync () {
		try {
			ArrayList<String> retval = new ArrayList<>();
			File tab_his_file = new File(configuration_dir, TAB_HISTORY);
			Scanner sc = new Scanner(tab_his_file);
			while (sc.hasNextLine()) {
				String nextline = sc.nextLine();
				if (!nextline.trim().equals(""))
					retval.add(nextline);
			}

			return retval;
		} catch (FileNotFoundException e) {
			// it's the first time running this app, no search history:
			return NewsCategory.getCategories();
		}
	}
}
