package com.example.pcControl.data;

import android.os.Handler;

import com.example.pcControl.network.SocketSender;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class References {
    public static String ip;
    public static String port;
    public static String password;
    public static boolean hasStoredIpSettings = false;
    public static String sharedPrefs = "sharedPrefs";

    public static SocketSender sender;
    public static Thread socketListener;
    public static Thread socketReconnecter;

    public static BufferedReader inSocket;
    public static PrintWriter outSocket;
    public static Socket socket;
    public static int socketPort;
    public static OutputStream outputStream;

    public static String lastConsoleOutput = "";
    public static int authAccepted = -1;

    public static boolean connected = false;
    public static boolean disconnectedAlreadyExtraInfo = false;
    public static boolean wrongPassword = false;
    public static boolean justReconnectedT = false;
    public static boolean firstConnectionT = true;
    public static boolean currentlyConnectingBusy = false;

    public static Handler handler;

    public static int heartBeatsNumber = 0;
    public static boolean printHeartBeats = true;
    public static int maxOutputLines = 250;

    public static int heartBeatsDelayMillis = 10000;

    public static String currentFolder = "";
    public static String[] filesList = null;
    public static String[] foldersList = null;
    public static String[] nonFoldersList = null;

    //public static String badCodingLastFolderName = null;

    public static String systemSeparator = "/";

    public static void reloadFoldersFilesList(){
        References.sender.sendMessage("$system.files.fileslist.request.silent=true");
        References.sender.sendMessage("$system.files.folderslist.request.silent=true");
        References.sender.sendMessage("$system.files.nonfolderslist.request.silent=true");
    }

    public static boolean processRunning = false;

    public static boolean showSendInputToExecBtn = false;

    public static boolean alreadyConnectedT = false;
    public static boolean alreadySetPolicy = false;

    public static boolean changedPasswordAfterConnecting = false;

    //debugSettings
    public static boolean printConnectionDetails = true;
    public static boolean printScreenDeltaMoveNumber = false;
}
