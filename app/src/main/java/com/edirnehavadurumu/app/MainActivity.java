package com.edirnehavadurumu.app;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class MainActivity extends Activity {
 private WebView web; private final Handler handler=new Handler();
 private final Runnable refresh=new Runnable(){public void run(){web.reload();handler.postDelayed(this,300000);}};
 @Override public void onCreate(Bundle b){super.onCreate(b); web=new WebView(this); web.setWebViewClient(new WebViewClient()); WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setUseWideViewPort(true); s.setLoadWithOverviewMode(true); setContentView(web); web.loadUrl("https://www.mgm.gov.tr/tahmin/saatlik.aspx?m=EDIRNE"); handler.postDelayed(refresh,300000);}
 @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);super.onDestroy();}
}
