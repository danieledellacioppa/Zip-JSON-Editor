package com.example.zip_json_editor;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {

    TextView debugConsole;
    MyHandlerThread worker;
    String labeltext;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextInputEditText textInputEditText = findViewById(R.id.newLinkLabel);
        Button myButton = findViewById(R.id.my_button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
            }
        });

        worker = new MyHandlerThread();


        // Trova il Fragment utilizzando un metodo del FragmentManager
        ConsoleFragment myFragment = (ConsoleFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main);


        debugConsole = myFragment.getView().findViewById(R.id.dbgConsole);
        debugConsole.setText("Debug Console");
        debugConsole.setMovementMethod(new android.text.method.ScrollingMovementMethod());


        Button browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to open file picker and select ZIP archive
                labeltext = textInputEditText.getText().toString();

                if(labeltext.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter a label", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/zip");
                    startActivityForResult(Intent.createChooser(intent, "Select ZIP archive"), 1);
                }
            }
        });

        // Imposta lo sfondo come immagine presente nella cartella "drawable"
        ConstraintLayout mainLayout = findViewById(R.id.activity_main);
        mainLayout.setBackgroundResource(R.drawable.wall);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            ZipTask zipTask = new ZipTask(this, data, debugConsole,labeltext);
            worker.execute(zipTask);

        }
    }

}