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
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.Locale;

public class NewsDetailActivity extends BaseActivity {

	private static String DATA = "web_data";
	private TextToSpeech tts;
	private String content;

	private class TTSListener implements TextToSpeech.OnInitListener {
		@Override
		public void onInit (int status) {
			if (status == TextToSpeech.SUCCESS) {
				int result = tts.setLanguage(Locale.CHINESE);
				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
					Toast.makeText(NewsDetailActivity.this, "系统不支持中文语音", Toast.LENGTH_SHORT).show();
				} else if (result == TextToSpeech.LANG_AVAILABLE){
					tts.speak(content,TextToSpeech.QUEUE_ADD,null);
				}
			} else {
				Toast.makeText(NewsDetailActivity.this, "初始化语音失败", Toast.LENGTH_SHORT).show();
				Log.e("TAG", "初始化失败");
			}
		}
	}

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.fab) FloatingActionMenu mFabMenu;
	@BindView(R.id.news_image) ImageView mImageView;
	@BindView(R.id.news_title) TextView mNewsTitle;
	@BindView(R.id.news_text) TextView mNewsText;
	@BindView(R.id.fab_favorite)
	FloatingActionButton mFavorite;
	@BindView(R.id.fab_share)
	FloatingActionButton mShare;
	@BindView(R.id.fab_voice)
	FloatingActionButton mVoice;

	private boolean isFavorite;

	private void startTTS() {
		stopTTS();
		tts = new TextToSpeech(this, new TTSListener());
	}

	private void stopTTS() {
		if (tts != null) {
			tts.shutdown();
			tts.stop();
			tts = null;
		}
	}

	@Override
	protected void onPause () {
		stopTTS();
		super.onPause();
	}

	@Override
	protected void onDestroy () {
		stopTTS();
		super.onDestroy();
	}

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

		isFavorite = StorageManager.getInstance().getFavoritesList().contains(news.getNewsID());

		String[] picUrls = news.getImageURLs();
		String newsTitle = news.getTitle();
		String newsText = news.getCleanContent();
		content = newsText;

		// toolbar
		initToolbar(newsTitle);
		syncFavoriteState();

		// fab
		mFabMenu.setClosedOnTouchOutside(true);
		mVoice.setOnClickListener(view -> {
			startTTS();
		});
		mShare.setOnClickListener(view -> {
			ShareUtils.share(view.getContext(), news);
			if (mFabMenu.isOpened())
				mFabMenu.close(true);
		});
		mFavorite.setOnClickListener(view -> {
			if (isFavorite) {
				if (StorageManager.getInstance().unsetFavorite(news)) {
					Toast.makeText(view.getContext(), "已删除收藏", Toast.LENGTH_LONG).show();
					isFavorite = false;
				} else {
					Toast.makeText(view.getContext(), "操作失败", Toast.LENGTH_LONG).show();
				}
			} else {
				if (StorageManager.getInstance().setFavorite(news)) {
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

		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mToolbar.setNavigationOnClickListener(view -> finish());
	}
}
