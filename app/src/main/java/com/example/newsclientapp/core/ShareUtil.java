/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.core;

import android.content.Context;
import android.content.Intent;

import com.example.newsclientapp.network.NewsEntity;

public class ShareUtil {
	public static void share(Context context, NewsEntity newsEntity) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		// intent.putExtra(Intent.EXTRA_TITLE, newsEntity.getTitle());
		intent.putExtra(Intent.EXTRA_TEXT,
				newsEntity.getTitle() + "\n\n" + newsEntity.getCleanContent());
		context.startActivity(Intent.createChooser(intent, "分享"));
	}
}
