package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.FileUrl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringPdfsAdapter extends RecyclerView.Adapter<StringPdfsAdapter.SPHolder> {

    List<FileUrl> stringList;
    Context context;

    public StringPdfsAdapter(List<FileUrl> stringList, Context context) {
        this.stringList = stringList;
        this.context = context;
    }

    @NonNull
    @Override
    public SPHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_file_item, parent, false);
        return new SPHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SPHolder holder, int position) {
        holder.btnCancel.setVisibility(View.GONE);
        try{
            if (stringList!=null){
                holder.tvFileName.setText(stringList.get(position).getName());
            }else {
                holder.tvFileName.setText("File "+position);
            }

        }catch (Exception e){
            holder.tvFileName.setText("File "+position);
        }

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class SPHolder extends RecyclerView.ViewHolder{

        ImageButton btnCancel ;
        TextView tvFileName;

        public SPHolder(@NonNull View itemView) {
            super(itemView);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            tvFileName = itemView.findViewById(R.id.fileName);
        }
    }
}
