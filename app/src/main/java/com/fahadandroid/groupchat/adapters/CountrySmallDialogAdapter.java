package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.CountryModel;

import java.util.List;

public class CountrySmallDialogAdapter extends RecyclerView.Adapter<CountrySmallDialogAdapter.CHolder> {

    List<CountryModel> countries;
    Context context;

    public CountrySmallDialogAdapter(List<CountryModel> countries, Context context) {
        this.countries = countries;
        this.context = context;
    }

    @NonNull
    @Override
    public CHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.countries_small_item, parent, false);
        return new CHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CHolder holder, int position) {
        holder.tvName.setText(countries.get(position).getCountryName());
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    class CHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        public CHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
