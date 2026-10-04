package com.edirnehavadurumu.app;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;
    TextView status;
    WebView w;
    Handler h = new Handler();
    Spinner sp;
    int blue = Color.rgb(18, 48, 78);
    String[] districts = {"Edirne Merkez","Keşan","Enez","İpsala","Uzunköprü","Havsa","Meriç","Süloğlu","Lalapaşa"};
    String[] slugs = {"","Kesan","Enez","Ipsala","Uzunkopru","Havsa","Meric","Suloglu","Lalapasa"};

    TextView tv(String s, float size) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(size);
        v.setTextColor(Color.WHITE);
        v.setPadding(18, 12, 18, 12);
        return v;
    }

    TextView card(String s, float size) {
        TextView v = tv(s, size);
        v.setBackgroundColor(Color.rgb(27, 65, 101));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(14, 8, 14, 8);
        v.setLayoutParams(p);
        return v;
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(blue);
        ui();
        collector();
    }

    void ui() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(blue);

        TextView hd = tv("EDİRNE HAVA DURUMU", 23);
        hd.setGravity(Gravity.CENTER);
        hd.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        hd.setPadding(10, 20, 10, 18);
        root.addView(hd);

        sp = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, districts);
        sp.setAdapter(a);
        root.addView(sp, new LinearLayout.LayoutParams(-1, 52));
        sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                load();
            }
        });

        status = tv("MGM verileri alınıyor…", 15);
        status.setGravity(Gravity.CENTER);
        root.addView(status);

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView f = tv("Veri kaynağı: MGM  •  Otomatik güncelleme: 5 dakika\n/ edirnehavadurumu", 12);
        f.setGravity(Gravity.CENTER);
        f.setAlpha(0.75f);
        root.addView(f);
        setContentView(root);
    }

    void collector() {
        w = new WebView(this);
        w.setVisibility(View.GONE);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setDomStorageEnabled(true);
        w.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView v, String u) {
                v.evaluateJavascript("(function(){return document.body?document.body.innerText:''})()", x -> show(x));
            }
        });
        addContentView(w, new ViewGroup.LayoutParams(1, 1));
        h.postDelayed(new Runnable() {
            public void run() {
                load();
                h.postDelayed(this, 300000);
            }
        }, 300000);
    }

    void load() {
        if (w == null) return;
        int pos = sp == null ? 0 : sp.getSelectedItemPosition();
        String url = "https://www.mgm.gov.tr/tahmin/il-ve-ilceler.aspx?il=Edirne";
        if (pos > 0) url += "&ilce=" + slugs[pos];
        status.setText("MGM verisi alınıyor…");
        w.loadUrl(url);
    }

    void show(String raw) {
        String s = raw.replace("\\\\n", " ").replace("\\n", " ");
        s = s.replace("\\"", """);
        final String data = s;
        runOnUiThread(() -> {
            status.setText("MGM verisi alındı • " + android.text.format.DateFormat.format("HH:mm", new Date()));
            list.removeAllViews();

            String selected = districts[sp.getSelectedItemPosition()];
            list.addView(card(selected + " • Güncel Durum", 19));

            String[] lines = data.split("\\n");
            int added = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.length() == 0) continue;
                if (line.contains("Hissedilen:")) {
                    list.addView(card(line.replace("Hissedilen:", "Hissedilen: "), 17));
                    added++;
                } else if (line.matches(".*(Nem \(%\)|Rüzgar \(km/sa\)).*") && added < 5) {
                    list.addView(card(line, 15));
                }
            }

            list.addView(card("5 GÜNLÜK TAHMİN", 19));
            String[] days = {"Cumartesi","Pazar","Pazartesi","Salı","Çarşamba","Perşembe","Cuma"};
            int count = 0;
            for (String line : lines) {
                String q = line.trim();
                for (String d : days) {
                    if (q.startsWith(d) && q.contains("°C")) {
                        list.addView(card(q, 16));
                        count++;
                        break;
                    }
                }
                if (count >= 5) break;
            }

            list.addView(card("SAATLİK TAHMİN", 19));
            list.addView(card("Saatlik veriler MGM sayfasından alınmaktadır. Ayrıntılar için ilgili merkezin tahminleri güncelleniyor.", 14));
        });
    }

    protected void onDestroy() {
        h.removeCallbacksAndMessages(null);
        if (w != null) w.destroy();
        super.onDestroy();
    }
}