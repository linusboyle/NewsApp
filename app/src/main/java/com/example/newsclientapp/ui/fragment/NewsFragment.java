package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import com.example.newsclientapp.R;
import com.example.newsclientapp.core.DateUtils;
import com.example.newsclientapp.injection.component.DaggerNewsComponent;
import com.example.newsclientapp.injection.module.NewsModule;
import com.example.newsclientapp.listener.OnItemClickListener;
import com.example.newsclientapp.listener.OnReloadClickListener;
import com.example.newsclientapp.network.NewsEntity;
import com.example.newsclientapp.presenter.NewsPresenter;
import com.example.newsclientapp.network.NewsResponse;
import com.example.newsclientapp.storage.StorageManager;
import com.example.newsclientapp.ui.activity.NewsDetailActivity;
import com.example.newsclientapp.ui.adapter.NewsAdapter;
import com.example.newsclientapp.ui.view.NewsView;

import javax.inject.Inject;

import java.util.List;

public class NewsFragment extends LazyFragment implements NewsView {

	private final static String CATEGORY = "category";
	private final static int COUNT_PER_PAGE = 15;
	private final static int BUFFER_MAX = 60;

	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout mRefreshLayout;

	private boolean isRefresh;

	private List<NewsEntity> newsBuffer;
	private int page = 1;
	private NewsAdapter mAdapter;

	@Inject
	NewsPresenter mPresenter;

	public static NewsFragment newNewsFragment(String category) {
		Bundle args = new Bundle();
		args.putString(CATEGORY, category);
		NewsFragment newsFragment = new NewsFragment();
		newsFragment.setArguments(args);
		return newsFragment;
	}

	@Override
	protected void initPrepare(@Nullable Bundle savedInstanceState) {
		page = 1;
		isRefresh = true;

		initPresenter();
		initRecyclerView();
	}

	@Override
	protected void onInvisible() {
	}

	@Override
	protected void initData() {
		requestNews();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_refresh_rv;
	}

	private String getCategory () {
		return getArguments().getString(CATEGORY);
	}

	private void requestNews() {
		mPresenter.requestNews(BUFFER_MAX, "",
				DateUtils.getCurrentTimeFormatted(), "", getCategory());
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
		DaggerNewsComponent.builder()
				.newsModule(new NewsModule(this))
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
				StorageManager.getInstance().addCache(data);
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

	@Override
	public void onNewsResponsed(NewsResponse res) {
		newsBuffer = res.getData();
		closeRefreshing();
		page = 1;
		isRefresh = false;
		if(!addPageFromBuffer(true)) {
			mAdapter.clear();
			Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onFailed(String errorMsg) {
		closeRefreshing();
		if (mAdapter.getItemCount() == 0) {
			Toast.makeText(getContext(), "加载出错：" + errorMsg, Toast.LENGTH_SHORT).show();
		} else {
			mAdapter.setNetError();
		}
	}

	/**
	 * 关闭 SwipeRefreshLayout 下拉动画
	 */
	private void closeRefreshing() {
		if (mRefreshLayout.isRefreshing()) {
			mRefreshLayout.setRefreshing(false);
		}
	}
}
