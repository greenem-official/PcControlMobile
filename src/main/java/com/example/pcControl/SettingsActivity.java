package com.example.pcControl;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pcControl.data.References;
import com.example.pcControl.tools.SaveData;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText ipEditText;
    private EditText portEditText;
    private EditText pwdEditText;
    private Button saveSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateView();

        /*Button backSettingsBtn = (Button) findViewById(R.id.back_settings_btn);
        backSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                setContentView(R.layout.activity_main);
//                startActivity(startIntent);
            }
        });*/
        System.out.println(References.port);
        if(References.ip!=null && !References.ip.equals("")) {
            ((EditText) findViewById(R.id.addIp_field)).setHint("IP (added)");
        }
        if(References.port!=null && !References.port.equals("")) {
            ((EditText) findViewById(R.id.addPort_field)).setHint("Port (added)");
        }
        if(References.password!=null && !References.password.equals("")) {
            ((EditText) findViewById(R.id.addPwd_field)).setHint("Password (added)");
        }

        saveSettingsBtn = (Button) findViewById(R.id.save_settings_btn);
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println(LoadData.loadBoolean(References.sharedPrefs, MODE_PRIVATE, "alreadySavedIpSettings"));


                ipEditText = (EditText) findViewById(R.id.addIp_field);
                portEditText = (EditText) findViewById(R.id.addPort_field);
                pwdEditText = (EditText) findViewById(R.id.addPwd_field);
                String ip = ipEditText.getText().toString().trim();
                String port = portEditText.getText().toString().trim();
                String pwd = pwdEditText.getText().toString().trim();
//                ipText.getContext().getText().toString();
                /*if(ip.equals("") || port.equals("") || pwd.equals("")){
                    Toast.makeText(getApplicationContext(),"Enter data first!", Toast.LENGTH_SHORT).show();
                    if(ip.equals("")){
                        ipEditText.setError("Enter the IP");
                    }
                    if(port.equals("")){
                        portEditText.setError("Enter the Port");
                    }
                    if(pwd.equals("")){
                        pwdEditText.setError("Enter the Password");
                    }
                }*/
                //else if(!pwd.equals("")) {
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                //}

//                if(pwd.equals("")){
//                    Toast.makeText(getApplicationContext(),"Enter the PASSWORD!", Toast.LENGTH_SHORT).show();
//                }

//                System.out.println("IP: " + ipText.getText().toString());

                if(!ip.equals("")) {
                    References.ip = ip;
                    SaveData.save(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_ip", ip);
                }
                if(!port.equals("")) {
                    References.port = port;
                    SaveData.save(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_port", port);
                }
                if(!pwd.equals("")) {
                    References.password = pwd;
                    SaveData.save(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_password", pwd);
                }

                //SaveData.save(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "alreadySavedIpSettings", true);
            }
        });
    }

    public void updateView(){
        //Toast.makeText(getApplicationContext(), LoadData.loadString(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_ip"), Toast.LENGTH_SHORT).show();

        /*if(LoadData.loadBoolean(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "alreadySavedIpSettings")){
            References.hasStoredIpSettings = true;
        }
        else {
            References.hasStoredIpSettings = false;
        }*/

        /*TextView alreadySavedSettings = findViewById(R.id.alreadySavedSettings_text);
        if(References.hasStoredIpSettings){
            alreadySavedSettings.setVisibility(View.VISIBLE);
//            alreadySavedSettings.setText("Already saved");
        }
        else{
            alreadySavedSettings.setVisibility(View.GONE);
//            alreadySavedSettings.setText("");
        }*/
    }
}