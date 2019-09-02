/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.newsclientapp.R;
import com.example.newsclientapp.ui.view.FuncItem;
import com.skydoves.powermenu.MenuBaseAdapter;

public class FuncMenuAdapter extends MenuBaseAdapter<FuncItem> {
	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		final Context context = viewGroup.getContext();

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.rv_item_func, viewGroup, false);

		FuncItem item = (FuncItem) getItem(index);
		final ImageView shareIcon = view.findViewById(R.id.item_func_shareIcon);
		shareIcon.setImageDrawable(item.getShareIcon());
		final ImageView galleryIcon = view.findViewById(R.id.item_func_galleryIcon);
		galleryIcon.setImageDrawable(item.getGalleryIcon());

		return super.getView(index, view, viewGroup);
	}
}
