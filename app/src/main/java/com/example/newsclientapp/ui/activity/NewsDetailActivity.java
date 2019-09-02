/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newsclientapp.core.ShareUtils;
import com.example.newsclientapp.network.NewsEntity;

import androidx.appcompat.widget.Toolbar;
import com.example.newsclientapp.R;
import com.example.newsclientapp.storage.StorageEntity;
import com.example.newsclientapp.storage.StorageManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class NewsDetailActivity extends BaseActivity {

	private static String DATA = "web_data";

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.fab) FloatingActionMenu mFabMenu;
	@BindView(R.id.news_image) ImageView mImageView;
	@BindView(R.id.news_title) TextView mNewsTitle;
	@BindView(R.id.news_text) TextView mNewsText;
	@BindView(R.id.fab_favorite)
	FloatingActionButton mFavorite;
	@BindView(R.id.fab_share)
	FloatingActionButton mShare;

	private boolean isFavorite;

	public static void startActivity (Context context, StorageEntity data) {
		Intent intent = new Intent(context, NewsDetailActivity.class);
		intent.putExtra(DATA, data);
		context.startActivity(intent);
	}

	@Override
	protected int getLayout () {
		return R.layout.activity_news_detail;
	}

	@Override
	protected void initData (Bundle savedInstanceState) {
		StorageEntity storageEntity = (StorageEntity) getIntent().getSerializableExtra(DATA);
		NewsEntity news = storageEntity.getNews();

		isFavorite = storageEntity.isFavorite();

		String[] picUrls = news.getImageURLs();
		String newsTitle = news.getTitle();
		String newsText = news.getCleanContent();

		// toolbar
		initToolbar(newsTitle);
		syncFavoriteState();

		// fab
		mFabMenu.setClosedOnTouchOutside(true);
		mShare.setOnClickListener(view -> {
			ShareUtils.share(view.getContext(), news);
			if (mFabMenu.isOpened())
				mFabMenu.close(true);
		});
		mFavorite.setOnClickListener(view -> {
			if (isFavorite) {
				if (StorageManager.getInstance().unsetFavorite(view.getContext(), news)) {
					Toast.makeText(view.getContext(), "已删除收藏", Toast.LENGTH_LONG).show();
					isFavorite = false;
				} else {
					Toast.makeText(view.getContext(), "操作失败", Toast.LENGTH_LONG).show();
				}
			} else {
				if (StorageManager.getInstance().setFavorite(view.getContext(), news)) {
					Toast.makeText(view.getContext(), "已添加到收藏", Toast.LENGTH_LONG).show();
					isFavorite = true;
				} else {
					Toast.makeText(view.getContext(), "操作失败", Toast.LENGTH_LONG).show();
				}
			}
			syncFavoriteState();
			if (mFabMenu.isOpened())
				mFabMenu.close(true);
		});

		// page content
		initNewsPage(picUrls, newsTitle, newsText);
	}

	private void syncFavoriteState() {
		if (isFavorite) {
			mFavorite.setImageDrawable(getDrawable(R.drawable.ic_star));
			mFavorite.setLabelText("取消收藏");
		} else {
			mFavorite.setImageDrawable(getDrawable(R.drawable.ic_empty_star));
			// mFavorite.setImageIcon(Icon.createWithResource(this, R.drawable.ic_empty_star));
			mFavorite.setLabelText("收藏");
		}
	}

	private void initNewsPage(String[] picUrls, String newsTitle, String newsText) {
		if (picUrls == null || picUrls.length == 0 || TextUtils.isEmpty(picUrls[0])) {
			mImageView.setVisibility(View.GONE);
		} else {
			String picUrl = picUrls[0];
			Glide.with(this)
					.load(picUrl)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.glide_pic_default)
					.error(R.drawable.glide_pic_failed)
					.into(mImageView);
			mImageView.setVisibility(View.VISIBLE);
		}
		mNewsTitle.setText(newsTitle);
		mNewsText.setText(newsText);
	}

	private void initToolbar (String title) {
		mToolbar.setTitle(title);

		// setSupportActionBar(mToolbar);
		createToolBarPopupMenu(mToolbar);

		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mToolbar.setNavigationOnClickListener(view -> finish());
	}
}
