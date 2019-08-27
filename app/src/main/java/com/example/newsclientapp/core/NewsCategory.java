/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.core;

import java.util.List;
import java.util.Arrays;

public final class NewsCategory {
	public static final List<String> getCategories() {
		return Arrays.asList(
				"娱乐",
				"军事",
				"教育",
				"文化",
				"健康",
				"财经",
				"体育",
				"汽车",
				"科技",
				"社会"
		);
	}
}
