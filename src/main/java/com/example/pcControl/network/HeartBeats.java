package com.example.pcControl.network;

import com.example.pcControl.data.References;

public class HeartBeats {

    public static Runnable loop = new Runnable() {
        @Override
        public void run() {
            if(References.printConnectionDetails) {
                System.out.println("1 (GeneralData.connected): " + References.connected);
                System.out.println("2 (GeneralData.socketSender!=null):" + References.socketSender != null);
                System.out.println("3 (GeneralData.socketSender.initialized):" + (References.socketSender.initialized));
            }
            if(References.connected && References.socketSender!=null && References.socketSender.initialized == true){

                References.socketSender.sendMessage("$heartbeat.check");
                References.handler.postDelayed(loop, 15000);
            }
        }
    };
}
