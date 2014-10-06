package com.preplay.streak;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PreplayStreakActivity extends Activity {

    private static final String GAMES_DOMAIN = ".streakit.preplaysports.com";

    private static String sAppId;

    public static void setAppId(String appId) {
        sAppId = appId;
    }

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        setContentView(mWebView);
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
                    view.loadData("<b>Impossible to reach your destination</b>",
                            "text/html; charset=UTF-8", null);
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
        if ("staging".equals(host)) {
            urlBuffer.append("staging.");
        } else if (host.length() > 0) {
            return null;
        }
        urlBuffer.append(appId);
        urlBuffer.append(GAMES_DOMAIN);
        urlBuffer.append(intentData.getPath());
        return urlBuffer.toString();
    }
}
