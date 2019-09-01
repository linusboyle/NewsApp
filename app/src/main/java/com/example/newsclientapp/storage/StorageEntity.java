/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import com.example.newsclientapp.network.NewsEntity;

import java.io.Serializable;

public class StorageEntity implements Serializable {
	private NewsEntity news;
	private boolean isFavorite;

	public NewsEntity getNews () {
		return news;
	}

	public boolean isFavorite () {
		return isFavorite;
	}

	public void setFavorite (boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public StorageEntity (NewsEntity news, boolean isFavorite) {
		this.news = news;
		this.isFavorite = isFavorite;
	}
}

