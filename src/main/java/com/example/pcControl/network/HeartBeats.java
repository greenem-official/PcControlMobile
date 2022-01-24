package com.example.pcControl.network;

import com.example.pcControl.data.References;

public class HeartBeats {

    public static Runnable loop = new Runnable() {
        @Override
        public void run() {
            if(References.printConnectionDetails) {
                System.out.println("Heartbeat:");
                System.out.println("HeartBeats 1 (References.connected): " + References.connected);
                System.out.println("HeartBeats 2 (References.socketSender!=null): " + References.socketSender != null);
                System.out.println("HeartBeats 3 (References.socketSender.initialized): " + (References.socketSender.initialized));
            }
            if(References.connected && References.socketSender!=null && References.socketSender.initialized == true){

                References.socketSender.sendMessage("$heartbeat.check");
                References.handler.postDelayed(loop, 15000);
            }
        }
    };
}
