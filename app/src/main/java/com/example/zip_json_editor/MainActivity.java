package com.example.zip_json_editor;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
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
            // Creazione istanza Gson
            Gson gson = new Gson();

            try {
                // Ottenimento del percorso assoluto della directory dei file dell'applicazione
                String path = getFilesDir().getAbsolutePath();

                // Apertura del file ZIP e lettura dei contenuti
                InputStream inputStream = getContentResolver().openInputStream(uri);
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    new DebugString("FileName: " + fileName, debugConsole);
                    if (fileName.endsWith(".json")) {
                        // Lettura del file JSON e modifica del contenuto
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                        String jsonString = outputStream.toString("UTF-8");
                        new DebugString("JSONString: " + jsonString, debugConsole);
                        Log.d(TAG, jsonString);

                        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                        new DebugString("JSONObj: " + jsonObject.toString(), debugConsole);

                        String imageUrl = jsonObject.get("image").getAsString();
                        new DebugString("ImageURL: " + imageUrl, debugConsole);

                        String newImageUrl = imageUrl.replace("CAMBIAMI", "nuovaStringa");
                        new DebugString("NewImageURL: " + newImageUrl, debugConsole);

                        jsonObject.addProperty("image", newImageUrl);
                        new DebugString("NewJSONObj: " + jsonObject.toString(), debugConsole);

                        String newJsonString = gson.newBuilder().setPrettyPrinting().create().toJson(jsonObject);
                        new DebugString("NewJSONString: " + newJsonString, debugConsole);

                        // Sovrascrittura del file JSON con il nuovo contenuto
                        // FileOutputStream fileOutputStream = new FileOutputStream(fileName);

//                        FileOutputStream fileOutputStream = new FileOutputStream(new File(path, fileName));
//                        new DebugString("FileOutputStream: " + fileOutputStream.toString(), debugConsole);
//
//                        fileOutputStream.write(newJsonString.getBytes());
//                        new DebugString("FileOutputStream: " + fileOutputStream.toString(), debugConsole);
//
//                        fileOutputStream.close();
//                        new DebugString("FileOutputStream: " + fileOutputStream.toString(), debugConsole);

                        File outputFile = new File(getFilesDir(), fileName.substring("metadata/".length()));
                        FileOutputStream output = new FileOutputStream(outputFile);
                        output.write(newJsonString.getBytes());


                    }
//                    zipInputStream.closeEntry();
//                    new DebugString("ZipInputStream: " + zipInputStream.toString(), debugConsole);

                }
                zipInputStream.close();
                new DebugString("ZipInputStream: " + zipInputStream.toString(), debugConsole);

                inputStream.close();
                new DebugString("InputStream: " + inputStream.toString(), debugConsole);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

}