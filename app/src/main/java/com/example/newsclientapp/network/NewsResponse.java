/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.network;

import java.util.List;

public class NewsResponse {
	private String pageSize;
	private int total;
	private List<NewsEntity> data;

	public String getPageSize () {
		return pageSize;
	}

	public void setPageSize (String pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal () {
		return total;
	}

	public void setTotal (int total) {
		this.total = total;
	}

	public List<NewsEntity> getData () {
		return data;
	}

	public void setData (List<NewsEntity> data) {
		this.data = data;
	}
}
