package com.example.zip_json_editor;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    TextView debugConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugConsole = findViewById(R.id.dbgConsole);
        debugConsole.setText("Debug Console");
        debugConsole.setMovementMethod(new android.text.method.ScrollingMovementMethod());


        Button browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to open file picker and select ZIP archive
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/zip");
                startActivityForResult(Intent.createChooser(intent, "Select ZIP archive"), 1);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            new DebugString("URI: " + uri.toString(), debugConsole);
            // Code to handle the selected ZIP archive
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    if (fileName.endsWith(".json")) {
                        // Code to read the JSON file
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                        String jsonString = outputStream.toString("UTF-8");
                        // Do something with the JSON string
                        new DebugString("JSONString: " + jsonString, debugConsole);
                        Log.d(TAG, jsonString);
                    }
                    zipInputStream.closeEntry();
                }
                zipInputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}