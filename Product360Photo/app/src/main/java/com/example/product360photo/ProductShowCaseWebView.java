package com.example.product360photo;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Ramesh M Nair (rameshvoltella)
 */

public class ProductShowCaseWebView extends WebView {


    @SuppressLint("JavascriptInterface")
    private void init(Context context) {

        /*Setting the basic settings for the webview*/

        getSettings().setJavaScriptEnabled(true);

        getSettings().setAppCacheEnabled(true);

        getSettings().setSaveFormData(true);

        getSettings().setJavaScriptEnabled(true);

        addJavascriptInterface(new JavaScriptInterface(),
                "jsinterface");
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        getSettings().setAllowFileAccess(true);
        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    final class JavaScriptInterface {
        JavaScriptInterface() {
//                System.out.println("intent");
        }

    }

    public ProductShowCaseWebView(Context context) {
        super(context);
        init(context);
    }

    public ProductShowCaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProductShowCaseWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {

        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }


}
