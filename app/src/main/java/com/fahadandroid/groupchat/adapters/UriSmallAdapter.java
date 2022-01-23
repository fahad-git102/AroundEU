package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;

import org.w3c.dom.Text;

import java.util.List;

public class UriSmallAdapter extends RecyclerView.Adapter<UriSmallAdapter.USHolder> {

    List<Uri> uriList;
    Context context;

    public UriSmallAdapter(List<Uri> uriList, Context context) {
        this.uriList = uriList;
        this.context = context;
    }

    @NonNull
    @Override
    public USHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_file_item, parent, false);
        return new USHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull USHolder holder, int position) {
        int i = position+1;
        holder.fileName.setText("File "+ i);
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public class USHolder extends RecyclerView.ViewHolder{
        ImageButton btnCancel;
        TextView fileName;
        public USHolder(@NonNull View itemView) {
            super(itemView);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }
}
