package com.preplay.streak;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

public class ShareInterface {

    private Context mContext;

    public ShareInterface(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void share(String textToShare) {
        if (TextUtils.isEmpty(textToShare)) {
            return;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }
}
