package com.example.pcControl.network;

import com.example.pcControl.console.GeneralLogger;
import com.example.pcControl.data.References;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SocketSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private OutputStream outputStream;
    //public boolean connectedSuccessfully;

    public boolean initialized = false;

    public void startConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (ConnectException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            References.authAccepted = -1;
        }

        if(socket !=null) {
            try {
                outputStream = socket.getOutputStream();
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "windows-1251")); //"windows-1251"
            } catch (SocketException e) {
                // ignore?
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                References.inSocket = in;
                References.outSocket = out;
                References.socket = socket;
                References.socketPort = port;
                References.outputStream = outputStream;
                this.initialized = true;
            }
        }
    }

    public void sendMessage(String msg){
        sendMessage(msg, false);
    }

    @Deprecated
    public void sendMessage(String msg, boolean response) {
        // don't do it
        if(response){
            throw new IllegalArgumentException("\"RESPONSE MODE\" IS DEPRECATED!");
        }

        // Encoding:
        //msg = new String(msg.getBytes(StandardCharsets.UTF_8)); // Cp1251 windows-1251
        //msg = new String(msg.getBytes(Charset.forName("windows-1251"))); // Cp1251 windows-1251

        if(References.outSocket==null) {
            System.out.println("null outSocket");
            return;
        }

        // like flush or writeln
        msg += "\n";

        // Sending:
        // Using bytes array:
        try {
            References.outputStream.write(msg.getBytes(Charset.forName("windows-1251")));
        } catch (SocketException e) {
            //SocketListener.onDisconnect(); // comment if some exception fires here, maybe it'll do it itself. Or just copy code from there here
        }catch (IOException e) {
            e.printStackTrace();
        }
        // Using normal string:
        //References.outSocket.println(msg);

        // Logging:
        if(msg!=null && (!msg.equals("$heartbeat.check") || References.printHeartBeats)) {
            GeneralLogger.log("Android sending: " + msg);
        }
        else{
            References.heartBeatsNumber++;
        }
        /*if(response) {
            String resp = "";
            try {
                resp = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public void stopConnection() {
        try {
            if(in!=null) in.close();
            if(out!=null) out.close();
            if(socket!=null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
