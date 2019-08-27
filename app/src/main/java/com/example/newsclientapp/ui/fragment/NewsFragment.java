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
import com.example.newsclientapp.injection.DaggerNewsComponent;
import com.example.newsclientapp.injection.NewsModule;
import com.example.newsclientapp.listener.OnItemClickListener;
import com.example.newsclientapp.listener.OnReloadClickListener;
import com.example.newsclientapp.network.NewsEntity;
import com.example.newsclientapp.network.NewsPresenter;
import com.example.newsclientapp.network.NewsResponse;
import com.example.newsclientapp.ui.adapter.NewsAdapter;
import com.example.newsclientapp.ui.view.NewsView;

import javax.inject.Inject;
import java.util.List;

// TODO
public class NewsFragment extends LazyFragment implements NewsView {

	private final static String CATEGORY = "category";
	private final static int COUNT = 15;

	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout mRefreshLayout;

	private boolean isRefresh;
	private boolean isLoadMore;

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
		isLoadMore = false;

		initPresenter();
		initRecyclerView();
	}

	@Override
	protected void onInvisible() {
	}

	@Override
	protected void initData() {
		// mPresenter.requestListNews(getCategory(), COUNT, page);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_refresh_rv;
	}

	private String getCategory() {
		return getArguments().getString(CATEGORY);
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
				mPresenter.requestNews(COUNT, "", "", "", getCategory());
			}
		});
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		 mAdapter = new NewsAdapter(getContext());
		 // 加载更多 异常处理
		 mAdapter.setOnReloadClickListener(new OnReloadClickListener() {
		 	@Override
		 	public void onClick() {
		 		mAdapter.setLoading();
			    mPresenter.requestNews(COUNT, "", "", "", getCategory());
		 	}
		 });
		 // mAdapter.setOnItemClickListener(new OnItemClickListener<NewsEntity>() {
		 // 	@Override
		 // 	public void onItemClick(View view, NewsEntity data) {
		 // 		NewsDetailActivity.startActivity(getActivity(), data.getTitle(), data.getUrl());
		 // 	}
		 // });
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
		 		if (!isRefresh && !isLoadMore && newState == RecyclerView.SCROLL_STATE_IDLE &&
		 				lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
		 			isLoadMore = true;
		 			isRefresh = false;
		 			mAdapter.setLoading();
				    mPresenter.requestNews(COUNT, "", "", "", getCategory());
		 		}
		 	}
		 });
	}

	@Override
	public void onNewsResponsed(NewsResponse res) {
		List<NewsEntity> list = res.getData();
		closeRefreshing();
		if (isRefresh) {
			if (list != null && list.size() > 0) {
				mAdapter.newDataItem(list);
				page++;
			} else {
				Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			}
		} else {
			if (list != null && list.size() > 0) {
				mAdapter.addMoreItem(list);
				page++;
			} else {
				mAdapter.setNotMore();
			}
		}
		isRefresh = false;
		isLoadMore = false;
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
