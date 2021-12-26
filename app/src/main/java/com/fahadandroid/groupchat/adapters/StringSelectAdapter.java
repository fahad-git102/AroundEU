package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;

import java.util.List;

public class StringSelectAdapter extends RecyclerView.Adapter<StringSelectAdapter.StringHolder> {

    List<String> stringList;
    Context context;

    public StringSelectAdapter(List<String> stringList, Context context) {
        this.stringList = stringList;
        this.context = context;
    }

    @NonNull
    @Override
    public StringHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.string_list_item, parent, false);
        return new StringHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringHolder holder, int position) {
        holder.text.setText(stringList.get(position));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class StringHolder extends RecyclerView.ViewHolder{

        TextView text;

        public StringHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
