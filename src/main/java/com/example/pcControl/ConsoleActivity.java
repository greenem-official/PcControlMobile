package com.example.pcControl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pcControl.data.References;
import com.example.pcControl.dialogs.ExecuteDialog;
import com.example.pcControl.dialogs.FilesDialog;

import java.util.Locale;

public class ConsoleActivity extends AppCompatActivity implements FilesDialog.FilesDialogListener, ExecuteDialog.ExecuteDialogListener {

    private TextView cmdText;
    private static EditText cmdInput;
    private LinearLayout cmdVerticalLayoutScrollView;
    private ScrollView cmdScrollView;
    private Button cmdSendButton;
    private ConstraintLayout layout;
    //private Button rowButtonExtraMcCommand;
    //private Button rowButtonExtraMcMessage;
    private Button rowButtonExtraShutdown;
    private Button rowButtonExtraTaskList;
    //private Button rowButtonExtraSpecialSymbol;
    private Button rowButtonExtraCtrl;
    private Button rowButtonExtraExecInput;
    private Button otherButton;
    private ImageButton scrollDownButton;
    private TextView connectionLostText;
    private Button cmdClearInputButton;
    private Button fodersMenuOkButton;

    private Button rowButtonExtraSpecialSymbols;
    private Button rowButtonExtraSystem;
    private Button rowButtonExtraFiles;

    private ConstraintLayout foldersMenuLayout;

    private static ConstraintLayout consoleLayout;

    private Thread printer;
    private int exit = 0;
    private boolean privacy1Done = false;
    //private Handler handler;
    //private Long time;
//    private Long lastTimeKeyboardOpen = -1L;
//    private Long lastTime2Speed = -1L;

    private float consoleClickTypeX1 = -1;
    private float consoleClickTypeY1 = -1;
    private long consoleClickTypeT1 = -1;
    private float consoleClickTypeX2 = -1;
    private float consoleClickTypeY2 = -1;
    private long consoleClickTypeT2 = -1;

    private boolean isRowButtonExtraMcCommandClicked = false;
    private boolean isRowButtonExtraMcMessageClicked = false;
    private boolean isRowButtonExtraCtrlClicked = false;
    private boolean isRowButtonExtraExecInputClicked = false;

    private boolean autoscroll = true;
    private boolean keyboardOpen = false;

    private float fadingScrollButtonToAlphaValue = 0f;
    private float fadingScrollButtonToAlphaAmplifier = 0.6f;

    private float fadingClearInputButtonToAlphaValue = 0f;
    private float fadingClearInputToAlphaAmplifier = 0.6f;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        //setTheme(R.style.Theme_Design_Light_NoActionBar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        //closeKeyboard();

        cmdText = findViewById(R.id.cmdOutputTextView);
        cmdInput = findViewById(R.id.cmdInput);
        cmdScrollView = findViewById(R.id.cmdScrollView);
        cmdVerticalLayoutScrollView = findViewById(R.id.cmdScroll_linearLayoutVertical);
        cmdSendButton = findViewById(R.id.cmdSend_btn);
        layout = findViewById(R.id.consoleLayout);
        otherButton = findViewById(R.id.cmdExtraButtonOther);
        connectionLostText = findViewById(R.id.cmd_connectionLost_text);
        rowButtonExtraTaskList = findViewById(R.id.cmdExtraButtonTaskList);
        rowButtonExtraShutdown = findViewById(R.id.cmdExtraButtonShutdown);
        rowButtonExtraSpecialSymbols = findViewById(R.id.cmdExtraButtonSpecialSymbols);
        rowButtonExtraCtrl = findViewById(R.id.cmdExtraButtonCtrl);
        scrollDownButton = findViewById(R.id.cmdScrollDownBtn);
        cmdClearInputButton = findViewById(R.id.cmdClearInput_btn);
        rowButtonExtraSystem = findViewById(R.id.cmdExtraButtonSystem);
        rowButtonExtraFiles = findViewById(R.id.cmdExtraButtonFiles);
        rowButtonExtraExecInput = findViewById(R.id.cmdExtraButtonSendToExec);
        consoleLayout = findViewById(R.id.consoleLayout);
        //foldersMenuLayout = findViewById(R.id.foldersmenulayout);
        //fodersMenuOkButton = findViewById(R.id.foldersmenu_okbutton);

