package com.preplay.streak;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PreplayStreakActivity extends Activity implements View.OnClickListener {

    private static final String GAMES_DOMAIN = ".streakit.preplaysports.com";

    private static final String GAMES_DOMAIN_STAGING = ".streakit-staging.preplaysports.com";

    private static String sAppId;

    public static void setAppId(String appId) {
        sAppId = appId;
    }

    private WebView mWebView;

    private View mViewClickToRefreshFailingURL;

    private String mLastFailingURL;

    private String mErrorHTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mainView = new FrameLayout(this);
        mWebView = new WebView(this);
        mViewClickToRefreshFailingURL = new View(this);
        mainView.addView(mWebView);
        mainView.addView(mViewClickToRefreshFailingURL);
        setContentView(mainView);

        mViewClickToRefreshFailingURL.setOnClickListener(this);
        mViewClickToRefreshFailingURL.setVisibility(View.GONE);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            String urlToLoadFirst = getUrlToLoadFirst(getIntent());
            if (urlToLoadFirst == null) {
                finish();
                return;
            }
            mWebView.loadUrl(urlToLoadFirst);
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (url != null && url.startsWith("http")) {
                        mLastFailingURL = null;
                        mViewClickToRefreshFailingURL.setVisibility(View.GONE);
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    CookieSyncManager.getInstance().sync();
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description,
                        String failingUrl) {
                    mLastFailingURL = failingUrl;
                    mViewClickToRefreshFailingURL.setVisibility(View.VISIBLE);
                    view.loadData(getErrorHTML(), "text/html; charset=UTF-8", null);
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mWebView != null) {
            String urlToLoadFirst = getUrlToLoadFirst(intent);
            if (urlToLoadFirst != null) {
                mWebView.loadUrl(urlToLoadFirst);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mViewClickToRefreshFailingURL) && mLastFailingURL != null) {
            mWebView.loadUrl(mLastFailingURL);
        }
    }

    private String getUrlToLoadFirst(Intent intent) {
        String appId = getAppId();
        if (TextUtils.isEmpty(appId)) {
            return null;
        } else {
            Uri intentData = intent.getData();
            if (intentData != null) {
                return getURLFromIntent(appId, intentData);
            } else {
                return getURLLanding(appId);
            }
        }
    }

    private String getAppId() {
        String appId = sAppId;
        if (TextUtils.isEmpty(appId)) {
            int resId = getResources().getIdentifier(
                    "preplay_streak_app_id", "string", getPackageName());
            if (resId != 0) {
                appId = getString(resId);
            }
        }
        return appId;
    }

    private String getURLLanding(String appId) {
        StringBuffer urlBuffer = new StringBuffer("http://");
        urlBuffer.append(appId);
        urlBuffer.append(GAMES_DOMAIN);
        return urlBuffer.toString();
    }

    private String getURLFromIntent(String appId, Uri intentData) {

        int resId = getResources().getIdentifier(
                "preplay_streak_scheme", "string", getPackageName());
        if (resId == 0) {
            return null;
        }

        String scheme = getString(resId);
        if (scheme == null || !scheme.equals(intentData.getScheme())) {
            return null;
        }

        String host = intentData.getHost();
        StringBuffer urlBuffer = new StringBuffer("http://");
        urlBuffer.append(appId);
        if ("staging".equals(host)) {
            urlBuffer.append(GAMES_DOMAIN_STAGING);
        } else if (host.length() == 0) {
            urlBuffer.append(GAMES_DOMAIN);
        } else {
            return null;
        }
        urlBuffer.append(intentData.getPath());
        return urlBuffer.toString();
    }

    private String getErrorHTML() {
        if (mErrorHTML == null) {
            loadErrorHTMLFromAssets();
            if(TextUtils.isEmpty(mErrorHTML)) {
                mErrorHTML
                        = "<center><b>Impossible to reach your destination,<br />Check your connection and click here to retry</b></center>";
            }
        }
        return mErrorHTML;
    }

    private void loadErrorHTMLFromAssets() {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = getAssets().open("streak/error.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            mErrorHTML = buf.toString();
        } catch (FileNotFoundException e) {
            mErrorHTML = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
