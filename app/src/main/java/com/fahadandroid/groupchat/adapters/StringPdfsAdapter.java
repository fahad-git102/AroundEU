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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class StringPdfsAdapter extends RecyclerView.Adapter<StringPdfsAdapter.SPHolder> {

    List<String> stringList;
    Context context;

    public StringPdfsAdapter(List<String> stringList, Context context) {
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
        int i = position+1;
        try{
            getFileNameFromUrl(stringList.get(position), holder.tvFileName);

        }catch (Exception e){
            holder.tvFileName.setText("File "+i);
        }

    }

    public void getFileNameFromUrl(String url, TextView tvName) {

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        // Get reference to the file
        storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String filename = storageMetadata.getName();
                tvName.setText(filename);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.i("Faillll", "onFailure: "+exception.toString());
            }
        });
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
