package com.example.newsclientapp.ui.activity;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
}
