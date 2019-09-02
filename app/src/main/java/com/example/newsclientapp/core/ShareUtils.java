/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.newsclientapp.network.NewsEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareUtils {
	private static final String TAG = "ShareUtils";
	private static final String EXTERNAL_TMP= "newsclient.jpeg";

	public static void share(Context context, NewsEntity newsEntity) {
		Log.v(TAG, "Start sharing...");
		String [] imgs = newsEntity.getImageURLs();
		if (imgs != null && imgs.length > 0) {
			Glide.with(context).downloadOnly().load(imgs[0]).listener(new RequestListener<File>() {
				@Override
				public boolean onLoadFailed (@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
					Log.w(TAG, "Load target image failed; fallback to text");
					Log.w(TAG, "Glide error: " + e.getMessage());
					sharePureText(context, newsEntity);
					return false;
				}

				@Override
				public boolean onResourceReady (File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
					File cachedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
					File tmpfile = new File(cachedir, EXTERNAL_TMP);
					copyFile(resource, tmpfile);
					Uri uri = Uri.fromFile(tmpfile);
					shareWithImg(context, newsEntity, uri);
					return true;
				}
			}).preload();
		} else {
			sharePureText(context, newsEntity);
		}
	}

	private static void sharePureText(Context context, NewsEntity newsEntity) {
		Log.d(TAG, "pure text sharing launched");
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT,
				newsEntity.getTitle() + "\n\n" + newsEntity.getCleanContent());
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	private static void shareWithImg(Context context, NewsEntity newsEntity, Uri uri) {
		Log.d(TAG, "image sharing launched");
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT,
				newsEntity.getTitle() + "\n\n" + newsEntity.getCleanContent());
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	/**
	 * 复制文件
	 * Taken from https://www.jianshu.com/p/a931e2a58c4e --HZL
	 *
	 * @param source 输入文件
	 * @param target 输出文件
	 */
	private static void copyFile(File source, File target) {
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			//noinspection ResultOfMethodCallIgnored
			target.createNewFile();
			fileInputStream = new FileInputStream(source);
			fileOutputStream = new FileOutputStream(target);
			byte[] buffer = new byte[1024];
			while (fileInputStream.read(buffer) > 0) {
				fileOutputStream.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
