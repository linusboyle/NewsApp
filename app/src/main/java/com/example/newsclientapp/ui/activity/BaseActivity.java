package com.example.newsclientapp.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.newsclientapp.R;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
