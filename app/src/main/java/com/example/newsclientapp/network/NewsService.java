/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
	@GET("/svc/news/queryNewsList")
	Observable<NewsResponse> getNews(@Query("size") int size,
	                                 @Query("startDate") String startDate,
	                                 @Query("endDate") String endDate,
	                                 @Query("words") String words,
	                                 @Query("categories") String categories);
}
