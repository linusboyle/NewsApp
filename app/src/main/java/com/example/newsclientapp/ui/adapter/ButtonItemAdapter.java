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
import android.widget.TextView;
import com.example.newsclientapp.R;
import com.example.newsclientapp.ui.view.ButtonItem;
import com.skydoves.powermenu.MenuBaseAdapter;
import com.suke.widget.SwitchButton;

public class ButtonItemAdapter extends MenuBaseAdapter<ButtonItem> {
	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		final Context context = viewGroup.getContext();

		if (view == null)
			view = LayoutInflater.from(context).inflate(R.layout.rv_item_button, viewGroup, false);

		ButtonItem item = (ButtonItem) getItem(index);
		final ImageView itemIcon = view.findViewById(R.id.item_button_icon);
		itemIcon.setImageDrawable(item.getItemIcon());
		final TextView itemText = view.findViewById(R.id.item_button_text);
		itemText.setText(item.getItemText());
		final SwitchButton itemButton = view.findViewById(R.id.item_button);
		SwitchButton.OnCheckedChangeListener itemListener = item.getItemListener();
		if (itemListener != null) {
			itemButton.setOnCheckedChangeListener(itemListener);
			itemButton.setChecked(item.getIsChecked());
		} else {
			// itemButton.setEnabled(false);
			// itemButton.setVisibility(View.INVISIBLE);
		}

		return super.getView(index, view, viewGroup);
	}
}
