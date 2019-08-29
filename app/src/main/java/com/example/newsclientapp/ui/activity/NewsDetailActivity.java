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
import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newsclientapp.core.ShareUtil;
import com.example.newsclientapp.network.NewsEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import com.example.newsclientapp.R;

public class NewsDetailActivity extends BaseActivity {

	private static String DATA = "web_data";

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.fab) FloatingActionButton mFab;
	@BindView(R.id.news_image) ImageView mImageView;
	@BindView(R.id.news_title) TextView mNewsTitle;
	@BindView(R.id.news_text) TextView mNewsText;

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
		NewsEntity news = (NewsEntity) getIntent().getSerializableExtra(DATA);
		String[] picUrls = news.getImageURLs();
		String newsTitle = news.getTitle();
		String newsText = news.getCleanContent();

		// toolbar
		initToolbar(newsTitle);

		// fab
		mFab.setOnClickListener(view -> {
		    // Snackbar.make(view, "To do with sharing", Snackbar.LENGTH_LONG)
			// .setAction("Action", null).show();
			ShareUtil.share(view.getContext(), news);
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
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				finish();
			}
		});
	}
}
