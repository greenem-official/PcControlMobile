package com.example.pcControl.tools;

import com.example.pcControl.data.References;

import java.io.IOException;

public class Stuff {
    public static void doDisconnectWithoutReconnect(){
        System.out.println("doDisconnectWithoutReconnect");
        References.firstConnectionT = false;
        References.alreadyConnectedT = false;
        References.socketListener.interrupt();
        try {
            References.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        References.disconnectedAlreadyExtraInfo = true;
        References.connected = false;
        //References.socketListener.interrupt(); // NPO
        References.socketListener = null;
    }
}
