/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
	private static final int DEFAULT_TIMEOUT = 10 * 1000;
	private static final String BASEURL = "https://api2.newsminer.net";
	private static NewsService _service;

	public static NewsService getNewsService() {
		if (_service == null) {
			synchronized (RetrofitClient.class) {
				OkHttpClient.Builder builder = new OkHttpClient.Builder();
				builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
				builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
				builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
				_service = new Retrofit.Builder()
						.client(builder.build())
						.addConverterFactory(GsonConverterFactory.create())
						.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
						.baseUrl(BASEURL)
						.build()
						.create(NewsService.class);
			}
		}

		return _service;
	}
}
