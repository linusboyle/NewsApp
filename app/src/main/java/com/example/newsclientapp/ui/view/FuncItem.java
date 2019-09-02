/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.view;

import android.graphics.drawable.Drawable;

public class FuncItem {
	Drawable shareIcon;
	Drawable galleryIcon;

	public FuncItem (Drawable shareIcon, Drawable galleryIcon) {
		this.shareIcon = shareIcon;
		this.galleryIcon = galleryIcon;
	}

	public Drawable getShareIcon() {
		return this.shareIcon;
	}

	public Drawable getGalleryIcon() {
		return this.galleryIcon;
	}
}
