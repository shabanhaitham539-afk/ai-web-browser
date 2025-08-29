package com.aiwebbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText urlBar;
    private ImageButton btnBack, btnForward, btnRefresh, btnModelSelection;
    private LinearLayout translationPanel;
    private TextView selectedText, translationResult;
    private TranslationService translationService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupWebView();
        setupListeners();
        
        translationService = new TranslationService(this);
        
        // Load default page
        webView.loadUrl("https://www.google.com");
    }
    
    private void initViews() {
        webView = findViewById(R.id.webview);
        urlBar = findViewById(R.id.url_bar);
        btnBack = findViewById(R.id.btn_back);
        btnForward = findViewById(R.id.btn_forward);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnModelSelection = findViewById(R.id.btn_model_selection);
        translationPanel = findViewById(R.id.translation_panel);
        selectedText = findViewById(R.id.selected_text);
        translationResult = findViewById(R.id.translation_result);
    }
    
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                urlBar.setText(url);
                
                // Inject JavaScript for text selection
                String js = "javascript:(function() {" +
                        "document.addEventListener('mouseup', function() {" +
                        "var selectedText = window.getSelection().toString();" +
                        "if (selectedText.length > 0) {" +
                        "Android.onTextSelected(selectedText);" +
                        "}" +
                        "});" +
                        "})()";
                view.loadUrl(js);
            }
        });
        
        // Add JavaScript interface for text selection
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void onTextSelected(String text) {
                runOnUiThread(() -> showTranslationPanel(text));
            }
        }, "Android");
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        
        btnForward.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        
        btnRefresh.setOnClickListener(v -> webView.reload());
        
        btnModelSelection.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModelSelectionActivity.class);
            startActivity(intent);
        });
        
        urlBar.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                loadUrl();
                return true;
            }
            return false;
        });
        
        findViewById(R.id.btn_close_translation).setOnClickListener(v -> {
            translationPanel.setVisibility(View.GONE);
        });
    }
    
    private void loadUrl() {
        String url = urlBar.getText().toString().trim();
        if (!url.isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                if (url.contains(".")) {
                    url = "https://" + url;
                } else {
                    url = "https://www.google.com/search?q=" + url;
                }
            }
            webView.loadUrl(url);
        }
    }
    
    private void showTranslationPanel(String text) {
        selectedText.setText(text);
        translationResult.setText("Translating...");
        translationPanel.setVisibility(View.VISIBLE);
        
        // Auto-detect and translate to English (you can make this configurable)
        translationService.translateText(text, "English", new TranslationService.TranslationCallback() {
            @Override
            public void onSuccess(String translation) {
                runOnUiThread(() -> {
                    translationResult.setText(translation);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    translationResult.setText("Translation failed: " + error);
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
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