package com.example.zip_json_editor;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipTask implements Runnable{

    private Context context;
    private Intent data;
    private TextView debugConsole;
    private Gson gson;

    public ZipTask(Context context, Intent data, TextView debugConsole)
    {
        this.context = context;
        this.data = data;
        this.debugConsole = debugConsole;
        this.gson = new Gson();
    }

    @Override
    public void run()
    {
        Uri uri = data.getData();
        String outputfile = getOutputfileName(uri);
//        String outputfile = "output.zip";
        new DebugString("URI: " + uri.toString(), debugConsole);
        new DebugString("Last Path Segment: " + outputfile, debugConsole);

        try
        {
            String path = context.getFilesDir().getAbsolutePath();

            String filePath = path + File.separator + outputfile;
            if (isFileExist(filePath))
            {
                new DebugString("File already exists", debugConsole);
            }
            else
            {
                PerformTheOperation(uri, path, outputfile);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String getOutputfileName(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        String outputfile = new String();
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst())
        {
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            outputfile = cursor.getString(nameIndex);
        }
        cursor.close();
        return outputfile;
    }

    private void PerformTheOperation(Uri uri, String path, String outputfile) throws IOException
    {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(path, outputfile));
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry;

        while ((zipEntry = zipInputStream.getNextEntry()) != null)
        {
            String fileName = zipEntry.getName();
            if (fileName.endsWith(".json"))
            {
                String jsonString = readJsonStringFromZip(zipInputStream);
                Log.d(TAG, jsonString);
                JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                String newJsonString = modifyJsonString(jsonObject);
                addEntryToZip(zipOutputStream, fileName, newJsonString);
            }
            zipInputStream.closeEntry();
        }

        zipInputStream.close();
        inputStream.close();
        zipOutputStream.close();
        fileOutputStream.close();
    }

    private boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private String readJsonStringFromZip(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString("UTF-8");
    }

    private String modifyJsonString(JsonObject jsonObject) {
        String imageUrl = jsonObject.get("image").getAsString();
        String newImageUrl = imageUrl.replace("CAMBIAMI", "nuovaStringa");
        jsonObject.addProperty("image", newImageUrl);
        return gson.newBuilder().setPrettyPrinting().create().toJson(jsonObject);
    }

    private void addEntryToZip(ZipOutputStream zipOutputStream, String fileName, String newJsonString) throws IOException {
        ZipEntry newZipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(newZipEntry);
        zipOutputStream.write(newJsonString.getBytes());
        zipOutputStream.closeEntry();
    }
}