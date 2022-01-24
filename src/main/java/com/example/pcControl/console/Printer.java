package com.example.pcControl.console;

import android.widget.TextView;

public class Printer implements Runnable {
    private TextView textView;

    public Printer (TextView view){
        this.textView = view;
    }

    @Override
    public void run(){
        while(true){
            //textView.setText(References.lastConsoleOutput);

        }
    }
}
