package com.preplay.streak;

import android.app.Activity;
import android.os.Bundle;
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
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            String appId = sAppId;
            if (appId == null || appId.trim().length() == 0) {
                int resId = getResources()
                        .getIdentifier("preplay_streak_app_id", "string", getPackageName());
                if (resId != 0) {
                    appId = getString(resId);
                }
            }
            if (appId == null || appId.trim().length() == 0) {
                appId = "landing";
            }
            String url = "http://" + appId + ".streakit.preplaysports.com";
            mWebView.loadUrl(url);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }
}
