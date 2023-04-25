package com.example.zip_json_editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private File[] mFiles;

    public FileAdapter(File[] files) {
        mFiles = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFileName.setText(mFiles[position].getName());
    }

    @Override
    public int getItemCount() {
        return mFiles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFileName;

        public ViewHolder(View itemView) {
            super(itemView);
            mFileName = itemView.findViewById(R.id.fileName);
        }
    }
}
