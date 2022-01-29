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

    @Override
    public void run() {
        /*boolean loop = true;
        while (loop) {*/
            System.out.println("Connection Checker loop");
            References.sender.stopConnection();
            References.sender = new SocketSender();
            References.connected = false;
            References.disconnectedAlreadyExtraInfo = true;
            References.sender.startConnection(References.ip, Integer.valueOf(References.port));

            // It DOES connect first try, because the entire thread was frozen                                  <--    (needs to be fixed)

            /*System.out.println("Initialized first try: " + References.socketSender.initialized);
            if (!References.socketSender.initialized) {
                System.out.println("Socket sender IS NOT initialized! (ConnectionChecker)");
                //Waiting a threshold
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }*/

            System.out.println("Started connection again");
            References.socketListener = new Thread(SocketListener.getInstance());
            References.socketListener.start();
            System.out.println("Entering password...");
            References.sender.sendMessage("$auth.request.password=" + References.password);
            References.handler.postDelayed(() -> {
                System.out.println("References.authAccepted = " + References.authAccepted);
                if (References.authAccepted == 1) {
                    References.justReconnectedT = true;
                    References.firstConnectionT = false;
                    System.out.println("justReconnected");
                    References.disconnectedAlreadyExtraInfo = false;
                    References.connected = true;
                    References.handler.postDelayed(HeartBeats.loop, 20000);
                    System.out.println("Exiting Connection Checker");
                    References.sender.sendMessage("$system.files.getlocation.request");
                    References.sender.sendMessage("$system.files.getpathseparator.request");
                    References.changedPasswordAfterConnecting = false;
                    References.reloadFoldersFilesList();
                    return;
                } else if (References.authAccepted == 0) {
                    System.out.println("Could not reconnect, Wrong password");
                    System.out.println("Exiting Connection Checker");
                    return;
                } else if (References.authAccepted == -1) {
                    System.out.println("Still didn't get reply");
                    if(References.printConnectionDetails) {
                        System.out.println("reconnected");
                        System.out.println("ConnectionChecker 1 (References.connected): " + References.connected);
                        System.out.println("ConnectionChecker 2 (References.socketSender!=null): " + References.sender != null);
                        System.out.println("ConnectionChecker 3 (References.socketSender.initialized): " + (References.sender.initialized));
                    }
                    References.handler.postDelayed(this, 200);
                } else {
                    System.out.println("Could not reconnect, General error, code = " + References.authAccepted);
                    System.out.println("Exiting Connection Checker");
                    return;
                }
            }, 200);

            //References.disconnectedAlreadyExtraInfo = false;
//      }
    }
}
