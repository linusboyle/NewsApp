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

import static com.example.newsclientapp.core.ThemeUtils.getAttrColor;

public class FuncMenuAdapter extends MenuBaseAdapter<FuncItem> {
	private Boolean isFavourite;

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		final Context context = viewGroup.getContext();

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.rv_item_func, viewGroup, false);
		}

		FuncItem item = (FuncItem) getItem(index);

		final ImageView shareIcon = view.findViewById(R.id.item_func_shareIcon);
		shareIcon.setOnClickListener(item.getShareListener());

		final ImageView galleryIcon = view.findViewById(R.id.item_func_galleryIcon);
		galleryIcon.setOnClickListener(item.getGalleryListener());

		int normal_color = getAttrColor(context, R.attr.mTextPrimary);
		int selected_color = getAttrColor(context, R.attr.colorSecondary);

		if (isFavourite) {
			galleryIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_white_24dp));
			galleryIcon.setColorFilter(selected_color);
		} else {
			galleryIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_border_white_24dp));
			galleryIcon.setColorFilter(normal_color);
		}

		return super.getView(index, view, viewGroup);
	}

	public void updateFavourite(Boolean isFavourite) {
		this.isFavourite = isFavourite;
	}
}
