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
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {

    TextView debugConsole;
    MyHandlerThread worker;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButton = findViewById(R.id.my_button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
            }
        });

        worker = new MyHandlerThread();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Uri del file ZIP selezionato dall'utente
                    Uri uri = data.getData();
                    new DebugString("URI: " + uri.toString(), debugConsole);

                    // Creazione istanza Gson
                    Gson gson = new Gson();

                    try {
                        // Ottenimento del percorso assoluto della directory dei file dell'applicazione
                        String path = getFilesDir().getAbsolutePath();

                        // Apertura del file ZIP in scrittura
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(path, "output.zip"));
                        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

                        // Apertura del file ZIP e lettura dei contenuti
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        ZipEntry zipEntry;
                        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                            String fileName = zipEntry.getName();
                            if (fileName.endsWith(".json")) {
                                // Lettura del file JSON e modifica del contenuto
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = zipInputStream.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, length);
                                }
                                String jsonString = outputStream.toString("UTF-8");
                                Log.d(TAG, jsonString);

                                JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                                String imageUrl = jsonObject.get("image").getAsString();
                                String newImageUrl = imageUrl.replace("CAMBIAMI", "nuovaStringa");
                                jsonObject.addProperty("image", newImageUrl);
                                String newJsonString = gson.newBuilder().setPrettyPrinting().create().toJson(jsonObject);

                                // Scrittura del file JSON modificato nell'archivio ZIP
                                ZipEntry newZipEntry = new ZipEntry(fileName);
                                zipOutputStream.putNextEntry(newZipEntry);
                                zipOutputStream.write(newJsonString.getBytes());
                                zipOutputStream.closeEntry();
                            }
                            zipInputStream.closeEntry();
                        }
                        zipInputStream.close();
                        inputStream.close();

                        // Chiusura dell'archivio ZIP
                        zipOutputStream.close();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

// Esecuzione del runnable in un thread separato
            worker.execute(runnable);


        }
    }

}