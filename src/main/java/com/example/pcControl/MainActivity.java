package com.example.pcControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pcControl.console.GeneralLogger;
import com.example.pcControl.data.References;
import com.example.pcControl.network.HeartBeats;
import com.example.pcControl.network.SocketListener;
import com.example.pcControl.network.SocketSender;
import com.example.pcControl.tools.LoadData;

public class MainActivity extends AppCompatActivity {
    public static volatile MainActivity instance;
    private boolean alreadyConnected = false;
    private boolean firstLaunch = true;

    public static MainActivity getInstance(){
        if(instance == null){
            instance = new MainActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(firstLaunch){
            loadOnLaunchData();
        }

        Button setSettingsBtn = (Button) findViewById(R.id.settings_btn);
        setSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SettingsActivity.class);
//                setContentView(R.layout.settings_view);
                startActivity(startIntent);
            }
        });

        Button connectBtn = (Button) findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
    }

    public void connect(){
        if (References.handler == null) {
            References.handler = new Handler();
        }
        if(References.ip!=null && References.port!=null && References.password!=null &&
                !References.ip.equals("") && !References.port.equals("") && !References.password.equals("")) {
            if (!alreadyConnected) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Thread theThread = new Thread(connectionFirst);
            theThread.start();
        }
        else{
            Toast.makeText(getApplicationContext(), "Not enough data to connect", Toast.LENGTH_SHORT);
            alreadyConnected = false;
        }
    }

    private Runnable connectionFirst = new Runnable() {
        @Override
        public void run() {
            boolean done = false;
            GeneralLogger.log("connectionFirst");
            if (!alreadyConnected) {
                References.sender = new SocketSender();
                //Freezes the thread                                                                                 <-- not good (there are multiple of these problems)
                References.sender.startConnection(References.ip, Integer.valueOf(References.port));
                //If not successful
                if(!References.sender.initialized)
                {
                    System.out.println("Socket sender IS NOT initialized! (MainActivity)");
                    connectionFirst.run();
                    return;
                }
                else{
                    References.socketListener = new Thread(SocketListener.getInstance());
                    References.socketListener.start();
                    //sender.sendMessage("android is connecting", false);
                    References.sender.sendMessage("$auth.request.password=" + References.password);
                    //sender.sendMessage("ugfasdfg", false);
                    alreadyConnected = true;
                    References.wrongPassword = false;
                    loopConnectionWait.run();
                    //checkPwdGoToConsoleScreen.run();
                }
            }

            //loopConnectionWait.run();

            //References.handler.postDelayed(loopConnectionWait, 100);
        }
    };

    public void loadOnLaunchData(){
        References.ip = LoadData.loadString(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_ip");
        References.port = LoadData.loadString(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_port");
        References.password = LoadData.loadString(getSharedPreferences(References.sharedPrefs, MODE_PRIVATE), "settings_main_password");
        firstLaunch = false;
    }

    private Runnable loopConnectionWait = new Runnable(){
        @Override
        public void run() {
            GeneralLogger.log("loopConnectionWait");
//            System.out.println(wrongPassword);
//            System.out.println(References.connected);
            if(References.wrongPassword) return;
            System.out.println(6);
            if(References.connected) {
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkPwdGoToConsoleScreen.run();
                //References.handler.postDelayed(checkPwdGoToConsoleScreen, 200);
            }
            else {
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loopConnectionWait.run();
                //References.handler.postDelayed(loopConnectionWait, 200);
            }
        }
    };

    private Runnable checkPwdGoToConsoleScreen = new Runnable() {
        @Override
        public void run() {
            GeneralLogger.log("checkPwdGoToConsoleScreen, References.authAccepted = " + References.authAccepted);
            if (References.authAccepted == 1) {
                Intent startIntent = new Intent(getApplicationContext(), ConsoleActivity.class);
//                setContentView(R.layout.settings_view);
                startActivity(startIntent);
                References.connected = true;
                References.wrongPassword = false;
                System.out.println("4 (from main activity)");
                System.out.println(References.sender + "");
                References.sender.sendMessage("$system.files.getlocation.request");
                References.sender.sendMessage("$system.files.getpathseparator.request");
                References.reloadFoldersFilesList();
                References.handler.postDelayed(HeartBeats.loop, References.heartBeatsDelayMillis);
            } else if (References.authAccepted == 0) {
                System.out.println(5);
                References.handler.post((Runnable) () -> Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show());
                alreadyConnected = false;
                References.connected = false;
                References.wrongPassword = true;
            } else {
                References.handler.post((Runnable) () -> Toast.makeText(getApplicationContext(), "General error", Toast.LENGTH_SHORT).show());
                alreadyConnected = false;
                References.connected = false;
            }
        }
    };
}