/**
 * Copyright 2020 Tampere University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.otula.locktaskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

/**
 * Based on: http://wenchaojiang.github.io/blog/realise-Android-kiosk-mode/
 *
 * Usage:
 * 	- install apk
 * 	- open adb shell, execute dpm set-device-owner com.otula.locktaskapp/.DeviceAdmin
 * 	 *
 *  WARNING: DEPENDING ON YOUR DEVICE, IT MIGHT BE _EXTREMELY_ DIFFICULT TO EXIT THE APPLICATION,
 *  AND YOU MIGHT BE REQUIRED TO RE-DEPLOY THE APPLICATION WITHOUT android.intent.category.HOME and android.intent.category.DEFAULT INTENT-FILTERS
 *
 *  NOTE: on some devices, uninstalling the application without first removing the admin permissions might be impossible.
 *  The admin can be removed from adb shell by executing the command dpm remove-active-admin com.otula.locktaskapp/.DeviceAdmin
 *  After this you should be able to uninstall the application normally or by using adb shell command (adb uninstall com.otula.locktaskapp or pm uninstall --user 0 com.otula.locktaskapp)
 */
public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.toString();
	private static final int NIGHTLY_RESET_HOUR = 4;
	private static final int NIGHTLY_RESET_MINUTES = 15;
	private static final String SESSIONSTORAGE_USERNAME = "username"; // session storage key name
	private static final String SESSIONSTORAGE_PASSWORD = "password"; // session storage key name
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String PASSWORD_HIDDEN = new String(new char[PASSWORD.length()]).replace("\0", "*");
	private static final String URL = "https://example.org/index.html";
	private DevicePolicyManager _dpm = null;
	private WebView _webView = null;
	private Handler _timerHandler = null;
	private final Runnable _nightlyResetRunnable = new Runnable() {
		@Override
		public void run() {
			loadStartPage();
			scheduleNightlyReset();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_timerHandler = new Handler(Looper.getMainLooper());

		DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mDPM = new ComponentName(this, DeviceAdmin.class);

		if (dpm.isDeviceOwnerApp(this.getPackageName())) { // check that the admin is set
			String[] packages = {this.getPackageName()}; // get this app package name
			// mDPM is the admin package, and allow the specified packages to lock task
			dpm.setLockTaskPackages(mDPM, packages);
			dpm.setLockTaskFeatures(mDPM, DevicePolicyManager.LOCK_TASK_FEATURE_NONE);
			dpm.addUserRestriction(mDPM, UserManager.DISALLOW_CREATE_WINDOWS); // this will stop toasts!
			startLockTask();  // start lock task
		} else {
			Log.e(TAG, "Application is not device owner!");
		}

		getSupportActionBar().hide(); // hide action bar

		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // on Android 11+
			getWindow().setDecorFitsSystemWindows(false);
			WindowInsetsController controller = getWindow().getInsetsController();
			if(controller != null) {
				controller.hide(WindowInsets.Type.systemBars());
				controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
			}
		} else { // Android <= 10
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

			getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // track visibility changes to hide system bars when they are opened
				@Override
				public void onSystemUiVisibilityChange(int visibility) { // hide navigation bar if it is opened
					if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
						getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
								| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
								| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_FULLSCREEN
								| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // close any status & notification bars if they become visible
					}
				}
			});
		}

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // we can also set screen on manually vs. AndroidManifest.xml

		muteVolume();

		initializeWebView();
	}

	/**
	 *
	 */
	private void initializeWebView(){
		_webView = findViewById(R.id.webview);
		WebSettings webSettings = _webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setDomStorageEnabled(true);
		_webView.setWebChromeClient(new WebChromeClient());
		_webView.setWebViewClient(new WebClient());
	}

	@Override
	public void onBackPressed() {
		// _webView.goBack(); // go back in webview
	}

	/**
	 *
	 */
	private void scheduleNightlyReset() {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime ldt = start.truncatedTo(ChronoUnit.HOURS);
		int hour = ldt.getHour();
		if(hour >= NIGHTLY_RESET_HOUR){
			ldt = ldt.plusDays(1); // the target time has passed for today, schedule for tomorrow, NOTE: there is no need to check minutes, if we are very close to the target time, the page has already been reseted in onResume() (or in previous nightly reset)
		}
		ldt = ldt.withHour(NIGHTLY_RESET_HOUR).withMinute(NIGHTLY_RESET_MINUTES);
		_timerHandler.postDelayed(_nightlyResetRunnable, Duration.between(start, ldt).toMillis());
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadStartPage();
		scheduleNightlyReset();
	}

	@Override
	protected void onPause() {
		_timerHandler.removeCallbacks(_nightlyResetRunnable);
		super.onPause();
	}

	/**
	 *
	 */
	private void loadStartPage() {
		Log.d(TAG, "Loading: "+URL);
		_webView.loadUrl(URL);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				return true; // silently ignore volume buttons
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 *
	 */
	private void muteVolume(){
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
		am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
		am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
		am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
		am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
	}

	/**
	 * Simple implementation of WebViewClient
	 */
	private class WebClient extends WebViewClient {
		private static final long ERROR_RESET_DELAY = 30000;
		private Runnable _errorResetRunnable = new Runnable() {
			@Override
			public void run() {
				loadStartPage();
			}
		};

		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			Log.d(TAG, "received error: "+error.getErrorCode()+" loading start page in "+ERROR_RESET_DELAY+" ms.");
			_timerHandler.postDelayed(_errorResetRunnable, ERROR_RESET_DELAY); // reload page on error
		}

		@Override
		public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
			Log.d(TAG, "http error: "+errorResponse.getStatusCode()+" "+errorResponse.getReasonPhrase());
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "Page finished.");
			view.invalidate();
			_webView.refreshDrawableState();	 // refresh visible UI after page load, just in case, might work without
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			Log.d(TAG, "ssl error");
			//super.onReceivedSslError(view, handler, error);
			handler.proceed(); // skip SSL errors, should not do this, but helps when dealing with self-signed certificates
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "Injecting username "+USERNAME+" and password "+PASSWORD_HIDDEN);
			/* inject data to HTML5 SessionStorage, in this case, username and password, we could also run other arbitrary JavaScript code after the page has finished loading if needed */
			view.evaluateJavascript("sessionStorage.setItem(\'"+SESSIONSTORAGE_USERNAME+"\', \'"+USERNAME+"\')", null); // set credentials
			view.evaluateJavascript("sessionStorage.setItem(\'"+SESSIONSTORAGE_PASSWORD+"\', \'"+PASSWORD+"\')", null); // set credentials
			super.onPageStarted(view, url, favicon);
		}
	} // class WebClient
}
