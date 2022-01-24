package com.example.pcControl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ServersActivity extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        linearLayout = findViewById(R.id.serversView_linearLayout);

        TextView textView1 = new TextView(getApplicationContext());
        textView1.setText("\"1\" variant");
        textView1.setTextSize(28);

        TextView textView2 = new TextView(getApplicationContext());
        textView2.setText("\"2\" variant");
        textView2.setTextSize(28);

        linearLayout.addView(textView1);
        linearLayout.addView(textView2);
    }
}