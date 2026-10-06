package com.edirnehavadurumu.app;

import android.app.*;
import android.os.*;
import android.Manifest;
import android.content.pm.PackageManager;
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
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
        }
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
                v.evaluateJavascript("(function(){var rows=[];document.querySelectorAll('table tr').forEach(function(tr){var a=[];tr.querySelectorAll('th,td').forEach(function(td){var s=(td.innerText||td.textContent||'').replace(/\\s+/g,' ').trim();if(s)a.push(s);});if(a.length)rows.push(a.join(' | '));});return rows.length?rows.join('\\n'):(document.body?document.body.innerText:'');})()", x -> show(x));
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
        String data = raw;
        try {
            Object decoded = new org.json.JSONTokener(raw).nextValue();
            if (decoded instanceof String) data = (String) decoded;
        } catch (Exception ignored) {}
        data = data.replace("\\\\n", "\\n").replace("\\\\t", "\\t").replace("\\\\r", "\\r");
        final String parsedData = data;

        runOnUiThread(() -> {
            status.setText("MGM verisi alındı • " + android.text.format.DateFormat.format("HH:mm", new Date()));
            list.removeAllViews();
            String selected = districts[sp.getSelectedItemPosition()];
            list.addView(card(selected + " • Güncel Durum", 19));

            String[] lines = parsedData.split("\\n");
            int shown = 0;
            boolean headerShown = false;
            for (String line : lines) {
                String q = line.replace("\\t", " ").trim();
                if (q.length() == 0) continue;
                if (!headerShown && (q.contains("Saat") || q.contains("Beklenen Hadise"))) {
                    list.addView(card(q, 14));
                    headerShown = true;
                    continue;
                }
                if (q.matches(".*\\d+.*°C.*") || q.contains("Hissedilen") || q.contains("Nem (%)")) {
                    list.addView(card(q, 15));
                    shown++;
                    if (shown >= 8) break;
                }
            }

            list.addView(card("5 GÜNLÜK TAHMİN", 19));
            int daily = 0;
            for (String line : lines) {
                String q = line.replace("\\t", " ").trim();
                if (q.length() == 0) continue;
                if (q.contains("°C") && (q.contains("|") || q.contains("En Düşük") || q.contains("En Yüksek"))) {
                    list.addView(card(q, 15));
                    daily++;
                    if (daily >= 5) break;
                }
            }
            if (shown == 0) {
                list.addView(card("MGM sayfasından veri satırları alınamadı. Sayfa yapısı değişmiş olabilir.", 14));
            }

            list.addView(card("SAATLİK TAHMİN", 19));
            list.addView(card("MGM verileri uygulama içinde gösteriliyor. Saatlik tahminler otomatik güncellenir.", 14));
        });
    }
    protected void onDestroy() {
        h.removeCallbacksAndMessages(null);
        if (w != null) w.destroy();
        super.onDestroy();
    }
}