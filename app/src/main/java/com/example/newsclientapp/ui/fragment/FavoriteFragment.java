/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsclientapp.R;
import com.example.newsclientapp.injection.component.DaggerFavoriteComponent;
import com.example.newsclientapp.injection.module.FavoriteModule;
import com.example.newsclientapp.listener.OnItemClickListener;
import com.example.newsclientapp.listener.OnReloadClickListener;
import com.example.newsclientapp.network.NewsEntity;
import com.example.newsclientapp.presenter.FavoritePresenter;
import com.example.newsclientapp.storage.StorageEntity;
import com.example.newsclientapp.storage.StorageResponse;
import com.example.newsclientapp.ui.activity.NewsDetailActivity;
import com.example.newsclientapp.ui.adapter.NewsAdapter;
import com.example.newsclientapp.ui.view.FavoriteView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class FavoriteFragment extends BaseFragment implements FavoriteView {

	private final static int COUNT_PER_PAGE = 15;
	private final static String TAG = "FavoriteFragment";

	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout mRefreshLayout;

	private boolean isRefresh;

	private List<NewsEntity> newsBuffer;
	private int page = 1;
	private NewsAdapter mAdapter;

	@Inject
	FavoritePresenter mPresenter;

	@Override
	protected int getLayoutId() {
		return R.layout.layout_refresh_rv;
	}

	@Override
	protected void initData (ViewGroup container, Bundle savedInstanceState) {
		page = 1;
		isRefresh = true;

		initPresenter();
		initRecyclerView();
		requestNews();

		Log.i(TAG, "initialized!");
	}

	private void requestNews() {
		mPresenter.requestFavorite(getContext());
	}

	private boolean addPageFromBuffer(boolean reset) {
		if (newsBuffer != null && newsBuffer.size() > 0) {
			int startIndex = (page-1)*COUNT_PER_PAGE;
			int endIndex = (page)*COUNT_PER_PAGE <= newsBuffer.size() ? (page)*COUNT_PER_PAGE : newsBuffer.size();
			if (startIndex < newsBuffer.size()) {
				if (reset)
					mAdapter.newDataItem(newsBuffer.subList(startIndex, endIndex));
				else
					mAdapter.addMoreItem(newsBuffer.subList(startIndex, endIndex));
				page++;
				return true;
			}
		}
		return false;
	}

	private void initPresenter() {
		DaggerFavoriteComponent.builder()
				.favoriteModule(new FavoriteModule(this))
				.build()
				.inject(this);
	}

	private void initRecyclerView() {
		mRefreshLayout.setRefreshing(true);
		mRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshLayoutColor));
		// 下拉刷新事件
		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefresh = true;
				page = 1;
				requestNews();
			}
		});
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mAdapter = new NewsAdapter(getContext());
		// 加载更多 异常处理
		mAdapter.setOnReloadClickListener(new OnReloadClickListener() {
			@Override
			public void onClick() {
				mAdapter.setLoading();
				requestNews();
			}
		});
		mAdapter.setOnItemClickListener(new OnItemClickListener<NewsEntity>() {
			@Override
			public void onItemClick(View view, NewsEntity data) {
				NewsDetailActivity.startActivity(getActivity(), data);
			}
		});
		mRecyclerView.setAdapter(mAdapter);
		// 滑到底部监听事件
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
				int totalItemCount = recyclerView.getAdapter().getItemCount();
				int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
				int visibleItemCount = recyclerView.getChildCount();
				if (!isRefresh && newState == RecyclerView.SCROLL_STATE_IDLE &&
						lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
					isRefresh = false;
					if (!addPageFromBuffer(false))
						mAdapter.setNotMore();
				}
			}
		});
	}

	/**
	 * 关闭 SwipeRefreshLayout 下拉动画
	 */
	private void closeRefreshing() {
		if (mRefreshLayout.isRefreshing()) {
			mRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	public void onFavoriteResponsed (StorageResponse response) {
		List<StorageEntity> storageEntities = response.getStorageEntities();
		newsBuffer = new ArrayList<>();
		for (StorageEntity se : storageEntities) {
			newsBuffer.add(se.getNews());
		}
		closeRefreshing();
		page = 1;
		isRefresh = false;
		if(!addPageFromBuffer(true))
			Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFavoriteFailed (String errorMsg) {
		closeRefreshing();
		if (mAdapter.getItemCount() == 0) {
			Toast.makeText(getContext(), "加载出错：" + errorMsg, Toast.LENGTH_SHORT).show();
		} else {
			mAdapter.setNetError();
		}
	}
}
