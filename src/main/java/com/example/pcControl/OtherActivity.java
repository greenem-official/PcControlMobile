package com.example.pcControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OtherActivity extends AppCompatActivity {
    private Button serversButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        serversButton = findViewById(R.id.otherViewServers_button);

        serversButton.setOnClickListener((View.OnClickListener) view -> {
            Intent startIntent = new Intent(getApplicationContext(), ServersActivity.class);
            startActivity(startIntent);
        });
    }
}