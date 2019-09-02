/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.view;

import android.graphics.drawable.Drawable;
import com.suke.widget.SwitchButton;

public class ButtonItem {
	Drawable icon;
	String text;
	SwitchButton.OnCheckedChangeListener listener;
	Boolean isChecked;

	public ButtonItem (Drawable icon, String text, SwitchButton.OnCheckedChangeListener listener, Boolean isChecked) {
		this.icon = icon;
		this.text = text;
		this.listener = listener;
		this.isChecked = isChecked;
	}

	public Drawable getItemIcon () {
		return icon;
	}

	public String getItemText () {
		return text;
	}

	public SwitchButton.OnCheckedChangeListener getItemListener () {
		return listener;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}
}