        //rowButtonExtraMcCommand = findViewById(R.id.cmdExtraButtonCommand);
        //rowButtonExtraMcMessage = findViewById(R.id.cmdExtraButtonMessage);


//        rowButtonExtraColor.setOnClickListener();

        cmdScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long CLICK_DURATION = 100;


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        consoleClickTypeX1 = event.getX();
                        consoleClickTypeY1 = event.getY();
                        consoleClickTypeT1 = System.currentTimeMillis();
                        //System.out.println("return true");
                        return true;
                    case MotionEvent.ACTION_UP:
                        consoleClickTypeX2 = event.getX();
                        consoleClickTypeY2 = event.getY();
                        consoleClickTypeT2 = System.currentTimeMillis();

                        //System.out.println((x2-x1) + " " + (y2-y1));

                        if (Math.abs(consoleClickTypeX2 - consoleClickTypeX1)<=1 && Math.abs(consoleClickTypeY2 - consoleClickTypeY1)<=1 && (consoleClickTypeT2 - consoleClickTypeT1) < CLICK_DURATION) {
                            //Click
                            //System.out.println("Click");
                            doInputStuff();
                        } else if ((consoleClickTypeT2 - consoleClickTypeT1) >= CLICK_DURATION) {
                            //Long click
                            //System.out.println("Long click");
                        } else if (consoleClickTypeX1 > consoleClickTypeX2) {
                           //Left swipe
                            //System.out.println("Left swipe");
                        } else if (consoleClickTypeX2 > consoleClickTypeX1) {
                            //Right swipe
                            //System.out.println("Right swipe");
                        } else if (consoleClickTypeY1 > consoleClickTypeY2) {
                           //Down swipe
                            //System.out.println("Down swipe");
                        } else if (consoleClickTypeY2 > consoleClickTypeY1) {
                            //Up swipe
                            //System.out.println("Up swipe");
                            //System.out.println(cmdScrollView.getScrollY());
                        }


                        return true;
                }

                return false;
            }
        });

        cmdSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInput();
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), OtherActivity.class);
                startActivity(startIntent);
            }
        });

        scrollDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoscroll = true;
                fadingScrollButtonToAlphaValue = 0f;
                cmdScrollView.post(scrollDownRunnable);
                //scrollDownButton.setVisibility(View.INVISIBLE);
            }
        });

        /*rowButtonExtraMcCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRowButtonExtraMcCommandClicked = !isRowButtonExtraMcCommandClicked;
                if(isRowButtonExtraMcCommandClicked){
                    rowButtonExtraMcCommand.setTextColor(Color.parseColor("#17eb00"));
                }
                else{
                    rowButtonExtraMcCommand.setTextColor(Color.parseColor("#FFFFFF"));
                }
                //rowButtonExtraMcCommand.setBackgroundColor(Color.parseColor("#5e5e5e"));
            }
        });

        rowButtonExtraMcMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRowButtonExtraMcMessageClicked = !isRowButtonExtraMcMessageClicked;
                if(isRowButtonExtraMcMessageClicked){
                    rowButtonExtraMcMessage.setTextColor(Color.parseColor("#17eb00"));
                }
                else{
                    rowButtonExtraMcMessage.setTextColor(Color.parseColor("#FFFFFF"));
                }
                //rowButtonExtraMcCommand.setBackgroundColor(Color.parseColor("#5e5e5e"));
            }
        });*/

        rowButtonExtraCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRowButtonExtraCtrlClicked = !isRowButtonExtraCtrlClicked;
                if(isRowButtonExtraCtrlClicked){

                    //rowButtonExtraCtrl.setBackgroundTintList(contextInstance.getResources().getColorStateList(R.color.your_xml_name));
                    rowButtonExtraCtrl.setTextColor(Color.parseColor("#FF6200EE")); //without theme
                }
                else{
                    rowButtonExtraCtrl.setTextColor(Color.parseColor("#FFFFFF")); //without theme
                }
                //rowButtonExtraMcCommand.setBackgroundColor(Color.parseColor("#5e5e5e"));
            }
        });

        rowButtonExtraExecInput.setOnClickListener(view -> {
            isRowButtonExtraExecInputClicked = !isRowButtonExtraExecInputClicked;
            if(isRowButtonExtraExecInputClicked){

                //rowButtonExtraCtrl.setBackgroundTintList(contextInstance.getResources().getColorStateList(R.color.your_xml_name));
                rowButtonExtraExecInput.setTextColor(Color.parseColor("#FF6200EE")); //without theme
            }
            else{
                rowButtonExtraExecInput.setTextColor(getResources().getColor(R.color.maincoloratt1_specialbtns)); //without theme
            }
        });

        rowButtonExtraTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String s = "§";
                String s = "$system.getinfo.tasklist.request";
                int index = cmdInput.getSelectionStart();
                Editable editable = cmdInput.getText();
                editable.insert(index, s);
