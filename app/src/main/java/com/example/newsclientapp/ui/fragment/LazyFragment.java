package com.example.newsclientapp.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.example.newsclientapp.ui.view.BaseView;

public abstract class LazyFragment extends RxFragment implements BaseView {
	protected View mRootView;
	protected Context mContext;
	protected boolean isVisible;
	private boolean isPrepared;
	private boolean isFirst = true;
	private Unbinder mUnBinder;

	//--------------------------call back------------------------------//

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		isPrepared = true;
		initPrepare(savedInstanceState);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			lazyLoad();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			setUserVisibleHint(true);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(getLayoutId(), container, false);
		}
		mUnBinder = ButterKnife.bind(this, mRootView);
		return mRootView;
	}

	//-------------------------method----------------------------------//

	protected void lazyLoad() {
		if (!isPrepared || !isVisible || !isFirst) {
			return;
		}
		initData();
		isFirst = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mUnBinder != null) {
			mUnBinder.unbind();
		}
	}

	//--------------------------abstract method------------------------//

	protected abstract void initPrepare(@Nullable Bundle savedInstanceState);

	protected abstract void onInvisible();

	protected abstract void initData();

	protected abstract int getLayoutId();

	@Override
	public <T> LifecycleTransformer<T> bindToLife() {
		return bindToLifecycle();
	}
}
