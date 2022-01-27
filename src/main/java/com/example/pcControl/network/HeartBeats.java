package com.example.pcControl.network;

import com.example.pcControl.data.References;

public class HeartBeats {

    public static Runnable loop = new Runnable() {
        @Override
        public void run() {
            if(References.printConnectionDetails) {
                System.out.println("Heartbeat:");
                System.out.println("HeartBeats 1 (References.connected): " + References.connected);
                System.out.println("HeartBeats 2 (References.socketSender!=null): " + (References.sender != null));
                System.out.println("HeartBeats 3 (References.socketSender.initialized): " + (References.sender.initialized));
            }
            if(References.connected && References.sender!=null && References.sender.initialized){
                References.sender.sendMessage("$heartbeat.check");
                References.handler.postDelayed(loop, References.heartBeatsDelayMillis);
            }
        }
    };
}
