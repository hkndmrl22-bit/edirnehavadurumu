package com.edirnehavadurumu.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b){
  super.onCreate(b);
  WebView w=new WebView(this);
  WebSettings s=w.getSettings();
  s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setLoadWithOverviewMode(true); s.setUseWideViewPort(true);
  w.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView view,String url){
    super.onPageFinished(view,url);
    view.evaluateJavascript("(function(){if(document.getElementById('appLogo'))return;var i=document.createElement('img');i.id='appLogo';i.src='file:///android_asset/edirne_logo.jpg';i.alt='Edirne Hava Durumu';i.style.cssText='width:72px;height:72px;object-fit:cover;border-radius:50%;display:block;margin:0 auto 10px;box-shadow:0 2px 12px #0008;';var t=document.querySelector('.top');if(t)t.insertBefore(i,t.firstChild);})()",null);
   }
  });
  w.loadUrl("file:///android_asset/index.html");
  setContentView(w);
 }
}