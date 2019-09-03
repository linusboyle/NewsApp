/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import butterknife.BindView;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newsclientapp.core.ShareUtils;
import com.example.newsclientapp.network.NewsEntity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.newsclientapp.R;
import com.example.newsclientapp.storage.StorageManager;
import com.example.newsclientapp.ui.fragment.RecommendFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.Arrays;
import java.util.Locale;

public class NewsDetailActivity extends BaseActivity {

	private static String DATA = "web_data";
	private static final String TAG = "NewsDetailActivity";
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
	// @BindView(R.id.news_image) ImageView mImageView;
	@BindView(R.id.banner)
	Banner _banner;
	@BindView(R.id.video)
	JzvdStd _video;
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
		Jzvd.releaseAllVideos();
		super.onPause();
	}

	@Override
	public void onBackPressed () {
		Jzvd.backPress();
		super.onBackPressed();
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

	private void initRecommendationFragment(NewsEntity news) {
		String keyword = news.getAKeyword();
		if (keyword == null)
			return; // no instantiation
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		RecommendFragment recommendFragment =
				RecommendFragment.newFragment(news);
		fragmentTransaction.add(R.id.detail_frame, recommendFragment);
		fragmentTransaction.commit();
	}

	@Override
	protected void initData (Bundle savedInstanceState) {
        NewsEntity news = (NewsEntity) getIntent().getSerializableExtra(DATA);

		assert news != null;
		isFavorite = StorageManager.getInstance().getFavoritesList().contains(news.getNewsID());
		initRecommendationFragment(news);

		String[] picUrls = news.getImageURLs();
		String newsTitle = news.getTitle();
		String newsText = news.getCleanContent();
		String video = news.getVideo();
		content = newsText;

		// toolbar
		initToolbar(newsTitle);
		syncFavoriteState();

		// fab
		mFabMenu.setClosedOnTouchOutside(true);
		mVoice.setOnClickListener(view -> startTTS());
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
		initNewsPage(video, picUrls, newsTitle, newsText);
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

	private void initNewsPage(String videoUrl, String[] picUrls, String newsTitle, String newsText) {
		if (videoUrl.length() != 0) {
			Log.i(TAG, "display video");
			_video.setUp(videoUrl, "视频", JzvdStd.SCREEN_NORMAL);
			_banner.setVisibility(View.GONE);
		} else {
			_video.setVisibility(View.GONE);
			if (picUrls == null || picUrls.length == 0 || TextUtils.isEmpty(picUrls[0])) {
				_banner.setVisibility(View.GONE);
			} else {
				for (String string : picUrls)
					Log.i(TAG, string);
				_banner.setImageLoader(new GlideImageLoader());
				_banner.setDelayTime(3000);
				_banner.setImages(Arrays.asList(picUrls));
				_banner.start();
			}
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

	private class GlideImageLoader extends ImageLoader {
		@Override
		public void displayImage (Context context, Object path, ImageView imageView) {
			Glide.with(context)
			.load((String)path)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.glide_pic_default)
			.error(R.drawable.glide_pic_failed)
			.into(imageView);
		}
	}
}
