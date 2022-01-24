package com.example.pcControl.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pcControl.R;
import com.example.pcControl.data.References;

import java.util.ArrayList;

public class FilesDialog extends AppCompatDialogFragment {
    private AutoCompleteTextView inCurrent;
    private AutoCompleteTextView fromPath;

    private FilesDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (FilesDialogListener) context;
        } catch (ClassCastException e) {
            //e.printStackTrace();
            throw new ClassCastException(context.toString() + "must implement FilesDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //References.socketSender.sendMessage("$system.files.fileslist.request.silent=true");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_paths, null);

        builder.setView(view)
                .setTitle("Change location")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fullPath = "";
                        String folder = "";
                        if (inCurrent.getText() != null) {
                            folder = inCurrent.getText().toString();
                        }
                        if(fromPath.getText()!=null){
                            fullPath = fromPath.getText().toString();
                        }
                        /*if(folder.equals("") || fullPath.equals(GeneralData.currentFolder)){ // does not work
                            dialogInterface.dismiss();
                        }*/

                        listener.applyDataFiles(folder, fullPath);
                    }
                });

        inCurrent = view.findViewById(R.id.edit_filesdialog_openincurrent);
        fromPath = view.findViewById(R.id.edit_filesdialog_openpath);

        System.out.println(References.currentFolder);
        fromPath.setText(References.currentFolder);

        inCurrent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!fromPath.isFocused()) {
                    if(!References.currentFolder.replace("\\", "/").endsWith("/")){
                        References.currentFolder += References.systemSeparator;
                    }
                    fromPath.setText(References.currentFolder + inCurrent.getText().toString());
                }
            }
        });

        fromPath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // files/mystuff   files/myst
                if(!fromPath.getText().toString().startsWith(References.currentFolder)){
                    inCurrent.setText("");
                }
            }
        });

        final String[] variants = References.foldersList;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, variants);
        inCurrent.setThreshold(1);//will start working from first character
        inCurrent.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        //fileEditText.setTextColor(Color.);

        //fromPath.setText(GeneralData.currentFolder + GeneralData.systemSeparator);

        return builder.create();
    }

    public interface FilesDialogListener {
        void applyDataFiles(String folder, String fullPath);
    }

    /*private void checkButtonEnabledState(){
        if(!fromPath.getText().toString().equals(GeneralData.currentFolder)){

        }
        if(inCurrent.getText().toString().equals("")){

        }
    }*/
}
