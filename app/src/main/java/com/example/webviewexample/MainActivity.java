package com.example.webviewexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    String mLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.wb);
        mLang = getMyAppLang();
        System.out.println("!!!!!!!!!!" + mLang);

        webView.setWebViewClient(new HelloWebViewClient());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState != null) {
            ((WebView) findViewById(R.id.wb)).restoreState(savedInstanceState.getBundle("webViewState"));
            webView.loadUrl(sp.getString("SAVED_URL", "https://navsegda.net"));
        } else {

            if (mLang.equals("ru")) {
//                webView.loadUrl("https://m.navsegda.net/rest/v2/user/settings/locale");
                webView.loadUrl(sp.getString("SAVED_URL", "https://navsegda.net"));
                System.out.println("Russian!!!" + mLang);
            } else {
                webView.loadUrl(sp.getString("SAVED_URL", "https://en.navsegda.net/matches#p=1"));
                System.out.println("English!!!" + mLang);
            }

        }

        settingWeb();

    }

    class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            webview.loadUrl(url);
            return true;
        }


        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            saveUrl(url);
        }
    }

    public void saveUrl(String url) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SAVED_URL", url);
        editor.apply();
    }

    public String getMyAppLang() {
        if (mLang == null)
            mLang = Locale.getDefault().getLanguage();
        return mLang;
    }

    public void settingWeb() {
        webView.getSettings().setLoadsImagesAutomatically(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.2; en-gb; Nexus One Build/FRF50) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
