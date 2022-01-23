package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;

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
        holder.tvFileName.setText("File "+i);
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
