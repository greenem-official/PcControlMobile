package com.example.pcControl.network;

import java.io.IOException;
import java.net.SocketException;

import com.example.pcControl.data.References;

public class SocketListener implements Runnable {


    private static volatile SocketListener INSTANCE;

    public static SocketListener getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SocketListener();
        }
        return INSTANCE;
    }


    @Override
    public void run() {
        try {
            String inputLine = "";
            String displayLine = "";
            while (inputLine != null && !References.socket.isClosed()) {
                try {
                    inputLine = References.inSocket.readLine();
                    //inputLine = new String(inputLine.getBytes("UTF-8"), "windows-1251"); //Charset.forName("windows-1252")    StandardCharsets.UTF_8
                }
                catch (SocketException e){
                    if(!References.socket.isClosed()) {
                        System.out.println("Socket exception debug: " + e);
                        if (References.sender.initialized) { // or .connected?
                            onDisconnect();
                        }
                    }
                    break;
                }
                catch (IOException e){
                    if(!References.socket.isClosed()) {
                        System.out.println("IOException debug: " + e);
                        if (References.sender.initialized) {
                            onDisconnect();
                        }
                    }
                    break;
                }
                catch (NullPointerException e){
                    if(!References.socket.isClosed()) {
                        System.out.println("NullPointerException debug: " + e);
                        onDisconnect();
                    }
                    break;
                }
                finally {
                    References.connected = true;
                    References.disconnectedAlreadyExtraInfo = false;
                    displayLine = inputLine;
                }
                if(inputLine != null){
//					if (inputLine.equalsIgnoreCase("servermanager stop serversocket")) {
//						References.getInstance().outSocket.println("Socket Closed");
//						break;
//					}
//					References.outSocket.println("message back to PX");                                                //useful
                    System.out.println("Android got: " + inputLine + "\n");
                    if(inputLine.startsWith("$")) {
                        String[] args = inputLine.substring(1).split("\\.");
                        int len = args.length;
                        if(len>0) {
//                            System.out.println(args[0]);
                            if(args[0].equals("auth")) {
                                if(len>1) {
                                    if(args[1].equals("result")) {
                                        if(len>2) {
                                            if(args[2].equals("accepted")) {
                                                References.authAccepted = 1;
                                                References.wrongPassword = false;
                                                System.out.println("justReconnected = " + References.justReconnectedT + "; firstConnection = " + References.firstConnectionT);
                                                //if(References.justReconnected || References.firstConnection) {
                                                displayLine = "Connected successfully\n ";
                                                //}
                                                //else {
                                                //    displayLine = "";
                                                //}
                                                References.justReconnectedT = false;
                                                //displayLine = null;
                                                if(!References.currentFolder.equals("")) References.sender.sendMessage("$system.files.changelocation.request.new=" + References.currentFolder);
                                            }
                                            else if(args[2].equals("denied")) {
                                                References.authAccepted = 0;
                                                References.wrongPassword = true;
                                                References.alreadyConnectedT = false;
                                                //References.handler.post((Runnable) () -> Toast.makeText( getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show());
                                                displayLine = "Unable to connect: wrong password";
                                            }
                                        }
                                    }
                                    if(args[1].equals("alreadyConnected")) {
                                        displayLine = null;
                                    }
                                }
                            }
                            if(args[0].equals("heartbeat")) {
                                if (len > 1) {
                                    if (args[1].equals("timeout")) {
                                        displayLine = "Got disconnected from the server, error code: 0x001"; // + "\n"
                                        onDisconnect(); // not tested
                                    }
                                }
                            }
                            else if(args[0].equals("rscmessage")) {
                                displayLine = "Message from the PC: " + inputLine.substring(12) + "\n";
                            }
                            else if(args[0].equals("servermessage")) {
                                if(len>1){
                                    if(args[1].startsWith("text=")){
                                        displayLine = inputLine.substring(20) + "\n";
                                    }
                                }
                            }
                            else if(args[0].equals("rsccommand")) {
                                if(len>1){
                                    if(args[1].equals("unknown")) {
                                        displayLine = "Unknown command!" + "\n"; // idk if this is used because there is anotehr way by message
                                    }
                                }
                            }
                            else if(args[0].equals("system")) {
                                if(len>1){
                                    if(args[1].equals("getinfo")) {
                                        if(len>2){
                                            if(args[2].equals("tasklist")) {
                                                if(len>3){
                                                    if(args[3].equals("result")) {
                                                        if(len>4){
                                                            if(args[4].startsWith("text=")){
                                                                String result = "The list of currently running processes:\n" + inputLine.substring(37);
                                                                displayLine = result + "\n"; //result.replaceAll("&l&ine&", "\n");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(args[1].equals("management")) {
                                        if(len>2){
                                            if(args[2].equals("shutdown")) {
                                                if(len>3){
                                                    if(args[3].equals("usual")) {
                                                        if(len>4){
                                                            if(args[4].equals("accepted")) {
                                                                displayLine = "Shutting down the computer..." + "\n"; //result.replaceAll("&l&ine&", "\n");
                                                            }
                                                            else if(args[4].equals("denied")) {
                                                                displayLine = "Can't shut down the computer, the feature is disabled by the user" + "\n";
                                                            }
                                                        }
                                                    }
                                                    if(args[3].equals("restart")) {
                                                        if(len>4){
                                                            if(args[4].equals("accepted")) {
                                                                displayLine = "Restarting the computer..." + "\n"; //result.replaceAll("&l&ine&", "\n");
                                                            }
                                                            else if(args[4].equals("denied")) {
                                                                displayLine = "Can't restart the computer, the feature is disabled by the user" + "\n";
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if(args[1].equals("execution")) {
                                        if(len>2) {
                                            if (args[2].startsWith("output=")) {
                                                displayLine = "[OUTPUT] " + inputLine.substring(25);//+"&no&l&ne&";
                                            }
                                            else if (args[2].equals("stopped")) {
                                                if(len>3) {
                                                    if (args[3].equals("justend")) {
                                                        displayLine = "Process stopped." + "\n";
                                                        References.processRunning = false;
                                                    }
                                                }
                                                References.showSendInputToExecBtn = false;
                                            }
                                        }
                                    }
                                    else if(args[1].equals("files")) {
                                        if(len>2) {
                                            //System.out.println(args[2]);
                                            if (args[2].equals("getpathseparator")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result")) {
                                                        References.systemSeparator = inputLine.substring(52);
                                                        displayLine = "";
                                                    }
                                                }
                                            }
                                            if (args[2].equals("getlocation")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result")) {
                                                        if (len > 4) {
                                                            if (args[4].startsWith("location=")) {
                                                                String text = inputLine.substring(42);
                                                                System.out.println("Got the location: " + text);
                                                                References.currentFolder = text;
                                                                displayLine = "";
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (args[2].equals("fileslist")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result") || args[3].equals("silentresult")) {
                                                        if (len > 4) {
                                                            if (inputLine.length() > 36) {
                                                                String text;
                                                                if (args[3].equals("result")) {
                                                                    text = inputLine.substring(36);
                                                                } else {
                                                                    text = inputLine.substring(42);
                                                                }

                                                                System.out.println("Got files list...");
                                                                String[] files = text.split("&&nex&t&");
                                                                References.filesList = files;
                                                                if (args[3].equals("result")) {
                                                                    displayLine = "The files list:" + "\n";
                                                                    for (int i = 0; i < files.length; i++) {
                                                                        displayLine += files[i];
                                                                        if (i < files.length - 1) {
                                                                            displayLine += "&l&ine&";
                                                                        }
                                                                    }
                                                                    displayLine += "\n";
                                                                    //displayLine += " ";
                                                                } else {
                                                                    displayLine = "";
                                                                }
                                                            } else {
                                                                if (args[3].equals("result")) {
                                                                    displayLine = "There are no files!" + "\n";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (args[2].equals("folderslist")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result") || args[3].equals("silentresult")) {
                                                        if (len > 4) {
                                                            if (inputLine.length() > 38) {
                                                                String text;
                                                                if (args[3].equals("result")) {
                                                                    text = inputLine.substring(38);
                                                                } else {
                                                                    text = inputLine.substring(44);
                                                                }
                                                                System.out.println("Got folders list...");
                                                                String[] folders = text.split("&&nex&t&");
                                                                References.foldersList = folders;
                                                                if (args[3].equals("result")) {
                                                                    displayLine = "The folders list:" + "\n";
                                                                    for (int i = 0; i < folders.length; i++) {
                                                                        displayLine += folders[i];
                                                                        if (i < folders.length - 1) {
                                                                            displayLine += "&l&ine&";
                                                                        }
                                                                    }
                                                                    displayLine += "\n";
                                                                    //displayLine += " ";
                                                                } else {
                                                                    displayLine = "";
                                                                }
                                                            } else {
                                                                if (args[3].equals("result")) {
                                                                    displayLine = "There are no folders!" + "\n";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (args[2].equals("nonfolderslist")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result") || args[3].equals("silentresult")) {
                                                        if (len > 4) {
                                                            if (inputLine.length() > 41) {
                                                                String text;
                                                                if (args[3].equals("result")) {
                                                                    text = inputLine.substring(41);
                                                                } else {
                                                                    text = inputLine.substring(47);
                                                                }
                                                                System.out.println("Got non-folders list...");
                                                                String[] files = text.split("&&nex&t&");
                                                                References.nonFoldersList = files;
                                                                if (args[3].equals("result")) {
                                                                    displayLine = "The non-folders list:" + "&l&ine&";
                                                                    for (int i = 0; i < files.length; i++) {
                                                                        displayLine += files[i];
                                                                        if (i < files.length - 1) {
                                                                            displayLine += "&l&ine&";
                                                                        }
                                                                    }
                                                                    displayLine += "\n";

                                                                    //displayLine += " ";
                                                                } else {
                                                                    displayLine = "";
                                                                }
                                                            } else {
                                                                if (args[3].equals("result")) {
                                                                    displayLine = "There are no non-folder files!" + "\n";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (args[2].equals("changelocation")) {
                                                //System.out.println("changelocation");
                                                if (len > 3) {
                                                    if (args[3].equals("result")) {
                                                        if (len > 4) {
                                                            if (args[4].equals("accepted")) {
                                                                displayLine = "";
                                                                if (len > 5) {
                                                                    if (args[5].startsWith("path=")) {
                                                                        String path = inputLine.substring(50);
                                                                        String[] parts = path.split(getSafeSeparator());
                                                                        String folder = parts[parts.length - 1];
                                                                        //System.out.println("path=");
                                                                        if (parts.length == 1 && path.contains(References.systemSeparator)) {
                                                                            folder += References.systemSeparator;
                                                                        }
//                                                                        System.out.println(References.currentFolder);
//                                                                        System.out.println(path);
                                                                        if(!References.currentFolder.equals(path)) References.lastConsoleOutput += "Entered directory \"" + folder + "\"\n\n"; //"Entered folder \"" + folder + "\"";
                                                                        References.currentFolder = path;
                                                                        References.reloadFoldersFilesList();
                                                                    }
                                                                }
                                                            }
                                                            if (args[4].equals("denied") || args[4].equals("deniednotfolder")) {
                                                                if (len > 5) {
                                                                    if (args[5].startsWith("old=")) {
                                                                        References.currentFolder = inputLine.substring(47);
                                                                        if(args[4].equals("denied")) {
                                                                            displayLine = "Such folder doesn't exist.&l&ine&"; // + "\n";
                                                                        } else {
                                                                            displayLine = "You can't enter that location because it's not a folder.&l&ine&"; // + "\n";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (args[2].equals("executefile")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result")) {
                                                        if (len > 4) {
                                                            if (args[4].equals("notfound")) {
                                                                displayLine = "File not found!" + "\n";
                                                                References.showSendInputToExecBtn = false;
                                                                References.processRunning = false;
                                                            }
                                                            if (args[4].equals("success")) {
                                                                displayLine = "Starting the file..." + "\n";
                                                                References.showSendInputToExecBtn = true;
                                                                References.processRunning = true;
                                                                //later
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (args[2].equals("dirinfo")) {
                                                if (len > 3) {
                                                    if (args[3].equals("result")) {
                                                        if (len > 4) {
                                                            if (args[4].startsWith("info=")) {
                                                                String text = inputLine.substring(34);
                                                                System.out.println("Got directory info");
                                                                displayLine = text + "\n";
                                                            }
                                                            else if (args[4].startsWith("sizeinfo=")) {
                                                                String text = inputLine.substring(38);
                                                                System.out.println("Got directory size");
                                                                String[] partOfCurrentPath = References.currentFolder.replace("\\", "/").split("/");
                                                                String currentFolder = partOfCurrentPath[partOfCurrentPath.length-1];
                                                                if(currentFolder.endsWith(":")){
                                                                    currentFolder += References.systemSeparator;
                                                                }
                                                                displayLine = "The folder \"" + partOfCurrentPath[partOfCurrentPath.length-1] + "\" is " + text;
                                                                if(text.startsWith(">")) {
                                                                    displayLine += " (deep scan takes too much time)" + "\n";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            References.sender.sendMessage("$rcsmessage.error.general");
                        }
                    }

//                    String already = References.lastConsoleOutput;
//                    String[] rows = already.split("\n");
//                    String newStr = "";
//                    if(rows.length > 40){
//                        for (int i = rows.length-40; i<rows.length; i++){
//                            newStr += rows[i];
//                        }
//                        newStr += inputLine + "\n";
//                    }

                    if (displayLine != null) {
                        if (References.authAccepted == 1) {
                            boolean needToHaveNextLineAfterThis = true;
                            if(displayLine.endsWith("&no&l&ne&")){
                                needToHaveNextLineAfterThis = false;
                                displayLine = displayLine.replaceAll("&no&l&ne&", "");
                            }
                            if(!displayLine.equals("")){
                                displayLine += "&l&ine& ";
                            }
                            displayLine = displayLine.trim();
                            String testStr = "";
                            if(displayLine.contains("&l&ine&")){
                                String[] list = displayLine.split("&l&ine&");
                                for (int i = 0; i < list.length; i++){
                                    References.lastConsoleOutput += list[i];
                                    testStr += list[i];
                                    if(i<list.length-1 || needToHaveNextLineAfterThis) {
                                        References.lastConsoleOutput += "\n";
                                        testStr += "\n";
                                    }
                                }
                                //System.out.println(testStr); //commented debug idk what's this but cleaning
                            }
                            else { //never
                                if(!displayLine.equals("")) {
                                    References.lastConsoleOutput += displayLine + "\n";
                                }
                            }
                        }
                    }
                }
//				if(References.getInstance().clientSocket.isClosed()) {
//					System.out.println("closed");
//				}
            }

            References.socket.close();

            if(References.authAccepted==1 && !References.disconnectedAlreadyExtraInfo) {
                System.out.println("Disconnect");
                //onDisconnect();
            }
            //System.out.println(References.authAccepted);
            if(References.authAccepted==1 && References.alreadyConnectedT) {
                References.lastConsoleOutput += "Connection lost, reconnecting...\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void onDisconnect() {
        System.out.println("Something called onDisconnect");
        References.disconnectedAlreadyExtraInfo = true;
        References.connected = false;
        //References.socketListener.interrupt(); // NPO
        References.socketListener = null;
        References.socketReconnecter = new Thread(ConnectionChecker.getInstance());
        References.socketReconnecter.start();
    }

    public static String getSafeSeparator(){
        if(References.systemSeparator.equals("/")) {
            return References.systemSeparator;
        }
        else if(References.systemSeparator.equals("\\")) {
            return References.systemSeparator + References.systemSeparator;
        }
        return null;
    }
}