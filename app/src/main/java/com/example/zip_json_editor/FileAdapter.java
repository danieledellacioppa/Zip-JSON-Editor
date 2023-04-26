package com.example.zip_json_editor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private File[] mFiles;
    private Context context;
    private File path;
    public FileAdapter(File[] files,Context context,File path) {
        mFiles = files;
        this.context = context;
        this.path = path;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.mFileName.setText(mFiles[position].getName());
        String fileName = mFiles[position].getName();
        int localPosition= holder.getAdapterPosition();
        holder.mFileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creazione dell'Intent per condividere il file tramite WhatsApp
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/zip");
                File file = new File(context.getFilesDir(), fileName);
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
//                Uri fileUri = Uri.fromFile(new File(path, "output.zip"));
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setPackage("com.whatsapp");
                context.startActivity(Intent.createChooser(shareIntent, "Condividi file"));
            }
        });


        holder.mFileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = mFiles[localPosition];
                if (file.exists()) {
                    file.delete();
                    // Aggiorna la RecyclerView dopo la cancellazione del file
                    notifyItemRemoved(localPosition);
                    notifyItemRangeChanged(localPosition, mFiles.length);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mFileName;
        public ImageView mFileImage;
        public ImageView mFileDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            mFileName = itemView.findViewById(R.id.fileName);
            mFileImage = itemView.findViewById(R.id.fileIcon);
            mFileDelete = itemView.findViewById(R.id.deleteBTN);
        }

    }
}
