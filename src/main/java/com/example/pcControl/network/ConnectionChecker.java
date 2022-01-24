package com.example.pcControl.network;

import com.example.pcControl.data.References;

public class ConnectionChecker implements Runnable {
    private static volatile ConnectionChecker instance;

    public static ConnectionChecker getInstance(){
        if(instance == null){
            instance = new ConnectionChecker();
        }
        return instance;
    }
    //boolean found = false;
    //private Handler handler;

    @Override
    public void run() {
        boolean loop = true;
        while (loop) {
            System.out.println("Connection Checker loop");
            References.socketSender.stopConnection();
            References.socketSender = new SocketSender();
            References.socketSender.startConnection(References.ip, Integer.valueOf(References.port));
            if (!References.socketSender.initialized) {
                System.out.println("Socket sender IS NOT initialized! (ConnectionChecker)");
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
//        while(true){
//            if(!GeneralData.disconnected){
            System.out.println("started connection");
            SocketSender sender = References.socketSender;
            //sender.startConnection("IP", Integer.valueOf("12345"));                                    //localhost to ip
            //SocketListener listener = SocketListener.getInstance();
            References.socketListener = new Thread(SocketListener.getInstance());
            References.socketListener.start();
            //sender.sendMessage("android is connecting", false);
            sender.sendMessage("$auth.request.password=" + References.password, false);
            //sender.sendMessage("ugfasdfg", false);

//                if (GeneralData.handler == null) {
//                    GeneralData.handler = new Handler();
//                }

            References.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("GeneralData.authAccepted = " + References.authAccepted);
                    if (References.authAccepted == 1) {
                        References.disconnected = false;
                        References.connected = true;
                        References.handler.postDelayed(HeartBeats.loop, 20000);
                        System.out.println("Exiting Connection Checker");
                        return;
                    } else if (References.authAccepted == 0) {
                        System.out.println("Could not reconnect, Wrong password");
                        System.out.println("Exiting Connection Checker");
                        return;
                    } else {
                        System.out.println("Could not reconnect, General error");
                        System.out.println("Exiting Connection Checker");
                        return;
                    }
                }
            }, 200);
            if(References.printConnectionDetails) {
                System.out.println("reconnected");
                System.out.println("NEW 1 (GeneralData.connected): " + References.connected);
                System.out.println("NEW 2 (GeneralData.socketSender!=null):" + References.socketSender != null);
                System.out.println("NEW 3 (GeneralData.socketSender.initialized):" + (References.socketSender.initialized));
            }
            References.disconnected = false;
//                break;
//            }
//        }
//            try {
//                Thread.currentThread().sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
