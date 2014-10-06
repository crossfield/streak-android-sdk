package com.preplay.streak;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PreplayStreakActivity extends Activity {

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
            mWebView.loadUrl(getUrlLandingPage());
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
                    view.loadData("<b>Impossible to reach your destination</b>", "text/html; charset=UTF-8", null);
                }
            });
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

    private String getUrlLandingPage() {
        String appId = sAppId;
        if (appId == null || appId.trim().length() == 0) {
            int resId = getResources()
                    .getIdentifier("preplay_streak_app_id", "string", getPackageName());
            if (resId != 0) {
                appId = getString(resId);
            }
        }
        if (appId == null || appId.trim().length() == 0) {
            return "http://preplay-share.s3.amazonaws.com/Streakgame/index.html";
        } else {
            return "http://" + appId + ".streakit.preplaysports.com";
        }
    }
}
