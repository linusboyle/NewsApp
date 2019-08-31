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

	private boolean fabOpened;

	public static void startActivity (Context context, NewsEntity data) {
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
		fabOpened = false;
		NewsEntity news = (NewsEntity) getIntent().getSerializableExtra(DATA);
		String[] picUrls = news.getImageURLs();
		String newsTitle = news.getTitle();
		String newsText = news.getCleanContent();

		// toolbar
		initToolbar(newsTitle);

		// fab
		mFabMenu.setClosedOnTouchOutside(true);
		mShare.setOnClickListener(view -> {
			ShareUtils.share(view.getContext(), news);
			if (mFabMenu.isOpened())
				mFabMenu.close(true);
		});
		mFavorite.setOnClickListener(view -> {
			StorageManager.getInstance().setFavorite(view.getContext(), news);
			Toast.makeText(view.getContext(), "已添加到收藏", Toast.LENGTH_LONG).show();
			if (mFabMenu.isOpened())
				mFabMenu.close(true);
		});

		// page content
		initNewsPage(picUrls, newsTitle, newsText);
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
		// description.setText("来源：" + newsEntity.getPublisher());
		// datetime.setText(newsEntity.getPublishTime());
		mNewsText.setText(newsText);

		// itemView.setOnClickListener(new View.OnClickListener() {
		// 	@Override
		// 	public void onClick(View view) {
		// 		if (mOnItemClickListener != null) {
		// 			mOnItemClickListener.onItemClick(view, newsEntity);
		// 		}
		// 	}
		// });
	}

	private void initToolbar (String title) {
		mToolbar.setTitle(title);

		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mToolbar.setNavigationOnClickListener(view -> finish());
	}

	/*
	private void openMenu(View view) {
		ObjectAnimator objectAnimator =
				ObjectAnimator.ofFloat(view, "rotation", 0, -155, -135);
		objectAnimator.setDuration(500);
		objectAnimator.start();
		mMask.setVisibility(View.VISIBLE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.7f);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		mMask.startAnimation(alphaAnimation);
		fabOpened = true;
	}

	private void closeMenu(View view) {
		ObjectAnimator objectAnimator =
				ObjectAnimator.ofFloat(view, "rotation",
										-135, 20, 0);
		objectAnimator.setDuration(500);
		objectAnimator.start();
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.7f, 0);
		alphaAnimation.setDuration(500);
		mMask.startAnimation(alphaAnimation);
		mMask.setVisibility(View.GONE);
		fabOpened = false;
	}
	*/
}
