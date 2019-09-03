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

import com.example.newsclientapp.R;
import com.example.newsclientapp.core.DateUtils;
import com.example.newsclientapp.injection.component.DaggerNewsComponent;
import com.example.newsclientapp.injection.module.NewsModule;
import com.example.newsclientapp.listener.OnItemClickListener;
import com.example.newsclientapp.network.NewsEntity;
import com.example.newsclientapp.network.NewsResponse;
import com.example.newsclientapp.presenter.NewsPresenter;
import com.example.newsclientapp.storage.StorageManager;
import com.example.newsclientapp.ui.activity.NewsDetailActivity;
import com.example.newsclientapp.ui.adapter.RecommendAdapter;
import com.example.newsclientapp.ui.view.NewsView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class RecommendFragment extends BaseFragment implements NewsView {
	@BindView(R.id.recommendation)
	RecyclerView mView;
	@Inject
	NewsPresenter mPresenter;

	private static final String ENTITY = "entity";
	private static final String TAG = "RecommendFragment";
	private static final int COUNT = 10;
	private static final int DISPLAY = 3;
	private RecommendAdapter recommendAdapter;
	private NewsEntity entity;

	public static RecommendFragment newFragment(NewsEntity newsEntity) {
		Bundle args = new Bundle();
		args.putSerializable(ENTITY, newsEntity);
		RecommendFragment recommendFragment = new RecommendFragment();
		recommendFragment.setArguments(args);
		return recommendFragment;
	}

	private void initPresenter() {
		DaggerNewsComponent.builder().newsModule(new NewsModule(this))
				.build().inject(this);
	}

	private void requestNews() {
		mPresenter.requestNews(
				COUNT,
				"",
				DateUtils.getCurrentTimeFormatted(),
				entity.getAKeyword(),
				entity.getCategory());
	}

	private void initRecyclerView() {
		mView.setLayoutManager(new LinearLayoutManager(getContext()));
		recommendAdapter = new RecommendAdapter(getContext());
		recommendAdapter.setOnItemClickListener(new OnItemClickListener<NewsEntity>() {
			@Override
			public void onItemClick (View view, NewsEntity data) {
				if (!StorageManager.getInstance().addCache(data))
					Log.w(TAG, "add to cache has failed");
				NewsDetailActivity.startActivity(getActivity(), data);
			}
		});
		mView.setAdapter(recommendAdapter);
	}

	@Override
	protected void initData (ViewGroup container, Bundle savedInstanceState) {
		entity = (NewsEntity) getArguments().getSerializable(ENTITY);
		initPresenter();
		initRecyclerView();
		requestNews();
		Log.i(TAG, "initialized");
	}

	@Override
	protected int getLayoutId () {
		return R.layout.rv_recommend;
	}

	@Override
	public void onNewsResponsed (NewsResponse res) {
		List<NewsEntity> entities = res.getData();
		List<NewsEntity> retval = new ArrayList<>();
		for (NewsEntity newsEntity : entities) {
			if (!newsEntity.getNewsID().equals(entity.getNewsID())
			&& !newsEntity.getTitle().equals(entity.getTitle())) {
				retval.add(newsEntity);
			}
		}
		Collections.shuffle(retval);
		recommendAdapter.newDataItem(retval.subList(0, DISPLAY));
	}

	@Override
	public void onFailed (String errorMsg) {
		Toast.makeText(getContext(), "加载推荐失败", Toast.LENGTH_LONG).show();
		Log.w(TAG, errorMsg);
	}
}
