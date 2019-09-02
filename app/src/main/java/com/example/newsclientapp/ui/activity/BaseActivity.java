package com.example.newsclientapp.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.newsclientapp.R;
import com.example.newsclientapp.ui.adapter.ButtonItemAdapter;
import com.example.newsclientapp.ui.adapter.FuncMenuAdapter;
import com.example.newsclientapp.ui.view.ButtonItem;
import com.example.newsclientapp.ui.view.FuncItem;
import com.skydoves.powermenu.*;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public abstract class BaseActivity extends RxAppCompatActivity {
	private Unbinder mUnBinder;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		mUnBinder = ButterKnife.bind(this);
		initData(savedInstanceState);
	}

	/**
	 * 布局id
	 * @return id
	 */
	@LayoutRes
	protected abstract int getLayout();

	/**
	 * 初始化
	 * @param savedInstanceState 保存
	 */
	protected abstract void initData(@Nullable Bundle savedInstanceState);

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUnBinder != null) {
			mUnBinder.unbind();
		}
	}

	protected void createToolBarPopupMenu(Toolbar toolbar) {
		// PowerMenu mainMenu = new PowerMenu.Builder(this)
		// 		.addItem(new PowerMenuItem(getResources().getString(R.string.action_settings), false))
		// 		.addItem(new PowerMenuItem(getResources().getString(R.string.action_update), false))
		// 		.addItem(new PowerMenuItem(getResources().getString(R.string.action_about), false))
		// 		.setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
		// 		.setMenuRadius(10f)
		// 		.setMenuShadow(10f)
		// 		.setTextTypeface(Typeface.MONOSPACE)
		// 		.setTextColor(ContextCompat.getColor(this, R.color.dark_text))
		// 		.setTextSize(14)
		// 		.setTextGravity(Gravity.LEFT)
		// 		.setMenuColor(ContextCompat.getColor(this, R.color.dark_ground_08dp))
		// 		.setSelectedMenuColor(ContextCompat.getColor(this, R.color.dark_ground_24dp))
		// 		.setSelectedTextColor(ContextCompat.getColor(this, R.color.dark_primary))
		// 		.build();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int windowWidth = displayMetrics.widthPixels;
		CustomPowerMenu mainMenu = new CustomPowerMenu.Builder<>(this, new ButtonItemAdapter())
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_menu_gallery), getResources().getString(R.string.action_settings), null, true))
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_menu_gallery), getResources().getString(R.string.action_update), null, false))
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_menu_gallery), getResources().getString(R.string.action_about), null, false))
				.setLifecycleOwner(this)
				.setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
				.setWidth(Math.round(windowWidth*0.75f))
				.setMenuRadius(10f)
				.setMenuShadow(10f)
				.build();

		mainMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<ButtonItem>() {
			@Override
			public void onItemClick (int position, ButtonItem item) {
				// TODO
				mainMenu.setSelectedPosition(position);
				mainMenu.dismiss();
			}
		});

		((ImageView) toolbar.findViewById(R.id.toolview)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				mainMenu.showAsDropDown(view);
			}
		});

		// return mainMenu;
	}
}
