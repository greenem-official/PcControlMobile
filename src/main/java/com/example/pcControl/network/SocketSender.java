package com.example.pcControl.network;

import com.example.pcControl.data.References;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class SocketSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    //public boolean connectedSuccessfully;

    public boolean initialized = false;

    //    @Deprecated
    public void startConnection(String ip, int port) { //DEPRECATED
        try {
            socket = new Socket(ip, port);
        }
        catch (ConnectException e) {
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            References.authAccepted = -1;
        }
        //System.out.println(socket);
        if(socket !=null) {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "windows-1251"));
            }
            catch (SocketException e) {

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                References.inSocket = in;
                References.outSocket = out;
                References.socket = socket;
                References.socketPort = port;
                this.initialized = true;
            }
        }
    }

    public void sendMessage(String msg){
        sendMessage(msg, false);
    }

    public void sendMessage(String msg, boolean response) {
        if(References.outSocket==null) {
            System.out.println("null outSocket");
            return;
        }
        References.outSocket.println(msg);
        if(msg!=null && (!msg.equals("$heartbeat.check")) || References.printHeartBeats) {
            System.out.println("Android sending: " + msg);
        }
        else{
            References.heartBeatsNumber++;
        }
        if(response) {
            String resp = "";
            try {
                resp = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
