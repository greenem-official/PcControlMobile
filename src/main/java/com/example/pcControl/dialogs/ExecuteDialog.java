package com.example.pcControl.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

public class ExecuteDialog extends AppCompatDialogFragment {
    private AutoCompleteTextView fileEditText;

    private ExecuteDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExecuteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExecuteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //References.socketSender.sendMessage("$system.files.fileslist.request.silent=true");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_execute, null);

        builder.setView(view)
                .setTitle("Execute")
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    String file = "";
                    if (fileEditText.getText() != null) {
                        file = fileEditText.getText().toString();
                    }
                    listener.applyDataExecute(file);
                });

        fileEditText = view.findViewById(R.id.edit_executedialog_filename);

        final ArrayList<String> variants = new ArrayList<>();
        for (String s : References.nonFoldersList){
            if(s.endsWith(".bat")
            || s.endsWith(".exe")
            || s.endsWith(".jar")
            || s.endsWith(".msi")
            || s.endsWith(".command")
            || s.endsWith(".cmd")
            || s.endsWith(".app")
            || s.endsWith(".run")) {
                variants.add(s);
            }
            //.cmd
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, variants);
        fileEditText.setThreshold(1);//will start working from first character
        fileEditText.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        //fileEditText.setTextColor(Color.);


        //fileEditText.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, variants));
        //System.out.println(References.);

        /*AutoCompleteTextView actv1 = (AutoCompleteTextView) view.findViewById(R.id.edit_executedialog_filename);
        actv1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        builder.setView(view);*/


        return builder.create();
    }

    public interface ExecuteDialogListener {
        void applyDataExecute(String file);
    }
}
