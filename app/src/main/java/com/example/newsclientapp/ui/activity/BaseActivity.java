package com.example.newsclientapp.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.newsclientapp.R;
import com.example.newsclientapp.ui.adapter.ButtonItemAdapter;
import com.example.newsclientapp.ui.view.ButtonItem;
import com.skydoves.powermenu.*;
import com.suke.widget.SwitchButton;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;


public abstract class BaseActivity extends RxAppCompatActivity {
	private Unbinder mUnBinder;

	private CustomPowerMenu mainMenu;
	private TextView headerText;
	private ImageView headerIcon;
	private PowerMenu dialog;
	private Toolbar toolbar;

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
		this.toolbar = toolbar;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int windowWidth = displayMetrics.widthPixels;

		SwitchButton.OnCheckedChangeListener darkModeListener = new SwitchButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged (SwitchButton view, boolean isChecked) {
				// TODO
			}
		};

		mainMenu = new CustomPowerMenu.Builder<>(this, new ButtonItemAdapter())
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_dark_white_24dp), getResources().getString(R.string.action_darkmode), darkModeListener, true))
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_update_white_24dp), getResources().getString(R.string.action_update), null, false))
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_adb_white_24dp), getResources().getString(R.string.action_about), null, false))
				.setLifecycleOwner(this)
				.setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
				.setWidth(Math.round(windowWidth*0.75f))
				.setMenuRadius(10f)
				.setMenuShadow(10f)
				.build();

		View header = LayoutInflater.from(BaseActivity.this).inflate(R.layout.dialog_header, null);
		headerText = header.findViewById(R.id.header_text);
		headerIcon = header.findViewById(R.id.header_icon);

		dialog = new PowerMenu.Builder(BaseActivity.this)
				.setHeaderView(header)
				.setAnimation(MenuAnimation.SHOW_UP_CENTER)
				.setMenuColor(ContextCompat.getColor(BaseActivity.this, R.color.dark_ground_primary))
				.setTextColor(ContextCompat.getColor(BaseActivity.this, R.color.dark_text))
				.setLifecycleOwner(this)
				.setMenuRadius(10f)
				.setMenuShadow(10f)
				.setWidth(Math.round(windowWidth*0.75f))
				.setSelectedEffect(false)
				.setCircularEffect(CircularEffect.BODY)
				.build();

		mainMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<ButtonItem>() {
			@Override
			public void onItemClick (int position, ButtonItem item) {
				// TODO
				if (position == 0) {
					// dark mode switch
					return;
				}

				if (position == 1) {
					// update
					dialog.clearItems();
					dialog.addItem(new PowerMenuItem(getResources().getString(R.string.already_update), false));
					headerText.setText(getResources().getString(R.string.action_update));
					headerIcon.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.drawable.ic_update_white_24dp));
					dialog.showAtCenter(BaseActivity.this.toolbar);
					mainMenu.dismiss();
					return;
				}

				if (position == 2) {
					// about
					dialog.clearItems();
					dialog.addItem(new PowerMenuItem(getResources().getString(R.string.about_text), false));
					headerText.setText(getResources().getString(R.string.action_about));
					headerIcon.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.drawable.ic_adb_white_24dp));
					dialog.showAtCenter(BaseActivity.this.toolbar);
					mainMenu.dismiss();
					return;
				}
			}
		});

		toolbar.findViewById(R.id.toolview).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				mainMenu.showAsDropDown(view);
			}
		});

		// return mainMenu;
	}
}
