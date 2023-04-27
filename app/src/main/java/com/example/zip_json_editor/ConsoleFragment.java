package com.example.zip_json_editor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ConsoleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Infla il layout XML del Fragment
        View view = inflater.inflate(R.layout.activity_main, container, false);

        // Trova la TextView all'interno del layout XML del Fragment
        TextView textView = view.findViewById(R.id.dbgConsole);

        // Eventualmente, impostare il testo della TextView o altre proprietà della TextView
        textView.setText("Questo è un altro testo");

        return view;
    }
}

