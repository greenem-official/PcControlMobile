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
        if(References.currentlyConnectingBusy){
            System.out.println("currentlyConnectingBusy");
            return;
        }

        References.currentlyConnectingBusy = true;

        if (References.handler == null) {
            References.handler = new Handler();
        }
        GeneralLogger.log("debug 1");
        if(References.ip!=null && References.port!=null && References.password!=null &&
                !References.ip.equals("") && !References.port.equals("") && !References.password.equals("")) {
            GeneralLogger.log("debug 2");
            if (!References.alreadyConnectedT && !References.alreadySetPolicy) {
                GeneralLogger.log("debug 3");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                References.alreadySetPolicy = true;
            }
            Thread theThread = new Thread(connectionFirst);
            theThread.start();
        }
        else{
            Toast.makeText(getApplicationContext(), "Not enough data to connect", Toast.LENGTH_SHORT);
            References.alreadyConnectedT = false;
        }
    }

    private Runnable connectionFirst = new Runnable() {
        @Override
        public void run() {
            boolean done = false;
            GeneralLogger.log("connectionFirst");
            if (!References.alreadyConnectedT) {
                References.sender = new SocketSender();
                //Freezes the thread                                                                                 <-- not good (there are multiple of these problems)
                GeneralLogger.log("connectionFirst startConnection");
                References.sender.startConnection(References.ip, Integer.valueOf(References.port));
                GeneralLogger.log("connectionFirst startedConnection");
                //If not successful
                if(!References.sender.initialized)
                {
                    System.out.println("Socket sender WAS NOT initialized! (MainActivity)");
                    connectionFirst.run();
                }
                else{
                    References.socketListener = new Thread(SocketListener.getInstance());
                    References.socketListener.start();
                    //sender.sendMessage("android is connecting", false);
                    References.sender.sendMessage("$auth.request.password=" + References.password);
                    //sender.sendMessage("ugfasdfg", false);
                    References.alreadyConnectedT = true; // WHY?
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
            //TODO: sdfg
            if(References.wrongPassword) {
                References.handler.post((Runnable) () -> Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show());
                GeneralLogger.log("Interrupting thread (loopConnectionWait)");
                References.currentlyConnectingBusy = false;
                References.alreadyConnectedT = false;
                Thread.currentThread().interrupt();
                return;
            }
            if(References.connected) {
                GeneralLogger.log("loopConnectionWait References.connected");
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkPwdGoToConsoleScreen.run();
                //References.handler.postDelayed(checkPwdGoToConsoleScreen, 200);
            }
            else {
                GeneralLogger.log("loopConnectionWait NOT References.connected");

                if(References.wrongPassword) {
                    References.currentlyConnectingBusy = false;
                    GeneralLogger.log("Interrupting thread (loopConnectionWait more like end)");
                    Thread.currentThread().interrupt();
                    return;
                }

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
                References.currentlyConnectingBusy = false;
            } else if (References.authAccepted == 0) {
                System.out.println(5);
                References.handler.post((Runnable) () -> Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show());
                References.alreadyConnectedT = false;
                References.connected = false;
                References.wrongPassword = true;
                References.currentlyConnectingBusy = false;
                References.alreadyConnectedT = false;
                GeneralLogger.log("Interrupting thread (checkPwdGoToConsoleScreen)");
                Thread.currentThread().interrupt();
                return;
            } else {
                References.handler.post((Runnable) () -> Toast.makeText(getApplicationContext(), "General error", Toast.LENGTH_SHORT).show());
                References.alreadyConnectedT = false;
                References.connected = false;
            }
        }
    };
}