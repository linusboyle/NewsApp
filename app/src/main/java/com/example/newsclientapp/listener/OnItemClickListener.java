package com.example.newsclientapp.listener;

import android.view.View;

public interface OnItemClickListener<T> {

	void onItemClick(View view, T data);

}