//                System.out.println(keyboardOpen);
//                System.out.println(cmdScrollView.getY());
//                System.out.println("heartBeats:" + References.heartBeatsNumber);
                //cmdInput.setText(cmdInput.getText().toString()+"§");

            }
        });
//        System.out.println(textView);

        /*rowButtonExtraSpecialSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "$";
                int index = cmdInput.getSelectionStart();
                Editable editable = cmdInput.getText();
                editable.insert(index, s);
            }
        });*/

        rowButtonExtraShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String s = "§";
                    String s = "$system.management.shutdown.usual.request";
                int index = cmdInput.getSelectionStart();
                Editable editable = cmdInput.getText();
                editable.insert(index, s);
//                System.out.println(keyboardOpen);
//                System.out.println(cmdScrollView.getY());
//                System.out.println("heartBeats:" + References.heartBeatsNumber);
                //cmdInput.setText(cmdInput.getText().toString()+"§");

            }
        });

        cmdClearInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable editable = cmdInput.getText();
                editable.clear();
                fadingClearInputButtonToAlphaValue = 0f;
            }
        });

        rowButtonExtraSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ConsoleActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.system_commands_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.item_tasklist:
                                References.sender.sendMessage("$system.getinfo.tasklist.request");
                                return true;
                            case R.id.item_shutdown_turnoff:
                                References.sender.sendMessage("$system.management.shutdown.usual.request");
                                return true;
                            case R.id.item_shutdown_restart:
                                References.sender.sendMessage("$system.management.shutdown.restart.request");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        rowButtonExtraSpecialSymbols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ConsoleActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.special_symbols_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.item_paragraph:
                                addTextToInput("§");
                                return true;
                            case R.id.item_dollar:
                                addTextToInput("$");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        rowButtonExtraFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ConsoleActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.files_actions_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.item_tolocation:
                                //References.socketSender.sendMessage("$system.files.cd");
                                //foldersMenuLayout.setVisibility(View.VISIBLE);
                                openFilesDialog();
                                //References.reloadFoldersFilesList();
                                return true;
                            case R.id.item_allfileslist:
                                References.sender.sendMessage("$system.files.fileslist.request");
                                return true;
                            case R.id.item_folderslist:
                                References.sender.sendMessage("$system.files.folderslist.request");
                                return true;
                            case R.id.item_nonfolderslist:
                                References.sender.sendMessage("$system.files.nonfolderslist.request");
                                return true;
                            case R.id.item_printLocation:
                                printCurrentLocation();
                                return true;
                            case R.id.item_folderinfo:
                                requestFolderInfo();
                                return true;
                            case R.id.item_execute:
                                openExecuteDialog();
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        //while (true) {
            //((TextView) findViewById(R.id.cmdOutputTextView)).setText(References.lastConsoleOutput);
        //}

        //Thread printer = new Thread(new Printer(textView));
        //printer.start();

        cmdInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("cmdInput start" + keyboardOpen);
                keyboardOpen = true;
                System.out.println("cmdInput end" + keyboardOpen);
            }
        });

        cmdInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                System.out.println("OnKeyListener - " + event.getAction());
                //System.out.println(event.getKeyCode() == KeyEvent.KEYCODE_C);
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    sendInput();
                    //focusOnInput();
                    /*if (cmdInput.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(cmdInput, InputMethodManager.SHOW_IMPLICIT);
                    }*/
                    cmdInput.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return true;
                }
                else if (isRowButtonExtraCtrlClicked && event.getAction() == KeyEvent.ACTION_DOWN && ((event.getKeyCode() == KeyEvent.KEYCODE_C) || (event.getKeyCode() == KeyEvent.KEYCODE_Q))) { //not always else if
                    stopRemoteExecution();
                    return true;
                }
                return false;
            }
        });

        cmdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //System.out.println(keyEvent.getAction());
                //System.out.println(keyEvent.getKeyCode() == KeyEvent.KEYCODE_C);
                return false;
            }
        });

        cmdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*System.out.println("onTextChanged - " + isRowButtonExtraCtrlClicked);
                if(charSequence.length()==1) {
                    System.out.println("onTextChanged - " + charSequence.charAt(0) == "c");
                    System.out.println("onTextChanged - " + charSequence.charAt(0) == "C");
                }*/
                if(isRowButtonExtraCtrlClicked && charSequence.length()==1 && (charSequence.charAt(0) == 'c' || charSequence.charAt(0) == 'q'
                || charSequence.charAt(0) == 'C' || charSequence.charAt(0) == 'Q')){
                    cmdInput.setText("");
                    stopRemoteExecution(); //later
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cmdInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        startRunnables();
    }

    private void startRunnables(){
        if(References.handler==null){
            References.handler = new Handler();
        }
//        if(!privacy1Done) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            privacy1Done = true;
//        }

        References.handler.postDelayed(loopPrinter, 50);
        References.handler.postDelayed(loopCleaner, 50);
        References.handler.postDelayed(liteLoopNoDelay, 0);
    }

    private Runnable liteLoopNoDelay = new Runnable() {
        @Override
        public void run() {
            if(cmdInput.getText().toString().equals("")){
                fadingClearInputButtonToAlphaValue = 0f;
            }
            else{
                fadingClearInputButtonToAlphaValue = 0.8f;
            }

            float lerpedOpacityScrollButton = (scrollDownButton.getAlpha() + fadingScrollButtonToAlphaValue) * fadingScrollButtonToAlphaAmplifier;
            float lerpedOpacityClearButton = (scrollDownButton.getAlpha() + fadingClearInputButtonToAlphaValue) * fadingClearInputToAlphaAmplifier;
            //System.out.println("(" + scrollDownButton.getAlpha() + " + " + fadingScrollButtonToAlphaValue + ") * " + fadingScrollButtonToAlphaAmplifier + " = " + lerpedOpacity);

            scrollDownButton.setAlpha(lerpedOpacityScrollButton);
            cmdClearInputButton.setAlpha(lerpedOpacityClearButton);

            //System.out.println(cmdClearInputButton.getAlpha());
            if(cmdClearInputButton.getAlpha()<0.2f){
                cmdClearInputButton.setVisibility(View.GONE);
            }
            else{
                if(!cmdInput.getText().toString().equals("")) {
                    cmdClearInputButton.setVisibility(View.VISIBLE);
                }
            }

            if(References.showSendInputToExecBtn){
                rowButtonExtraExecInput.setVisibility(View.VISIBLE);
            }
            else{
                rowButtonExtraExecInput.setVisibility(View.GONE);
                isRowButtonExtraExecInputClicked = false;
                rowButtonExtraExecInput.setTextColor(getResources().getColor(R.color.maincoloratt1_specialbtns));
                //Color.parseColor("#FF6200EE")
            }

            References.handler.postDelayed(liteLoopNoDelay, 1);
        }
    };

    private Runnable loopCleaner = new Runnable() {
        @Override
        public void run() {
            String current = References.lastConsoleOutput;
            String[] array = current.split("\n");
            //String[] newArray = new String[References.maxOutputLines];
            String newString = "";

            if(array.length > References.maxOutputLines){
                for (int i = (array.length- References.maxOutputLines); i < array.length; i++){
                    newString += array[i] + "\n";
                }
                References.lastConsoleOutput = newString;
            }

            //commented debug
            //System.out.println("text:" + cmdInput.getText() + "| " + cmdInput.getText().toString().equals(""));

            References.handler.postDelayed(loopCleaner, 1000);
        }
    };

    private Runnable loopPrinter = new Runnable(){
        int i = 0;
        int maxI = 10;
        @Override
        public void run() {
            if (exit == 0) {
                if (i >= maxI) {
                    //System.out.println(autoscroll + ", " + i);
                    if (cmdScrollView.getScrollY() >= (cmdScrollView.getChildAt(0).getHeight() - cmdScrollView.getHeight())) {
                        autoscroll = true;
                        fadingScrollButtonToAlphaValue = 0f;
                        //scrollDownButton.setVisibility(View.INVISIBLE);
                    } else {
                        autoscroll = false;
                        fadingScrollButtonToAlphaValue = 0.5f;
                        //scrollDownButton.setVisibility(View.VISIBLE);
                    }
                }
                String previousText = cmdText.getText().toString();
                cmdText.setText(References.lastConsoleOutput);
                if (!previousText.equals(References.lastConsoleOutput) && (autoscroll)) {
                    cmdScrollView.post(scrollDownRunnable);
                    //cmdScrollView.scrollTo(0, 9999999);
                }
                //System.out.println("Changed console text");
                if(References.printConnectionDetails) {
                    System.out.println("ConsoleActivity: connected = " + References.connected + "; disconnectedAlreadyExtraInfo = " + References.disconnectedAlreadyExtraInfo);
                }
                if (References.disconnectedAlreadyExtraInfo || !References.connected) {
                    connectionLostText.setVisibility(View.VISIBLE);
                } else {
                    connectionLostText.setVisibility(View.INVISIBLE);
                }

                //time = Calendar.getInstance().getTimeInMillis();

                i++;
                if (i > maxI) {
                    i = 0;
                }



                References.handler.postDelayed(loopPrinter, 10);
            }
        }
    };

    private Runnable scrollDownRunnable = new Runnable() {
        @Override
        public void run() {
            cmdScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    public void doInputStuff(){
        System.out.println("doInputStuff start " + keyboardOpen);
        focusOnInput();
        openKeyboard();

        keyboardOpen = !keyboardOpen;
//        System.out.println(cmdScrollView.getY());
        doKeyboardStuff();
        System.out.println("doInputStuff end " + keyboardOpen);
    }

    public void doKeyboardStuff(){
        if(true) {
            return;
        }
        //System.out.println("doKeyboardStuff start " + keyboardOpen);
        //System.out.println(cmdScrollView.getHeight());
        if(keyboardOpen){
            References.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cmdScrollView.setY(880);

                }
            }, 250);
        }
        else{
            cmdScrollView.setY(0);
        }
        //System.out.println("doKeyboardStuff end " + keyboardOpen);
    }

    public void openKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        //keyboardOpen = true;
    }

    public void closeKeyboard(){
        //keyboardOpen = false;
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cmdInput.getWindowToken(), 0);
    }

    public void focusOnInput(){
        cmdInput.requestFocus();
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed start " + keyboardOpen);
        if(keyboardOpen){
            keyboardOpen = false;
        }
        else{
            exit = 1;
        }
        doKeyboardStuff();
        System.out.println("onBackPressed end " + keyboardOpen);
        super.onBackPressed();
    }

    private void addTextToInput(String text){
        int index = cmdInput.getSelectionStart();
        Editable editable = cmdInput.getText();
        editable.insert(index, text);
    }

    private void openFilesDialog(){
        FilesDialog filesDialog = new FilesDialog();
        filesDialog.show(getSupportFragmentManager(), "Dialog");
    }

    private void openExecuteDialog(){
        ExecuteDialog executeDialog = new ExecuteDialog();
        executeDialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void applyDataFiles(String folder, String fullPath) {

        //String s = "asd\\asdsdf\\adfsd/sdfsd";
        //System.out.println(s.replace("\\", "/"));

        fullPath.replace("\\", "/");
        folder.replace("\\", "/");

        fullPath = fullPath.trim();
        folder = folder.trim();

        References.sender.sendMessage("$system.files.changelocation.request.new=" + fullPath);

        /*if(fullPath.startsWith(References.currentFolder)) {
//            String lastFolder = folder;

//            String[] pathParts = fullPath.split(References.systemSeparator + References.systemSeparator);

//            if(lastFolder.equals("")) {
//                lastFolder = pathParts[pathParts.length - 1];
//            }

            //References.currentFolder = fullPath; // += References.systemSeparator + fullPath; // maybe uncomment
            References.socketSender.sendMessage("$system.files.changelocation.request.new=" + fullPath);

        }
        else{
            //References.currentFolder = fullPath; // += References.systemSeparator + fullPath;
            References.socketSender.sendMessage("$system.files.changelocation.request.new=" + References.fullPath);
        }*/
        //if(folder.)
        //References.
        /*if(fullPath!=null && !fullPath.equals("")){
            if(folder!=null && !folder.equals("")){
                References.lastConsoleOutput += "Using full path";
            }
        }
        else if(folder!=null && !folder.equals("")){

        }
        //what if both?
        else{
            References.lastConsoleOutput += "Enter the folder name OR path, you've done something wrong";
        }*/
    }

    @Override
    public void applyDataExecute(String file) {
        References.sender.sendMessage("$system.files.executefile.request.file=" + file);
        References.lastConsoleOutput += "Sending request to start \"" + file + "\"\n";
        /*boolean contains = false;
        for (int i = 0; i < References.nonFoldersList.length; i++) {
            System.out.println("File: " + References.nonFoldersList[i]);
            if (References.nonFoldersList[i].equals(file)) {
                contains = true;
            }
        }

        if (contains) {
            References.socketSender.sendMessage("$system.files.executefile.request.file=" + file);
        } else {
            References.lastConsoleOutput += "File \"" + file + "\" doesn't exit!\n\n"; //idk why another one is working but ok fine let it be
        }*/
        //References.reloadFoldersFilesList();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                sendInput();
                //newInputFocus();
                cmdInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true;
                //if (event.isShiftPressed()) {
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void sendInput() {
        String text = cmdInput.getText().toString();
        text = text.trim();
        if (!text.equals("")) {
            String consoleText = "> " + text;
            String secondaryText = "";
            cmdInput.setText("");
            boolean actionDone = false;

            if (text.equals("clear")) {
                References.lastConsoleOutput = "";
            } else {

                if (text.toLowerCase(Locale.ROOT).startsWith("send")) {
                    consoleText = "[Sending \"" + text.substring(5) + "\" to the PC]";
                    actionDone = true;
                }

                if (isRowButtonExtraMcMessageClicked) {
                    References.sender.sendMessage("$mcmessage.normal.text=" + text);
                    consoleText = "Sending to the MC server: " + consoleText;
                    actionDone = true;
                } else if (isRowButtonExtraMcCommandClicked) {
                    References.sender.sendMessage("$mccommand.normal.text=" + text);
                    consoleText = "Executing at the MC server: " + consoleText;
                    actionDone = true;
                } else if (isRowButtonExtraExecInputClicked) {
                    References.sender.sendMessage("$system.execution.input=" + text);
                    consoleText = "[INPUT] " + text;
                    actionDone = true;
                } else if (text.startsWith("$")) { //in-app commands
                    References.sender.sendMessage("" + text);
                    if (text.equals("$system.getinfo.tasklist.request")) {
                        consoleText = "> [Request to the system to display the task list]";
                    } else if (text.equals("$system.management.shutdown.usual.request")) {
                        consoleText = "> [Request to the system to SHUTDOWN]";
                    }
                    actionDone = true;
                }
                //else if(text.equals("hide")){
                //    rowButtonExtraCtrl.setVisibility(View.GONE);
                //}
                else {
                    if (text.split(" ")[0].toLowerCase(Locale.ROOT).equals("send")) {
                        References.sender.sendMessage("$rscmessage.normal.text=" + text.substring(5));
                        //consoleText = "Sending to RSC desktop app: " + consoleText;
                        actionDone = true;
                    }
                    else if (text.equalsIgnoreCase("pwd") || text.equalsIgnoreCase("cd")) {
                        References.lastConsoleOutput += consoleText + "\n";
                        consoleText = "";
                        printCurrentLocation();
                        actionDone = true;
                    }
                    else if (text.toLowerCase(Locale.ROOT).equals("stop size")){
                        References.sender.sendMessage("$system.files.dirinfo.stopCalculatingSize");
                        consoleText = "Stopping the sizes calculation";
                    }
                    else if (text.toLowerCase(Locale.ROOT).startsWith("stop") || text.toLowerCase(Locale.ROOT).startsWith("exit") || text.toLowerCase(Locale.ROOT).startsWith("q")) {
                        References.lastConsoleOutput += consoleText + "\n";
                        consoleText = "";
                        stopRemoteExecution();
                        actionDone = true;
                    }
                    else if (text.toLowerCase(Locale.ROOT).startsWith("cd ")) {
                        References.lastConsoleOutput += consoleText + "\n";
                        consoleText = "";
                        if(text.equalsIgnoreCase("cd ..")){
                            //if(References.currentFolder.replaceAll("\\\\+", "/").split("/").length<2){
                            //    secondaryText = "You can't use it in main directory.\n";
                            //}
                            //else {
                                References.sender.sendMessage("$system.files.changelocation.request.up");
                                /*String s = References.currentFolder.replaceAll("\\\\+", "/").substring(0, References.currentFolder.length() -
                                                (References.currentFolder.split("/")[(References.currentFolder.split("/")).length - 1]).length());
                                //String s = References.currentFolder.substring(0, References.currentFolder.length() - (References.currentFolder.
                                        //split(References.systemSeparator + References.systemSeparator)[(References.currentFolder.split(References.systemSeparator + References.systemSeparator)).length - 1]).length());
                                References.socketSender.sendMessage("$system.files.changelocation.request.new=" + s);*/
                            //}
                        }
                        else if(text.equalsIgnoreCase("cd .")){
                            secondaryText = "Use \"cd ..\" to go to the upper folder.\n";
                        }
                        else {
                            if (text.contains("/") || text.contains("\\")) {
                                References.sender.sendMessage("$system.files.changelocation.request.new=" + text.substring(3)); // tolowercase
                            } else {
                                String[] text1 = text.split("/");
                                String[] text2 = text.split("\\\\");
                                boolean b1 = (((text1.length==1) && text1[0].endsWith(":")) || ((text2.length==1) && text2[0].endsWith(":")));
                                if(text.contains("/") || text.contains("\\") || b1){
                                    if(b1){
                                        text+=References.systemSeparator;
                                    }
                                    References.sender.sendMessage("$system.files.changelocation.request.new=" + text.substring(3));
                                }
                                else {
                                    String maybeSlash = "";
                                    if (!References.currentFolder.endsWith("\\") && !References.currentFolder.endsWith("/")) {
                                        maybeSlash = References.systemSeparator;
                                    }
                                    References.sender.sendMessage("$system.files.changelocation.request.new=" + (References.currentFolder + maybeSlash + text.substring(3)));
                                }
                            }
                        }
                        actionDone = true;
                    }
                    else if (text.toLowerCase(Locale.ROOT).startsWith("start ") || text.toLowerCase(Locale.ROOT).startsWith("exec ") || text.toLowerCase(Locale.ROOT).  startsWith("run ")) {
                        References.lastConsoleOutput += consoleText + "\n";
                        consoleText = "";
                        String whatToStart = text.substring((text.split(" ")[0]).length()+1);
                        References.sender.sendMessage("$system.files.executefile.request.file=" + whatToStart);
                        actionDone = true;
                    }
                    else if (text.equalsIgnoreCase("ls") || text.equalsIgnoreCase("dir")) {
                        References.lastConsoleOutput += consoleText + "\n";
                        consoleText = "";
                        References.sender.sendMessage("$system.files.fileslist.request");
                        actionDone = true;
                    }
                    else {
                        References.sender.sendMessage("$rsccommand.normal.text=" + text);
                    }
                }
                if(!consoleText.trim().equals("")) {
                    References.lastConsoleOutput += consoleText + "\n";
                }
                if(!secondaryText.trim().equals("")) {
                    References.lastConsoleOutput += secondaryText + "\n";
                }
                /*if(actionDone=false && secondaryText.trim().equals("")){ // Idk how it was gonna work so nvm
                    References.lastConsoleOutput += "" + "\n";
                }*/
            }
            //References.socketSender.sendMessage(text);
        }
    }

    public static void printCurrentLocation(){
        References.lastConsoleOutput += References.currentFolder + "\n\n";
    }

    public static void stopRemoteExecution(){
        References.sender.sendMessage("$system.execution.stop.request");
    }

//    public static void newInputFocus(){
//        int index = cmdInput.getSelectionStart();
//        Editable editable = cmdInput.getText();
//        editable.insert(index, text);
        //cmdInput.setSelection(0);

//        cmdInput.requestFocus();
//        InputMethodManager imm = (InputMethodManager) get getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }

    public void requestFolderInfo(){
        References.sender.sendMessage("$system.files.dirinfo.request");
    }
}