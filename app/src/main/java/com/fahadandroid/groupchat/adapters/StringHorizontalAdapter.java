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

public class StringHorizontalAdapter extends RecyclerView.Adapter<StringHorizontalAdapter.SHolder> {

    List<String> stringList;
    Context context;

    public StringHorizontalAdapter(List<String> stringList, Context context) {
        this.stringList = stringList;
        this.context = context;
    }

    @NonNull
    @Override
    public SHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.string_horizontals_item, parent, false);
        return new SHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SHolder holder, int position) {
        if (position<stringList.size()-1){
            holder.tvText.setText(stringList.get(position)+",");
        }else {
            holder.tvText.setText(stringList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class SHolder extends RecyclerView.ViewHolder{

        TextView tvText;

        public SHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }
}
