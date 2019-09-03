/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;
import com.example.newsclientapp.R;
import com.example.newsclientapp.storage.StorageManager;

public class ThemeUtils {
	public enum AppTheme {
		DARK,
		LIGHT
	}

	private static AppTheme sTheme;
	private static Boolean toInitiate = true;
	private static final String TAG = "ThemeUtils";

	public static void changeToTheme(Activity activity, AppTheme theme) {
		if (theme == sTheme) return;
		sTheme = theme;
		if (!StorageManager.getInstance().updateThemeHistory(sTheme))
			Log.w(TAG, "update theme config has failed!");
		activity.finish();
		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	public static void initTheme() {
		if (!toInitiate) return;
		sTheme = StorageManager.getInstance().getThemeHistorySync();
		toInitiate = false;
	}

	public static void onActivityCreateSetTheme(Activity activity) {
		initTheme();
		switch (sTheme) {
			case DARK:
				activity.setTheme(R.style.DarkAppTheme_NoActionBar);
				break;
			case LIGHT:
				activity.setTheme(R.style.LightAppTheme_NoActionBar);
				break;
		}
	}

	public static int getAttrColor(Context context, int attr) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(attr, typedValue, true);
		return ContextCompat.getColor(context, typedValue.resourceId);
	}

	public static Boolean isDarkMode() {
		return sTheme == AppTheme.DARK;
	}
}
