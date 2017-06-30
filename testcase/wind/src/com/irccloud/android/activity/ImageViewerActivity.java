/*
 * Copyright (c) 2015 IRCCloud, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irccloud.android.activity;



import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.irccloud.android.AsyncTaskEx;
import com.irccloud.android.BuildConfig;
import com.irccloud.android.ChromeCopyLinkBroadcastReceiver;
import com.irccloud.android.ColorScheme;
import com.irccloud.android.IRCCloudApplication;
import com.irccloud.android.NetworkConnection;
import com.irccloud.android.R;
import com.irccloud.android.ShareActionProviderHax;

import org.chromium.customtabsclient.shared.CustomTabsHelper;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class ImageViewerActivity extends BaseActivity implements ShareActionProviderHax.OnShareActionProviderSubVisibilityChangedListener {
    private MediaPlayer player = null;
    private String mVideoURL = null;
    private String mImageURL = null;

    private class OEmbedTask extends AsyncTaskEx<String, Void, String> {
        private String provider = null;
        private String giphy_fallback = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject o = NetworkConnection.getInstance().fetchJSON(params[0]);
                if (o.has("provider_name"))
                    provider = o.getString("provider_name");

                if (provider != null && provider.equals("Giphy") && o.has("image") && o.getString("image").endsWith(".gif"))
                    giphy_fallback = o.getString("image");

                if ((provider != null && provider.equals("Giphy")) || o.getString("type").equalsIgnoreCase("photo"))
                    return o.getString("url");
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                if (provider != null && provider.equals("Giphy"))
                    new GiphyTask().execute(url.substring(url.indexOf("/gifs/") + 6), giphy_fallback);
                else
                    loadImage(url);
            } else {
                fail();
            }
        }
    }

    public class ImgurImageTask extends AsyncTaskEx<String, Void, String> {
        private final String IMAGE_URL = (BuildConfig.MASHAPE_KEY.length() > 0) ? "https://imgur-apiv3.p.mashape.com/3/image/" : "https://api.imgur.com/3/image/";

        @Override
        protected String doInBackground(String... params) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                if (BuildConfig.MASHAPE_KEY.length() > 0)
                    headers.put("X-Mashape-Authorization", BuildConfig.MASHAPE_KEY);
                headers.put("Authorization", "Client-ID " + BuildConfig.IMGUR_KEY);
                JSONObject o = NetworkConnection.getInstance().fetchJSON(IMAGE_URL + params[0], headers);
                if (o.getBoolean("success")) {
                    JSONObject data = o.getJSONObject("data");
                    if (data.getString("type").startsWith("image/") && !data.getBoolean("animated"))
                        return data.getString("link");
                    else if (data.getBoolean("animated"))
                        return data.getString("mp4");
                }
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                if (url.endsWith(".mp4")) {
                    loadVideo(url);
                } else {
                    loadImage(url);
                }
            } else {
                fail();
            }
        }
    }

    public class ImgurGalleryTask extends AsyncTaskEx<String, Void, String> {
        private String type = "gallery";
        private final String GALLERY_URL = (BuildConfig.MASHAPE_KEY.length() > 0) ? "https://imgur-apiv3.p.mashape.com/3/" : "https://api.imgur.com/3/";

        public ImgurGalleryTask(String type) {
            this.type = type;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                if (BuildConfig.MASHAPE_KEY.length() > 0)
                    headers.put("X-Mashape-Authorization", BuildConfig.MASHAPE_KEY);
                headers.put("Authorization", "Client-ID " + BuildConfig.IMGUR_KEY);
                JSONObject o = NetworkConnection.getInstance().fetchJSON(GALLERY_URL + type + "/" + params[0], headers);
                if (o.getBoolean("success")) {
                    JSONObject data = o.getJSONObject("data");
                    if((data.has("images_count") && data.getInt("images_count") == 1) || !data.getBoolean("is_album")) {
                        if(data.getBoolean("is_album"))
                            data = data.getJSONArray("images").getJSONObject(0);
                        if (data.getString("type").startsWith("image/") && !data.getBoolean("animated"))
                            return data.getString("link");
                        else if (data.getBoolean("animated"))
                            return data.getString("mp4");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                if (url.endsWith(".mp4")) {
                    loadVideo(url);
                } else {
                    loadImage(url);
                }
            } else {
                fail();
            }
        }
    }

    public class GfyCatTask extends AsyncTaskEx<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                JSONObject o = NetworkConnection.getInstance().fetchJSON("https://gfycat.com/cajax/get/" + params[0], headers);
                if (o.has("gfyItem")) {
                    JSONObject data = o.getJSONObject("gfyItem");
                    if (data.has("mp4Url") && data.getString("mp4Url").length() > 0)
                        return data.getString("mp4Url");
                    else if (data.has("gifUrl") && data.getString("gifUrl").length() > 0)
                        return data.getString("gifUrl");
                }
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                if (url.endsWith(".mp4")) {
                    loadVideo(url);
                } else {
                    loadImage(url);
                }
            } else {
                fail();
            }
        }
    }

    public class GiphyTask extends AsyncTaskEx<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                JSONObject o = NetworkConnection.getInstance().fetchJSON("https://api.giphy.com/v1/gifs/" + params[0] + "?api_key=dc6zaTOxFJmzC", headers);
                if (o.has("data") && o.getJSONObject("data").has("images")) {
                    JSONObject data = o.getJSONObject("data").getJSONObject("images").getJSONObject("original");
                    if (data.has("mp4") && data.getString("mp4").length() > 0)
                        return data.getString("mp4");
                    else if (data.getString("url").endsWith(".gif"))
                        return data.getString("url");
                }
            } catch (Exception e) {
            }
            return params[1];
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                if (url.endsWith(".mp4")) {
                    loadVideo(url);
                } else {
                    loadImage(url);
                }
            } else {
                fail();
            }
        }
    }

    private class ClLyTask extends AsyncTaskEx<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject o = NetworkConnection.getInstance().fetchJSON(params[0]);
                if (o.getString("item_type").equalsIgnoreCase("image"))
                    return o.getString("content_url");
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                loadImage(url);
            } else {
                fail();
            }
        }
    }

    private class WikiTask extends AsyncTaskEx<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject o = NetworkConnection.getInstance().fetchJSON(params[0]);
                JSONObject pages = o.getJSONObject("query").getJSONObject("pages");
                Iterator<String> i = pages.keys();
                String pageid = i.next();
                return pages.getJSONObject(pageid).getJSONArray("imageinfo").getJSONObject(0).getString("url");
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                loadImage(url);
            } else {
                fail();
            }
        }
    }

    WebView mImage;
    ProgressBar mSpinner;
    ProgressBar mProgress;
    Toolbar toolbar;
    private static Timer mHideTimer = null;
    TimerTask mHideTimerTask = null;

    public class JSInterface {
        @JavascriptInterface
        public void imageFailed() {
            fail();
        }

        @JavascriptInterface
        public void imageClicked() {
            ImageViewerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toolbar.getVisibility() == View.VISIBLE) {
                        if (mHideTimerTask != null)
                            mHideTimerTask.cancel();
                        if (Build.VERSION.SDK_INT > 16) {
                            toolbar.animate().alpha(0).translationY(-toolbar.getHeight()).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    toolbar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            toolbar.setVisibility(View.GONE);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT > 16) {
                            toolbar.setAlpha(0);
                            toolbar.animate().alpha(1).translationY(0);
                        }
                        toolbar.setVisibility(View.VISIBLE);
                        hide_actionbar();
                    }
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mImageURL != null)
            outState.putString("imageURL", mImageURL);

        if(mVideoURL != null)
            outState.putString("videoURL", mVideoURL);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ImageViewerTheme);
        mHideTimer = new Timer("actionbar-hide-timer");
        if (savedInstanceState == null)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        setContentView(R.layout.activity_imageviewer);
        toolbar = findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolbar);
        } catch (Throwable t) {
        }
        if (Build.VERSION.SDK_INT < 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        } else if(Build.VERSION.SDK_INT >= 21) {
            Bitmap cloud = BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo);
            if(cloud != null) {
                setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), cloud, getResources().getColor(android.R.color.black)));
            }
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));
            if(Build.VERSION.SDK_INT >= 23) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() &~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        getSupportActionBar().setTitle("Image Viewer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_translucent));

        mImage = findViewById(R.id.image);
        mImage.setBackgroundColor(0);
        mImage.addJavascriptInterface(new JSInterface(), "Android");
        mImage.getSettings().setBuiltInZoomControls(true);
        if (Integer.parseInt(Build.VERSION.SDK) >= 19)
            mImage.getSettings().setDisplayZoomControls(!getPackageManager().hasSystemFeature("android.hardware.touchscreen"));
        mImage.getSettings().setJavaScriptEnabled(true);
        mImage.getSettings().setLoadWithOverviewMode(true);
        mImage.getSettings().setUseWideViewPort(true);
        mImage.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgress.setProgress(newProgress);
            }
        });
        mImage.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mSpinner.setVisibility(View.GONE);
                mProgress.setVisibility(View.GONE);
                mImage.setVisibility(View.VISIBLE);
                hide_actionbar();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                fail();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                mSpinner.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
            }
        });
        mSpinner = findViewById(R.id.spinner);
        mProgress = findViewById(R.id.progress);
        final SurfaceView v = findViewById(R.id.video);
        v.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        v.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (player != null) {
                        player.setDisplay(surfaceHolder);
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                int videoWidth = player.getVideoWidth();
                                int videoHeight = player.getVideoHeight();

                                int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                                int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

                                int scaledWidth = (int) (((float) videoWidth / (float) videoHeight) * (float) screenHeight);
                                int scaledHeight = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

                                android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
                                lp.width = screenWidth;
                                lp.height = scaledHeight;
                                if (lp.height > screenHeight && scaledWidth < screenWidth) {
                                    lp.width = scaledWidth;
                                    lp.height = screenHeight;
                                }
                                v.setLayoutParams(lp);

                                player.start();
                                mSpinner.setVisibility(View.GONE);
                                mProgress.setVisibility(View.GONE);
                                hide_actionbar();
                            }
                        });
                        player.prepareAsync();
                    }
                } catch (Exception e) {
                    fail();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (player != null) {
                    try {
                        player.stop();
                    } catch (IllegalStateException e) {
                    }
                    player.release();
                    player = null;
                }
            }
        });

        findViewById(R.id.video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolbar.getVisibility() == View.VISIBLE) {
                    if (mHideTimerTask != null)
                        mHideTimerTask.cancel();
                    if (Build.VERSION.SDK_INT > 16) {
                        toolbar.animate().alpha(0).translationY(-toolbar.getHeight()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                toolbar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        toolbar.setVisibility(View.GONE);
                    }
                } else {
                    if (Build.VERSION.SDK_INT > 16) {
                        toolbar.setAlpha(0);
                        toolbar.animate().alpha(1).translationY(0);
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    hide_actionbar();
                }
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey("imageURL")) {
            loadImage(savedInstanceState.getString("imageURL"));
        } else if(savedInstanceState != null && savedInstanceState.containsKey("videoURL")) {
            loadVideo(savedInstanceState.getString("videoURL"));
        } else if (getIntent() != null && getIntent().getDataString() != null) {
            String url = getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http");
            String lower = url.toLowerCase().replace("https://", "").replace("http://", "");
            if (lower.startsWith("www.dropbox.com/")) {
                if (lower.startsWith("www.dropbox.com/s/")) {
                    url = url.replace("://www.dropbox.com/s/", "://dl.dropboxusercontent.com/s/");
                } else {
                    url = url + "?dl=1";
                }
            } else if ((lower.startsWith("d.pr/i/") || lower.startsWith("droplr.com/i/")) && !lower.endsWith("+")) {
                url += "+";
            } else if (lower.startsWith("imgur.com/") || lower.startsWith("www.imgur.com/") || lower.startsWith("m.imgur.com/")) {
                String id = url.replace("https://", "").replace("http://", "");
                id = id.substring(id.indexOf("/") + 1);

                if (!id.contains("/") && id.length() > 0) {
                    new ImgurImageTask().execute(id);
                } else if (id.startsWith("gallery/") && id.length() > 8) {
                    new ImgurGalleryTask("gallery").execute(id.substring(8));
                } else if (id.startsWith("a/") && id.length() > 2) {
                    new ImgurGalleryTask("album").execute(id.substring(2));
                } else {
                    fail();
                }
                return;
            } else if (lower.startsWith("i.imgur.com") && (lower.endsWith(".gifv") || lower.endsWith(".gif"))) {
                String id = url.replace("https://", "").replace("http://", "");
                id = id.substring(id.indexOf("/") + 1);
                id = id.substring(0, id.lastIndexOf("."));
                new ImgurImageTask().execute(id);
                return;
            } else if (lower.startsWith("gfycat.com/") || lower.startsWith("www.gfycat.com/")) {
                String id = url;
                if (id.endsWith("/"))
                    id = id.substring(0, id.length() - 1);
                id = id.substring(id.lastIndexOf("/") + 1, id.length());
                new GfyCatTask().execute(id);
                return;
            } else if (lower.startsWith("giphy.com/") || lower.startsWith("www.giphy.com/") || lower.startsWith("gph.is/")) {
                if (lower.contains("/gifs/") && lower.lastIndexOf("/") > lower.indexOf("/gifs/") + 6)
                    url = url.substring(0, lower.lastIndexOf("/"));
                new OEmbedTask().execute("https://giphy.com/services/oembed/?url=" + url);
                return;
            } else if (lower.startsWith("flickr.com/") || lower.startsWith("www.flickr.com/")) {
                new OEmbedTask().execute("https://www.flickr.com/services/oembed/?format=json&url=" + url);
                return;
            } else if (lower.startsWith("instagram.com/") || lower.startsWith("www.instagram.com/") || lower.startsWith("instagr.am/") || lower.startsWith("www.instagr.am/")) {
                new OEmbedTask().execute("http://api.instagram.com/oembed?url=" + url);
                return;
            } else if (lower.startsWith("cl.ly")) {
                new ClLyTask().execute(url);
                return;
            } else if (url.matches(".*/wiki/.*/File:.*")) {
                new WikiTask().execute(url.replaceAll("/wiki/.*/File:", "/w/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles=File:"));
            } else if (lower.startsWith("leetfiles.com/") || lower.startsWith("www.leetfiles.com/")) {
                url = url.replace("www.", "").replace("leetfiles.com/image/", "i.leetfiles.com/").replace("?id=", "");
            } else if (lower.startsWith("leetfil.es/") || lower.startsWith("www.leetfil.es/")) {
                url = url.replace("www.", "").replace("leetfil.es/image/", "i.leetfiles.com/").replace("?id=", "");
            }
            loadImage(url);
        } else {
            finish();
        }
    }

    private void loadVideo(String urlStr) {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if(mCustomTabsSession != null)
                mCustomTabsSession.mayLaunchUrl(Uri.parse(urlStr), null, null);
            Answers.getInstance().logContentView(new ContentViewEvent().putContentType("Animation"));
            player = new MediaPlayer();
            findViewById(R.id.video).setVisibility(View.VISIBLE);

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mSpinner.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    hide_actionbar();
                }
            });

            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    if(mediaPlayer == player)
                        fail();
                    return false;
                }
            });

            player.setDataSource(ImageViewerActivity.this, Uri.parse(urlStr));
            player.setLooping(true);
            player.setVolume(0, 0);

            try {
                if (Build.VERSION.SDK_INT >= 16) {
                    NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
                    if (nfc != null) {
                        nfc.setNdefPushMessage(new NdefMessage(NdefRecord.createUri(urlStr)), this);
                    }
                }
            } catch (Exception e) {
            }

            mVideoURL = urlStr;
        } catch (Exception e) {
            NetworkConnection.printStackTraceToCrashlytics(e);
            fail();
        }
    }

    private void loadImage(String urlStr) {
        try {
            if(urlStr.toLowerCase().endsWith("gif"))
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if(mCustomTabsSession != null)
                mCustomTabsSession.mayLaunchUrl(Uri.parse(urlStr), null, null);
            Answers.getInstance().logContentView(new ContentViewEvent().putContentType("Image"));
            URL url = new URL(urlStr);

            mImage.loadDataWithBaseURL(null, "<!DOCTYPE html>\n" +
                    "<html><head><style>html, body, table { height: 100%; width: 100%; background-color: #000;}</style></head>\n" +
                    "<body>\n" +
                    "<table><tr><td>" +
                    "<img src='" + url.toString() + "' width='100%' onerror='Android.imageFailed()' onclick='Android.imageClicked()' style='background-color: #fff;'/>\n" +
                    "</td></tr></table>" +
                    "</body>\n" +
                    "</html>", "text/html", "UTF-8", null);

            try {
                if (Build.VERSION.SDK_INT >= 16) {
                    NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
                    if (nfc != null) {
                        nfc.setNdefPushMessage(new NdefMessage(NdefRecord.createUri(urlStr)), this);
                    }
                }
            } catch (Exception e) {
            }
            mImageURL = urlStr;
        } catch (Exception e) {
            fail();
        }
    }

    private void fail() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IRCCloudApplication.getInstance().onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSpinner != null && mSpinner.getVisibility() == View.GONE)
            hide_actionbar();
        if(mVideoURL != null)
            loadVideo(mVideoURL);
        IRCCloudApplication.getInstance().onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            try {
                unbindService(mCustomTabsConnection);
            } catch (Exception e) {
            }
        }
    }

    CustomTabsSession mCustomTabsSession = null;
    CustomTabsServiceConnection mCustomTabsConnection = new CustomTabsServiceConnection() {
        @Override
        public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
            client.warmup(0);
            mCustomTabsSession = client.newSession(null);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                String packageName = CustomTabsHelper.getPackageNameToUse(this);
                if (packageName != null && packageName.length() > 0)
                    CustomTabsClient.bindCustomTabsService(this, packageName, mCustomTabsConnection);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (share != null) {
            share.setOnShareTargetSelectedListener(null);
            share.onShareActionProviderSubVisibilityChangedListener = null;
            share.setSubUiVisibilityListener(null);
            share.setVisibilityListener(null);
        }
        if (mHideTimer != null) {
            mHideTimer.cancel();
            mHideTimer = null;
        }
        if (mImage != null) {
            mImage.setWebViewClient(null);
            mImage.setWebChromeClient(null);
            mImage.removeJavascriptInterface("Android");
        }
        if (player != null)
            player.release();
    }

    private void hide_actionbar() {
        if (mHideTimer != null) {
            if (mHideTimerTask != null)
                mHideTimerTask.cancel();
            mHideTimerTask = new TimerTask() {
                @Override
                public void run() {
                    ImageViewerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT > 16) {
                                toolbar.animate().alpha(0).translationY(-toolbar.getHeight()).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        toolbar.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                toolbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            };
            mHideTimer.schedule(mHideTimerTask, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }

    ShareActionProviderHax share = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_imageviewer, menu);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && getIntent() != null && getIntent().getDataString() != null) {
            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http"));
            intent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, getPackageName());
            intent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);

            MenuItem shareItem = menu.findItem(R.id.action_share);
            share = (ShareActionProviderHax) MenuItemCompat.getActionProvider(shareItem);
            share.onShareActionProviderSubVisibilityChangedListener = this;
            share.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                    String name = intent.getComponent().getPackageName();
                    try {
                        name = String.valueOf(getPackageManager().getActivityInfo(intent.getComponent(), 0).loadLabel(getPackageManager()));
                    } catch (PackageManager.NameNotFoundException e) {
                        NetworkConnection.printStackTraceToCrashlytics(e);
                    }
                    Answers.getInstance().logShare(new ShareEvent().putContentType((player != null) ? "Animation" : "Image").putMethod(name));
                    return false;
                }
            });
            share.setShareIntent(intent);
        } else {
            MenuItem shareItem = menu.findItem(R.id.action_share);
            if(shareItem != null && shareItem.getIcon() != null)
                shareItem.getIcon().mutate().setColorFilter(0xFFCCCCCC, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mHideTimerTask != null)
            mHideTimerTask.cancel();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
            return true;
        } else if (item.getItemId() == R.id.action_browser) {
            Answers.getInstance().logShare(new ShareEvent().putContentType((player != null) ? "Animation" : "Image").putMethod("Open in Browser"));
            if(!PreferenceManager.getDefaultSharedPreferences(IRCCloudApplication.getInstance().getApplicationContext()).getBoolean("browser", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ColorScheme.getInstance().navBarColor);
                builder.addDefaultShareMenuItem();
                builder.addMenuItem("Copy URL", PendingIntent.getBroadcast(this, 0, new Intent(this, ChromeCopyLinkBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));

                CustomTabsIntent intent = builder.build();
                intent.intent.setData(Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
                if(Build.VERSION.SDK_INT >= 22)
                    intent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + getPackageName()));
                if (Build.VERSION.SDK_INT >= 16 && intent.startAnimationBundle != null) {
                    startActivity(intent.intent, intent.startAnimationBundle);
                } else {
                    startActivity(intent.intent);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
                startActivity(intent);
            }
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            if (Build.VERSION.SDK_INT >= 16 && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                DownloadManager d = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                if (d != null) {
                    String uri = getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http");
                    if(uri.startsWith("https://"))
                        uri = "http://" + uri.substring(8);
                    DownloadManager.Request r = new DownloadManager.Request(Uri.parse(uri));
                    r.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, getIntent().getData().getLastPathSegment());
                    r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    r.allowScanningByMediaScanner();
                    d.enqueue(r);
                    Answers.getInstance().logShare(new ShareEvent().putContentType((player != null) ? "Animation" : "Image").putMethod("Download"));
                }
            }
            return true;
        } else if (item.getItemId() == R.id.action_copy) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if(clipboard != null) {
                android.content.ClipData clip = android.content.ClipData.newRawUri("IRCCloud Image URL", Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ImageViewerActivity.this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
                Answers.getInstance().logShare(new ShareEvent().putContentType((player != null) ? "Animation" : "Image").putMethod("Copy to Clipboard"));
            } else {
                Toast.makeText(ImageViewerActivity.this, "Clipboard service unavailable, please try again", Toast.LENGTH_SHORT).show();
            }
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && item.getItemId() == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http"));
            intent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, getPackageName());
            intent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "Share Image"));
            Answers.getInstance().logShare(new ShareEvent().putContentType((player != null) ? "Animation" : "Image"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShareActionProviderSubVisibilityChanged(boolean visible) {
        if (visible) {
            if (mHideTimerTask != null)
                mHideTimerTask.cancel();
        } else {
            hide_actionbar();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            DownloadManager d = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (d != null) {
                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(getIntent().getDataString().replace(getResources().getString(R.string.IMAGE_SCHEME), "http")));
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, getIntent().getData().getLastPathSegment());
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                r.allowScanningByMediaScanner();
                d.enqueue(r);
                Answers.getInstance().logShare(new ShareEvent().putContentType((player != null) ? "Animation" : "Image").putMethod("Download"));
            }
        } else {
            Toast.makeText(this, "Unable to download: permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}