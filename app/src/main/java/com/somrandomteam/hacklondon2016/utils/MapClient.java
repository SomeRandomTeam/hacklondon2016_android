package com.somrandomteam.hacklondon2016.utils;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by abhinavmishra on 28/02/2016.
 */
public class MapClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }
}
