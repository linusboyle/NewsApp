/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.core;

import android.annotation.SuppressLint;
import android.os.Process;
import android.util.Log;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.io.StringWriter;
import java.io.PrintWriter;

public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	private static final String TAG = "NewsExceptionHandler";
	@SuppressLint("StaticFieldLeak")
	private static ExceptionHandler _instance;
	private Thread.UncaughtExceptionHandler defaultHandler;
	private Context _context;

	private ExceptionHandler() {}

	public static ExceptionHandler getInstance() {
		if (_instance == null)
			_instance = new ExceptionHandler();
		return _instance;
	}

	public void init(Context context) {
		_context = context;
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	private boolean handleException(Throwable e) {
		if (e == null)
			return false;
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		e.printStackTrace(printWriter);
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		final String logMsg = stringWriter.toString();

		Thread notifyThread = new Thread(() -> {
			Looper.prepare();
			Toast.makeText(_context, "程序发生异常，即将关闭...", Toast.LENGTH_LONG).show();
			Log.e(TAG, logMsg);
			Looper.loop();
		});
		notifyThread.start();
		return true;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (!handleException(e) && defaultHandler != null) {
			defaultHandler.uncaughtException(t, e);
		} else {
			try {
				// wait for the toast to show up
				Thread.sleep(3000);
			} catch (InterruptedException ex) {
				Log.e(TAG, "Interrupted:" + ex.getMessage());
			} catch (Exception ex) {
				Log.e(TAG, "Exception:" + ex.getMessage());
			}

			// double insurance
			Process.killProcess(Process.myPid());
			System.exit(10);
		}
	}
}