package com.example.webviewexample;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    WebView webView;
    String splashURL = "";
    String secret = "";
    String taskURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbSplashUrl = database.getReference("splash_url");
        DatabaseReference dbSecret = database.getReference("secret");
        DatabaseReference dbTaskUrl = database.getReference("task_url");

        dbSplashUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                splashURL = dataSnapshot.getValue(String.class);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String savedUrl = sp.getString("SAVED_URL", null);

                if (savedUrl == null) {
                    webView.loadUrl(splashURL);
                } else {
                    webView.loadUrl(savedUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        dbSecret.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                secret = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        dbTaskUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskURL = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        webView = (WebView) findViewById(R.id.wb);

        webView.setWebViewClient(new HelloWebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                intent.setType("*/*");

                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(MainActivity.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

        });

        settingWeb();
    }

    class HelloWebViewClient extends WebViewClient {

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);

            if (url.contains("money")) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("secret", secret);
                startActivity(intent);
            } else if (url.contains("main")) {
                webView.loadUrl(taskURL);
            }
            saveUrl(url);
        }

        public void saveUrl(String url) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("SAVED_URL", url);
            editor.apply();
        }
    }

    public void settingWeb() {
        webView.getSettings().setLoadsImagesAutomatically(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko)");
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(MainActivity.this.getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
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