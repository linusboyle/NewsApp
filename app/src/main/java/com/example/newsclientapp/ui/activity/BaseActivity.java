package com.example.newsclientapp.ui.activity;

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
import com.example.newsclientapp.core.ThemeUtils;
import com.example.newsclientapp.ui.adapter.ButtonItemAdapter;
import com.example.newsclientapp.ui.view.ButtonItem;
import com.skydoves.powermenu.*;
import com.suke.widget.SwitchButton;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import static com.example.newsclientapp.core.ThemeUtils.*;


public abstract class BaseActivity extends RxAppCompatActivity {
	private Unbinder mUnBinder;

	private CustomPowerMenu mainMenu;
	private TextView headerText;
	private ImageView headerIcon;
	private PowerMenu dialog;
	private Toolbar toolbar;

	protected ThemeUtils.AppTheme mTheme;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtils.onActivityCreateSetTheme(this);
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
			mUnBinder = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTheme != ThemeUtils.getCurTheme())
			recreate();
	}

	public void setThemeMark(ThemeUtils.AppTheme newTheme) {
		mTheme = newTheme;
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
				ThemeUtils.AppTheme toTheme = isChecked ? ThemeUtils.AppTheme.DARK : ThemeUtils.AppTheme.LIGHT;
				changeToTheme(BaseActivity.this, toTheme);
			}
		};

		mainMenu = new CustomPowerMenu.Builder<>(this, new ButtonItemAdapter())
				.addItem(new ButtonItem(ContextCompat.getDrawable(this, R.drawable.ic_dark_white_24dp), getResources().getString(R.string.action_darkmode), darkModeListener, isDarkMode()))
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

		int text_color = getAttrColor(this, R.attr.mTextPrimary);
		int ground_color = getAttrColor(this, R.attr.mBackground_00dp);
		dialog = new PowerMenu.Builder(BaseActivity.this)
				.setHeaderView(header)
				.setAnimation(MenuAnimation.SHOW_UP_CENTER)
				.setMenuColor(ground_color)
				.setTextColor(text_color)
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
