package com.edirnehavadurumu.app;
import android.app.*;import android.os.*;import android.graphics.Color;import android.view.*;import android.widget.*;import android.webkit.*;
public class MainActivity extends Activity{
 LinearLayout list; TextView status; WebView w; Handler h=new Handler();
 String[] districts={"Edirne Merkez","Keşan","Enez","İpsala","Uzunköprü","Havsa","Meriç","Süloğlu","Lalapaşa"};
 TextView t(String s,int z){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(Color.WHITE);v.setPadding(20,16,20,16);return v;}
 public void onCreate(Bundle b){super.onCreate(b);ui();collector();}
 void ui(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setBackgroundColor(Color.rgb(15,42,68));
 TextView hd=t("EDİRNE HAVA DURUMU",24);hd.setGravity(17);hd.setTypeface(null,1);r.addView(hd);
 Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,districts));r.addView(sp);
 status=t("MGM verileri alınıyor…",15);status.setGravity(17);r.addView(status);
 list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);ScrollView s=new ScrollView(this);s.addView(list);r.addView(s,new LinearLayout.LayoutParams(-1,0,1));
 TextView f=t("MGM verisi • otomatik güncelleme: 5 dakika\n/ edirnehavadurumu",13);f.setGravity(17);r.addView(f);setContentView(r);}
 void collector(){w=new WebView(this);w.setVisibility(View.GONE);w.getSettings().setJavaScriptEnabled(true);w.getSettings().setDomStorageEnabled(true);w.setWebViewClient(new WebViewClient(){public void onPageFinished(WebView v,String u){v.evaluateJavascript("(function(){return document.body?document.body.innerText:''})()",x->show(x));}});addContentView(w,new ViewGroup.LayoutParams(1,1));load();h.postDelayed(new Runnable(){public void run(){load();h.postDelayed(this,300000);}},300000);}
 void load(){w.loadUrl("https://www.mgm.gov.tr/?il=Edirne");}
 void show(String x){status.setText("MGM verisi alındı • "+android.text.format.DateFormat.format("HH:mm",new java.util.Date()));list.removeAllViews();String s=x.replace("\\n"," ").replace("\\"","\"");String[] a=s.split("  +");int n=0;for(String q:a){q=q.trim();if(q.length()>2&&(q.contains("°C")||q.contains("Nem")||q.contains("Rüzgar"))&&n<20){list.addView(t(q,16));n++;}}if(n==0)list.addView(t("MGM verisi yükleniyor…",17));}
 protected void onDestroy(){h.removeCallbacksAndMessages(null);if(w!=null)w.destroy();super.onDestroy();}
}